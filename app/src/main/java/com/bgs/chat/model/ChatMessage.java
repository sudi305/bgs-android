package com.bgs.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatMessage implements Parcelable {
    private String senderName;
    private String messageText;
    private MessageType messageType;
    private MessageStatus messageStatus;

    public ChatMessage() {}
    /**
     *
     * @param senderName
     * @param messageText
     * @param messageType
     * @param messageStatus
     */
    public ChatMessage(String senderName, String messageText, MessageType messageType, MessageStatus messageStatus) {
        this.senderName = senderName;
        this.messageText = messageText;
        this.messageType = messageType;
        this.messageStatus = messageStatus;
    }

    public ChatMessage(Parcel in) {
        this.senderName = in.readString();
        this.messageText = in.readString();
        this.messageType = MessageType.parse(in.readInt());
        this.messageStatus = MessageStatus.parse(in.readInt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.senderName);
        dest.writeString(this.messageText);
        if ( this.messageType != null )
            dest.writeInt(this.messageType.ordinal());
        if ( this.messageStatus != null )
            dest.writeInt(this.messageStatus.ordinal());
    }


    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };


    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    private long messageTime;

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {

        return messageText;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }


}
