package javabot.operations.locator

import com.google.common.base.Strings
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup
import java.util.HashMap

class JCPJSRLocator {

    fun locate(inputs: Map<String, String>): Map<String, String> {
        val retVal = HashMap<String, String>()
        val urlString = "http://www.jcp.org/en/jsr/detail?id=${inputs["jsr"]}"
        retVal.put("url", urlString)
        try {
            HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig).build()
                    .use { client ->
                        client.execute(HttpGet(urlString))
                                .entity?.let { entity ->
                            try {
                                retVal.put("title", Jsoup.parse(EntityUtils.toString(entity))
                                        .select("div.header1")
                                        .first().textNodes()
                                        .map { it.text().trim() }
                                        .joinToString(" "))
                            } finally {
                                EntityUtils.consume(entity)
                            }
                        }
                    }
        } catch (ignored: Exception) {
        }

        return retVal
    }

    fun findInformation(jsr: Int): String {
        val inputs = HashMap<String, String>()
        inputs.put("jsr", Integer.toString(jsr))
        val outputs = locate(inputs)
        if (Strings.isNullOrEmpty(outputs["title"])) {
            return ""
        }
        return "'" + outputs["title"] + "' can be found at " + outputs["url"]
    }

    companion object {
        val requestConfig = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000).setSocketTimeout(5000).build()
    }
}
