package javabot.operations

import com.antwerkz.sofia.Sofia
import javabot.Javabot
import javabot.Message
import javabot.dao.AdminDao
import javabot.dao.impl.WeatherDao
import javax.inject.Inject

/**
 * Gets current weather conditions for a place given as a parameter.
 */
class WeatherOperation @Inject constructor(bot: Javabot, adminDao: AdminDao, var weatherDao: WeatherDao) : BotOperation(bot, adminDao) {
    override fun handleMessage(event: Message): List<Message> {
        val responses = arrayListOf<Message>()
        val message = event.value
        if (message.toLowerCase().startsWith("weather ")) {
            val place = message.substring("weather ".length).trim()
            val result = weatherDao.getWeatherFor(place)
            responses.add(Message(event, if (result == null) Sofia.weatherUnknown(place) else result.toString()))
        }
        return responses
    }

}
