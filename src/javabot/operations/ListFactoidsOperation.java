package javabot.operations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javabot.BotEvent;
import javabot.Javabot;
import javabot.Message;

import com.rickyclarkson.java.util.TypeSafeList;

/**
 * This one must always be used last.
 */
public class ListFactoidsOperation implements BotOperation {
    /**
     * @see javabot.operations.BotOperation#handleMessage(javabot.BotEvent)
     */
    public List handleMessage(BotEvent event) {
        List messages = new TypeSafeList(new ArrayList(), Message.class);

        Javabot bot = event.getBot();
        String channel = event.getChannel();
        String message = event.getMessage().toLowerCase();

        if (channel.startsWith("#")) {
            messages.add(new Message(channel,
                "I will only list keys in a private " + "message", false));

            return messages;
        }

        Map map = bot.getMap();
        Iterator iterator = map.keySet().iterator();

        while (iterator.hasNext()) {
            String next = (String)iterator.next();

            messages.add(new Message(channel, next + " is " + map.get(next),
                false));
        }

        return messages;
    }
}
