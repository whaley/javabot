package operations;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.rickyclarkson.java.util.Arrays;
import com.rickyclarkson.java.util.TypeSafeList;

public class ForgetFactoidOperation implements BotOperation
{
	public List handleMessage(BotEvent event)
	{
		List messages=new TypeSafeList(new ArrayList(),Message.class);
		
		String channel=event.getChannel();
		String message=event.getMessage();
		String sender=event.getSender();
		Javabot bot=event.getBot();
	
		String[] messageParts=message.split(" ");
			
		if (messageParts[0].equals("forget"))
		{
			int length=Array.getLength(messageParts);
			Object keyParts=Arrays.subset(messageParts,1,length);
			
			String key=Arrays.toString(keyParts," ");
			
			key=key.toLowerCase();
					
			if (bot.hasFactoid(key))
			{
				messages.add
				(
					new Message
					(
						channel,
						"I forgot about "+key+", "+
							sender+".",
						false
					)
				);
				
				bot.forgetFactoid(key);
				return messages;
			}
		
			messages.add
			(
				new Message
				(
					channel,
					"I never knew about "+key+" anyway, "+
						sender+".",
					false
				)
			);
		}
		
		return messages;
	}
}