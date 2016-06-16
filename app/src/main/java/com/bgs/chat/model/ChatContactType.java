package com.bgs.chat.model;

/**
 * Created by madhur on 17/01/15.
 */
public enum ChatContactType {
    PRIVATE, GROUP;

    public static ChatContactType parse(int value) {
        for(ChatContactType item : values()) {
            if ( item.ordinal() == value )
                return item;
        }
        return null;
    }
};
