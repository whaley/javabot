package javabot.operations

import javabot.BaseTest
import javabot.Message
import javabot.operations.urlcontent.URLContentAnalyzer
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.testng.Assert
import org.testng.Assert.assertEquals
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import javax.inject.Inject
import org.eclipse.jetty.servlet.ServletHandler
import javax.servlet.Servlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Test(groups = arrayOf("operations"))
class URLTitleOperationTest : BaseTest() {
    @Inject
    private lateinit var operation: URLTitleOperation
    private val analyzer = URLContentAnalyzer()
    private var server: Server = Server(0) //0 causes Jetty to pick a random port
    private var port = Int.MIN_VALUE

    @BeforeClass
    fun startHttpServer() {
        //Start jetty on a randomly available port
        val handler = ServletHandler()
        server.handler = handler
        handler.addServletWithMapping(ServerHtmlWithTitles::class.java, "/*");

        val connector = server.connectors[0]
        if (connector is ServerConnector) {
            port = connector.port
        } else {
            throw Exception("Unable to start embedded jetty")
        }
        server.start()
        println("Port $port")

    }

    @AfterClass
    fun stopHttpServer() {
        if (server.isStarted) {
            server.stop()
        }
    }

    inner class ServerHtmlWithTitles : HttpServlet() {
        val urlsAndTitles = this@URLTitleOperationTest.getUrls()
        override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
            res.contentType = "text/html"
            res.writer.print("test foo")
            res.writer.flush()
        }
    }


    @Test(dataProvider = "urls")
    fun testSimpleUrl(url: String, content: String?) {
        val results = operation.handleChannelMessage(Message(TEST_CHANNEL, TEST_USER, url))
        if (content != null) {
            Assert.assertEquals(results[0].value, content)
        } else {
            Assert.assertTrue(results.isEmpty(), "Results for '${url}' should be empty: ${results}")
        }
    }

    @DataProvider(name = "urls")
    fun getUrls(): Array<Array<*>> {
        return arrayOf(
                arrayOf("http://google.com/", null),
                arrayOf("http://google.com", null),
                arrayOf("http://localhost", null),
                arrayOf("http://127.0.0.1", null),
                arrayOf("http://a", null), // should work unless DNS gets in the way, as it does for me
                arrayOf("Have you tried to http://google.com", null),
                arrayOf("http://varietyofsound.wordpress.com has a lot of VSTs", null),
                arrayOf("Have you tried to http://javachannel.org/", "botuser's title: \"Freenode ##java  enthusiasts united\""),
                arrayOf("http://javachannel.org/posts/finding-hash-collisions-in-java-strings/", null),
                arrayOf("http://hastebin.com/askhjahs", null),
                arrayOf("http://pastebin.com/askhjahs", null),
                arrayOf("http://architects.dzone.com/articles/why-programmers-should-have",
                        "botuser's title: \"Why Programmers Should Have a Blog - DZone Agile\""), // url matches title
                arrayOf("http://facebook.com/foo/bar/blah", null), // doesn't exist on facebook, I hope
                arrayOf("http://", null),
                arrayOf("http://docs.oracle.com/javaee/6/tutorial/doc/", "botuser's title: \"- The Java EE 6 Tutorial\""),
                arrayOf("https://docs.oracle.com/javaee/7/api/javax/enterprise/inject/Instance.html", null),
                arrayOf("http://docs.oracle.com/javase/tutorial/java/nutsandbolts/branch.html",
                        "botuser's title: \"Branching Statements (The Java Tutorials > Learning the Java Language > Language Basics)\""),
                arrayOf("http://git.io/foo", null),
                arrayOf("Two urls with titles: http://docs.oracle.com/javaee/6/tutorial/doc/ and http://javachannel.org/",
                        "botuser's titles: \"- The Java EE 6 Tutorial\" | \"Freenode ##java  enthusiasts united\""),
                arrayOf("Two urls, one with a title: http://javachannel.org/posts/finding-hash-collisions-in-java-strings/  and " +
                        "http://javachannel.org/", "botuser's title: \"Freenode ##java  enthusiasts united\""),
                arrayOf("Two urls, duplicated:  http://javachannel.org/ and http://javachannel.org/"
                        , "botuser's title: \"Freenode ##java  enthusiasts united\""),
                arrayOf("https://twitter.com/djspiewak/status/1004038775989678080", "botuser's title: \"Daniel Spiewak on Twitter: \"Random best practice note: just because your language has type inference doesn't mean it's bad to explicitly write types. Types are good! Types are documentation. Don't make future code reviewers play the human compiler game.\"\""),
                arrayOf("http://refheap.com", null) // this may change: right now, refheap.com returns a 502
        )
    }

    @Test(dataProvider = "urlRulesCheck")
    fun testFuzzyContent(url: String, title: String?, pass: Boolean) {
        assertEquals(analyzer.check(url, title), pass)
    }

    @DataProvider(name = "urlRulesCheck")
    fun getUrlsForRulesCheck(): Array<Array<*>> {
        return arrayOf(arrayOf("http://pastebin.com", "pastebin for your wastebin", false),
                arrayOf("http://makemoneyfast.com/super-profit", "make money fast! super profit", false),
                arrayOf("http://varietyofsound.wordpress.com", "Variety Of Sound", false),
                arrayOf("http://javachannel.com", "Freenode ##java: for enthusiasts by enthusiasts", true),
                arrayOf("http://javachannel.com/exceptions", "Freenode ##java: How to properly handle exceptions", true),
                arrayOf("http://foo.bar.com", "", false),
                arrayOf("http://foo.bar.com", null, false))
    }


}

