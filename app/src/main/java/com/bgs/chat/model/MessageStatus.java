package com.bgs.chat.model;

/**
 * Created by madhur on 17/01/15.
 */
public enum MessageStatus {
    SENT, DELIVERED;

    public static MessageStatus parse(int value) {
        for(MessageStatus item : values()) {
            if ( item.ordinal() == value )
                return item;
        }
        return null;
    }
}
