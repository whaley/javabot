package javabot.javadoc

import javabot.JavabotConfig
import javabot.JavabotThreadFactory
import javabot.dao.ApiDao
import javabot.dao.JavadocClassDao
import org.bson.types.ObjectId
import org.jboss.forge.roaster.Roaster
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintStream
import java.io.Writer
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import javax.tools.DocumentationTool
import javax.tools.ToolProvider

@Singleton
class JavadocParser @Inject constructor(val apiDao: ApiDao, val javadocClassDao: JavadocClassDao,
                                        val provider: Provider<JavadocClassParser>, val config: JavabotConfig) {

    fun parse(api: JavadocApi, location: File, writer: Writer) {
        try {
            val tmpDir = File(".tmp")
            if (!tmpDir.exists()) {
                File(System.getProperty("java.io.tmpdir"))
            }

            val workQueue = LinkedBlockingQueue<Runnable>()
            val executor = ThreadPoolExecutor(20, 30, 30, TimeUnit.SECONDS, workQueue,
                    JavabotThreadFactory(false, "javadoc-thread-"))
            executor.prestartCoreThread()
            var packages = mutableListOf<String>()
            if ("JDK" == api.name) {
                packages = mutableListOf("java", "javax")
            }
            packages = packages
                    .map { it.replace('.', '/') + "/" }
                    .toMutableList()
            try {
                JarFile(location).use { jarFile ->
                    val entries = jarFile.entries()
                    entries.iterator()
                            .forEach { entry ->
                                if (entry.name.endsWith(".java")
                                        && (packages.isEmpty() || packages.any { entry.name.startsWith(it) })) {
                                    if (!workQueue.offer(JavadocClassReader(api, jarFile, entry), 1, TimeUnit.MINUTES)) {
                                        writer.write("Failed to class to queue: " + entry)
                                    }
                                }
                            }
                }
                while (!workQueue.isEmpty()) {
                    writer.write("Waiting on %s work queue to drain.  %d items left".format(api.name, workQueue.size))
                    Thread.sleep(5000)
                }
                buildHtml(api, location, packages)
                executor.shutdown()
                executor.awaitTermination(1, TimeUnit.HOURS)
            } finally {
/*
                if (deleteFile) {
                    location.delete()
                }
*/
//                target?.deleteRecursively()
            }
            writer.write("Finished importing %s.  %s!".format(api.name, if (workQueue.isEmpty()) "SUCCESS" else "FAILURE"))
        } catch (e: IOException) {
            log.error(e.message, e)
            throw RuntimeException(e.message, e)
        } catch (e: InterruptedException) {
            log.error(e.message, e)
            throw RuntimeException(e.message, e)
        }

    }

    fun buildHtml(api: JavadocApi, file: File, packages: List<String>) {
        val host = config.databaseHost()
        val port = config.databasePort()
        val database = config.databaseName()
        val uri = URI("gridfs://$host:$port/$database/javadoc/${api.name}")
        val targetDir = Paths.get(uri)
        val javadocDir = buildJavadocHtml(packages, file)
        val javadocPath = javadocDir.toPath()
        try {
            javadocDir.walk()
                    .filter { !it.isDirectory }
                    .forEach {
                        val source = Paths.get(it.absolutePath)
                        val target = targetDir.resolve(javadocPath.relativize(source).toString())
                        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
                    }
        } finally {
            javadocDir.deleteRecursively()
        }
    }

    private fun buildJavadocHtml(packages: List<String>, jar: File): File {
        var tmp = File("/tmp/")
        if (!tmp.exists()) {
            tmp = File(System.getProperty("java.io.tmpdir"))
        }
        val jarTarget = File(tmp, ObjectId().toString())
        val javadocDir = File(tmp, "javadoc" + ObjectId())
        try {
            extractJar(jar, jarTarget, packages)
            ToolProvider.getSystemDocumentationTool().run(null, PrintStream(ByteArrayOutputStream()), PrintStream(ByteArrayOutputStream()),
                    "-d", javadocDir.absolutePath,
                    "-subpackages", packages.joinToString(":"),
                    "-protected",
                    "-use",
                    "-sourcepath", jarTarget.absolutePath);
        } finally {
            jarTarget.deleteRecursively()
        }
        return javadocDir
    }

    private fun extractJar(jar: File, jarTarget: File, packages: List<String>) {
        jarTarget.mkdirs()
        val jarFile = JarFile(jar)
        jarFile.entries().iterator().forEach { entry ->
            if (entry.name.endsWith(".java") && (packages.isEmpty() || packages.any { entry.name.startsWith(it) })) {
                val javaFile = File(jarTarget, entry.name)
                javaFile.parentFile.mkdirs()
                FileOutputStream(javaFile).use {
                    jarFile.getInputStream(entry).copyTo(it)
                }
            }
        }
    }

    fun getJavadocClass(api: JavadocApi, fqcn: String): JavadocClass {
        val pkgName = getPackage(fqcn)
        val parentName = fqcn.split('.').last()
        return getJavadocClass(api, pkgName, parentName)
    }

    fun getJavadocClass(api: JavadocApi, pkg: String, name: String): JavadocClass {
        var javadocClass = javadocClassDao.getClass(api, pkg, name)
        if (javadocClass == null) {
            javadocClass = JavadocClass(api, pkg, name)
            javadocClassDao.save(javadocClass)
        }
        return javadocClass
    }

    private inner class JavadocClassReader(private val api: JavadocApi, private val jarFile: JarFile,
                                           private val entry: JarEntry) : Runnable {

        override fun run() {
            try {
                val source = Roaster.parse(jarFile.getInputStream(entry))
                val parser = provider.get()
                val packages = if ("JDK" == api.name) arrayOf("java", "javax") else arrayOf<String>()
                parser.parse(api, source, *packages)
            } catch (e: Exception) {
                throw RuntimeException(e.message, e)
            }

        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(JavadocParser::class.java)

        fun getPackage(name: String): String {
            return name.split('.').dropLast(1).joinToString(".")
        }
    }
}
