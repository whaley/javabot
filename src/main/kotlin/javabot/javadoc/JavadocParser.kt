package javabot.javadoc

import javabot.JavabotThreadFactory
import javabot.dao.ApiDao
import javabot.dao.JavadocClassDao
import org.jboss.forge.roaster.Roaster
import org.jboss.forge.roaster.model.JavaClass
import org.jboss.forge.roaster.model.JavaType
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.io.Writer
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class JavadocParser @Inject constructor(val apiDao: ApiDao, val javadocClassDao: JavadocClassDao,
                                        val provider: Provider<JavadocClassParser>) {
    lateinit var api: JavadocApi

    fun parse(classApi: JavadocApi, location: String, writer: Writer) {
        api = classApi
        try {
            val tmpDir = File("/tmp")
            if (!tmpDir.exists()) {
                File(System.getProperty("java.io.tmpdir"))
            }

            val workQueue = LinkedBlockingQueue<Runnable>()
            val executor = ThreadPoolExecutor(20, 30, 30, TimeUnit.SECONDS, workQueue,
                    JavabotThreadFactory(false, "javadoc-thread-"))
            executor.prestartCoreThread()
            val file = File(location)
            var deleteFile = false
            val packages = mutableListOf<String>()
            if ("JDK" != api.name) {
                deleteFile = true
            } else {
                packages.addAll(arrayOf("java", "javax"))
            }
            try {
                JarFile(file).use { jarFile ->
                    val entries = jarFile.entries()
                    entries.iterator()
                            .forEach { entry ->
                                if (entry.name.endsWith(".java")
                                        && (packages.isEmpty() || packages.firstOrNull { entry.name.startsWith(it + "/") } != null)) {
                                    if (!workQueue.offer(JavadocClassReader(jarFile, entry), 1, TimeUnit.MINUTES)) {
                                        writer.write("Failed to class to queue: " + entry)
                                    }
                                }
                            }
                    while (!workQueue.isEmpty()) {
                        writer.write("Waiting on %s work queue to drain.  %d items left".format(api.name, workQueue.size))
                        Thread.sleep(5000)
                    }
                }
            } finally {
                if (deleteFile) {
                    file.delete()
                }
            }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.HOURS)
            writer.write("Finished importing %s.  %s!".format(api.name, if (workQueue.isEmpty()) "SUCCESS" else "FAILURE"))
        } catch (e: IOException) {
            log.error(e.message, e)
            throw RuntimeException(e.message, e)
        } catch (e: InterruptedException) {
            log.error(e.message, e)
            throw RuntimeException(e.message, e)
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

    private inner class JavadocClassReader(private val jarFile: JarFile, private val entry: JarEntry) : Runnable {

        override fun run() {
            try {
                val source = Roaster.parse(jarFile.getInputStream(entry))
                val parser = provider.get()
                val packages = if ("JDK" == api.name) arrayOf("java", "javax") else arrayOf<String>()
                parser.parse(source, *packages)
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
