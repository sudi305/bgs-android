package com.bgs.chat.model;

import java.util.Date;

/**
 * Created by zhufre on 6/14/2016.
 */
public class ChatHelper {
    public static ChatMessage createMessage(final String messageText, final MessageType messageType) {
        return createMessage(null, messageText, messageType);
    }
    public static ChatMessage createMessage(final String senderName, final String messageText, final MessageType messageType) {
        if(messageText.trim().length()==0)
            return null ;

        final ChatMessage message = new ChatMessage();
        if ( senderName != null )
            message.setSenderName(senderName);

        message.setMessageStatus(MessageStatus.SENT);
        message.setMessageText(messageText);
        message.setMessageType(messageType);
        message.setMessageTime(new Date().getTime());

        return message;
    }
}
