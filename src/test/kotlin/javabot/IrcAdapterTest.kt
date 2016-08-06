package javabot

import com.antwerkz.sofia.Sofia
import com.google.inject.Inject
import com.google.inject.Provider
import com.jayway.awaitility.Duration
import javabot.dao.FactoidDao
import javabot.dao.LogsDaoTest
import javabot.model.Channel
import javabot.model.JavabotUser
import org.pircbotx.PircBotX
import org.pircbotx.User
import org.pircbotx.hooks.events.MessageEvent
import org.pircbotx.hooks.events.PrivateMessageEvent
import org.testng.Assert
import org.testng.annotations.Test
import java.util.concurrent.TimeUnit

class IrcAdapterTest : BaseTest() {
    @Inject
    lateinit var listener: IrcAdapter
    @Inject
    lateinit var factoidDao: FactoidDao
    @Inject
    lateinit var ircBot: Provider<PircBotX>

    @Test
    fun testOnMessage() {
        listener.onMessage(MessageEvent(ircBot.get(), MockIrcChannel(TEST_CHANNEL), MockIrcUser(TEST_USER), "~dude"))
        Assert.assertEquals(messages.get(Duration(10, TimeUnit.MINUTES))[0], Sofia.unhandledMessage(TEST_USER.nick))
    }

    @Test
    fun testOnPrivateMessage() {
        listener.onPrivateMessage(PrivateMessageEvent(ircBot.get(), MockIrcUser(TEST_USER), "dude"))
        Assert.assertEquals(messages.get()[0], Sofia.unhandledMessage(TEST_USER.nick))
    }

    @Test
    fun factoidLookup() {
        factoidDao.delete(TEST_USER.nick, "impact", LogsDaoTest.CHANNEL_NAME)
        factoidDao.addFactoid(TEST_USER.nick, "impact", "<reply>ouch", LogsDaoTest.CHANNEL_NAME)
        listener.onMessage(MessageEvent(ircBot.get(), MockIrcChannel(TEST_CHANNEL), MockIrcUser(TEST_USER), "~impact"))
        Assert.assertEquals(messages.get(Duration(10, TimeUnit.MINUTES))[0], "ouch")

    }

    @Test
    fun tell() {
        factoidDao.delete(TEST_USER.nick, "impact", LogsDaoTest.CHANNEL_NAME)
        factoidDao.addFactoid(TEST_USER.nick, "impact", "<reply>ouch", LogsDaoTest.CHANNEL_NAME)
        listener.onMessage(MessageEvent(ircBot.get(), MockIrcChannel(TEST_CHANNEL), MockIrcUser(TEST_USER), "~~ ${TARGET_USER.nick} impact"))
        Assert.assertEquals(messages.get(Duration(10, TimeUnit.MINUTES))[0], "${TARGET_USER.nick}, ouch")
    }

}

class MockIrcUser(user: JavabotUser) : User(null, null, user.nick) {}

class MockIrcChannel(channel: Channel) : org.pircbotx.Channel(null, null, channel.name) {}
