package javabot.model

import com.google.inject.Provider
import javabot.JavabotConfig
import javabot.dao.AdminDao
import javabot.dao.ApiDao
import javabot.javadoc.JavadocApi
import javabot.javadoc.JavadocParser
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Transient
import java.io.File
import java.io.FileOutputStream
import java.io.StringWriter
import java.net.MalformedURLException
import java.net.URL
import javax.inject.Inject

@Entity("events") class ApiEvent : AdminEvent {

    companion object {
        fun locateJDK(): String {
            var property = System.getProperty("java.home")
            if (property.endsWith("/jre")) {
                property = property.dropLast(4)
            }
            return File(property, "src.zip").toURI().toURL().toString()
        }
    }

    var apiId: ObjectId? = null

    lateinit var name: String

    lateinit var downloadUrl: String

    @Inject
    @Transient
    lateinit var ircBot: Provider<PircBotX>

    @Inject
    @Transient
    lateinit var config: JavabotConfig

    @Inject
    @Transient
    lateinit var parser: JavadocParser

    @Inject
    @Transient
    lateinit var apiDao: ApiDao

    @Inject
    @Transient
    lateinit var adminDao: AdminDao

    constructor() {
    }

    constructor(requestedBy: String, name: String, downloadUrl: String) : super(requestedBy, EventType.ADD) {
        this.requestedBy = requestedBy
        this.name = name
        if (name == "JDK") {
            try {
                this.downloadUrl = locateJDK()
            } catch (e: MalformedURLException) {
                throw IllegalArgumentException(e.message, e)
            }
        } else {
            this.downloadUrl = downloadUrl
        }
    }

    constructor(requestedBy: String, type: EventType, apiId: ObjectId?) : super(requestedBy, type) {
        this.apiId = apiId
    }

    constructor(requestedBy: String, type: EventType, name: String) : super(requestedBy, type) {
        this.name = name
    }

    override fun update() {
        delete()
        add()
    }

    override fun delete() {
        var api = apiDao.find(apiId)
        if (api == null) {
            api = apiDao.find(name)
        }
        if (api != null) {
            apiDao.delete(api)
        }
    }

    override fun add() {
        val api = JavadocApi(name, config.url() + "/javadoc/", downloadUrl)
        apiDao.save(api)
        process(api)
    }

    override fun reload() {
        val api = apiDao.find(apiId)
        if (api != null) {
            apiDao.delete(apiId)
            api.id = ObjectId()
            apiDao.save(api)
            process(api)
        }
    }

    private fun process(api: JavadocApi) {
        val user = JavabotUser(requestedBy)
        val admin = adminDao.getAdmin(user)
        if (admin != null) {
            val file = api.downloadUrl.downloadZip(api.name + ".jar", api.downloadUrl)
            parser.parse(api, file.absolutePath, object : StringWriter() {
                override fun write(line: String) {
                    bot.privateMessageUser(user, line)
                }
            })
        }

    }

    override fun toString(): String {
        return "ApiEvent{name='${name}', state=${state}, completed=${completed}, type=${type}}"
    }

}

fun String.downloadZip(file: File): File {
    if (!file.exists()) {
        val fileOutputStream = FileOutputStream(file)
        val openStream = URL(this).openStream()
        fileOutputStream.write(openStream.readBytes())
        fileOutputStream.close()
        openStream.close()
    }
    return file
}