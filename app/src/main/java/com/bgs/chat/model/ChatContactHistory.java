package com.bgs.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhufre on 6/8/2016.
 */
public class ChatContactHistory implements Parcelable {
    private ChatContact contact;
    private int newMessageCount;
    private ChatMessage lastChatMessage;

    public ChatContactHistory() {}
    /**
     *
     * @param contact
     * @param newMessageCount
     * @param lastChatMessage
     */
    public ChatContactHistory(ChatContact contact, int newMessageCount, ChatMessage lastChatMessage) {
        this.contact = contact;
        this.newMessageCount = newMessageCount;
        this.lastChatMessage = lastChatMessage;
    }

    public ChatContactHistory(Parcel in) {
        this.contact = in.readParcelable(ChatContact.class.getClassLoader());
        this.newMessageCount = in.readInt();
        this.lastChatMessage = in.readParcelable(ChatMessage.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if ( this.contact != null )
            dest.writeParcelable(this.contact, flags);
        dest.writeInt(this.newMessageCount);
        if ( this.lastChatMessage != null )
            dest.writeParcelable(this.lastChatMessage, flags);
    }

    public static final Creator<ChatContactHistory> CREATOR = new Creator<ChatContactHistory>() {
        @Override
        public ChatContactHistory createFromParcel(Parcel in) {
            return new ChatContactHistory(in);
        }

        @Override
        public ChatContactHistory[] newArray(int size) {
            return new ChatContactHistory[size];
        }
    };

    public ChatMessage getLastChatMessage() {
        return lastChatMessage;
    }

    public void setLastChatMessage(ChatMessage lastChatMessage) {
        this.lastChatMessage = lastChatMessage;
    }

    public ChatContact getContact() {
        return contact;
    }

    public void setContact(ChatContact contact) {
        this.contact = contact;
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(int newMessageCount) {
        this.newMessageCount = newMessageCount;
    }


}
