package javabot.commands

import com.antwerkz.sofia.Sofia
import com.beust.jcommander.Parameter
import javabot.Message
import javabot.dao.ChannelDao
import org.pircbotx.PircBotX
import javax.inject.Inject
import javax.inject.Provider

public class AddChannel : AdminCommand() {
    @Inject
    lateinit var channelDao: ChannelDao
    @Inject
    lateinit var ircBot: Provider<PircBotX>

    @Parameter(required = true)
    lateinit var channelName: String
    @Parameter(required = false)
    var logged = true
    @Parameter(required = false, password = true)
    var password: String = ""

    override fun execute(event: Message): List<Message> {
        val responses = arrayListOf<Message>()
        if (channelName.startsWith("#")) {
            var channel = channelDao.get(channelName)
            val isLogged = java.lang.Boolean.valueOf(logged)
            if (channel == null) {
                channel = channelDao.create(channelName, isLogged, password)
            } else {
                channel.logged = isLogged
                channelDao.save(channel)
            }

            responses.add(Message(event,
                    if (isLogged)
                        Sofia.adminJoiningLoggedChannel(channelName)
                    else
                        Sofia.adminJoiningChannel(channelName)))
            if (channel.key == null) {
                ircBot.get().sendIRC().joinChannel(channel.name)
            } else {
                ircBot.get().sendIRC().joinChannel(channel.name, channel.key)
            }

            responses.add(Message(ircBot.get().userChannelDao.getChannel(channelName), event, Sofia.adminJoinedChannel(event.user.nick)))
        } else {
            responses.add(Message(event, Sofia.adminBadChannelName()))
        }

        return responses
    }
}