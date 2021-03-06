package javabot.model.javadoc

import javabot.dao.ApiDao
import javabot.model.Persistent
import net.swisstech.bitly.BitlyClient
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Id

abstract class JavadocElement : Persistent {
    @Id
    lateinit var id: ObjectId

    var apiId: ObjectId? = null

    var shortUrl: String? = null

    lateinit var url: String

    lateinit var visibility: Visibility

    fun setApi(api: JavadocApi) {
        this.apiId = api.id
    }

    private fun buildShortUrl(client: BitlyClient, url: String): String? {
        return client.shorten().setLongUrl(url).call().data.url
    }

    fun getDisplayUrl(hint: String, dao: ApiDao, client: BitlyClient?): String {
        if (shortUrl == null && client != null) {
            shortUrl = buildShortUrl(client, url) + " [" + dao.find(apiId) + ": " + hint + "]"
            dao.save(this)
        }
        return shortUrl ?: url
    }
}