package javabot.operations

import javabot.BaseTest
import javabot.Message
import javabot.operations.urlcontent.URLContentAnalyzer
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.AbstractHandler
import org.testng.Assert
import org.testng.Assert.assertEquals
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Test(groups = ["operations"])
class URLTitleOperationTest : BaseTest() {
    @Inject
    private lateinit var operation: URLTitleOperation
    private val analyzer = URLContentAnalyzer()

    private var server: Server? = null
    private var port = 0
    private val pathsToTitles = mapOf(
            "/" to "Google",
            "/java-channel" to "Freenode ##java  enthusiasts united",
            "/finding-hash-collisions-in-java-strings" to "Finding hash collisions in Java Strings",
            "/javase/tutorial/java/nutsandbolts/branch.html" to "Branching Statements (The Java Tutorials > Learning the Java Language > Language Basics",
            "/non-ascii-title-here" to "使用百度前必读 意见反馈 non-ascii-title-here 使用百度前必读 意见反馈",
            "/articles/why-programmers-should-have" to "Why Programmers Should Have a Blog - DZone Agile",
            "/javaee/7/api/javax/enterprise/inject/Instance.html" to "Instance (Java(TM) EE 7 Specification APIs)"
    )

    @BeforeClass
    fun startEmbeddedJetty() {
        val server = Server(0)
        server.handler = HandlerForTitleTests(pathsToTitles)
        server.start()
        server.dumpStdErr()

        val connector = server.connectors.first() as ServerConnector
        port = connector.localPort
    }

    @AfterClass
    fun stopEmbeddedJetty() {
        server?.stop()
    }

    class HandlerForTitleTests(private val pathsToTitles: Map<String,String>) : AbstractHandler() {
        override fun handle(target: String, baseRequest: Request, request: HttpServletRequest, response: HttpServletResponse) {
            val title = pathsToTitles[request.pathInfo]
            response.contentType = "text/html; charset=utf-8"
            response.status = if (title == null) HttpServletResponse.SC_NOT_FOUND else HttpServletResponse.SC_OK

            val out = response.writer
            out.println("<title>$title</title>")
            baseRequest.isHandled = true
        }
    }

    @Test(dataProvider = "urls")
    fun testSimpleUrl(url: String, content: String?) {
        val results = operation.handleChannelMessage(Message(TEST_CHANNEL, TEST_USER, url))
        if (content != null) {
            val result = if (results.isNotEmpty()) results[0].value else ""
            Assert.assertEquals(result, content,  "Results for '$url' should [$content]")
        } else {
            Assert.assertTrue(results.isEmpty(), "Results for '$url' should be empty: was [$results]")
        }
    }

    @Test(dataProvider = "urlRulesCheck")
    fun testFuzzyContent(url: String, title: String?, pass: Boolean) {
        assertEquals(analyzer.check(url, title), pass)
    }

    @DataProvider(name = "urls")
    fun getUrls(): Array<Array<*>> {
        val baseUrl = "http://localhost:$port"
        return arrayOf(
                arrayOf(baseUrl, null),
                arrayOf("Some text before $baseUrl", null),
                arrayOf("Some text before $baseUrl/java-channel", "botuser's title: \"Freenode ##java  enthusiasts united\""),
                arrayOf("$baseUrl some text after", null),
                arrayOf("$baseUrl/java-channel some text after", "botuser's title: \"Freenode ##java  enthusiasts united\""),
                arrayOf("$baseUrl/finding-hash-collisions-in-java-strings", null),
                arrayOf("$baseUrl/articles/why-programmers-should-have",
                        "botuser's title: \"Why Programmers Should Have a Blog - DZone Agile\""), // url matches title
                arrayOf("$baseUrl/doesnt_exist", null),
                arrayOf("http://", null),
                arrayOf("http://a", null),
                arrayOf("$baseUrl/javase/tutorial/java/nutsandbolts/branch.html",
                        "botuser's title: \"Branching Statements (The Java Tutorials > Learning the Java Language > Language Basics)\""),
                arrayOf("$baseUrl/javaee/7/api/javax/enterprise/inject/Instance.html", null),

                arrayOf("Two urls with titles: $baseUrl/finding-hash-collisions-in-java-strings and $baseUrl/java-channel",
                        "botuser's titles: \"- The Java EE 6 Tutorial\" | \"Freenode ##java  enthusiasts united\""),
                arrayOf("Two urls, one with a title: $baseUrl/finding-hash-collisions-in-java-strings and $baseUrl/java-channel",
                        "botuser's title: \"Freenode ##java  enthusiasts united\""),
                arrayOf("Two urls, duplicated:  $baseUrl/java-channel and $baseUrl/java-channel"
                        , "botuser's title: \"Freenode ##java  enthusiasts united\""),

                arrayOf("Ignore title if it doesn't contain at least 20 ascii chars $baseUrl/non-ascii-title-here", null)
        )
    }

    @DataProvider(name = "urlRulesCheck")
    fun getUrlsForRulesCheck(): Array<Array<*>> {
        //TODO check that 127.0.0.1 is blacklisted
        //TODO should hastebin and patebin be blacklisted?
        //                arrayOf("http://hastebin.com/askhjahs", null),
        //                arrayOf("http://pastebin.com/askhjahs", null),
        return arrayOf(arrayOf("http://pastebin.com", "pastebin for your wastebin", false),
                arrayOf("http://makemoneyfast.com/super-profit", "make money fast! super profit", false),
                arrayOf("http://varietyofsound.wordpress.com", "Variety Of Sound", false),
                arrayOf("http://javachannel.com", "Freenode ##java: for enthusiasts by enthusiasts", true),
                arrayOf("http://javachannel.com/exceptions", "Freenode ##java: How to properly handle exceptions", true),
                arrayOf("http://foo.bar.com", "", false),
                arrayOf("http://foo.bar.com", null, false))
    }

}