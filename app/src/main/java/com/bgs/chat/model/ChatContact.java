package com.bgs.chat.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhufre on 6/7/2016.
 */
public class ChatContact implements Parcelable {
    private String id;
    private String name;
    private String picture;
    private String email;
    private String phone;
    private ChatContactType contactType;
    private int active;

    public ChatContact() {}
    /**
     * 2param id
     * @param name
     * @param picture
     * @param email
     * @param phone
     * @param contactType
     */
    public ChatContact(String id, String name, String picture, String email, String phone, ChatContactType contactType) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.email = email;
        this.phone = phone;
        this.contactType = contactType;
        this.active = 0;
    }

    public ChatContact(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.picture = in.readString();
        this.email = in.readString();
        this.phone = in.readString();
        this.contactType = ChatContactType.parse(in.readInt());
        this.active = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.picture);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeInt(this.contactType.ordinal());
        dest.writeInt(this.active);
    }

    public static final Creator<ChatContact> CREATOR = new Creator<ChatContact>() {

        @Override
        public ChatContact createFromParcel(Parcel source) {
            return new ChatContact(source);
        }

        @Override
        public ChatContact[] newArray(int size) {
            return new ChatContact[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ChatContactType getContactType() {
        return contactType;
    }

    public void setContactType(ChatContactType contactType) {
        this.contactType = contactType;
    }

    public int getActive() {return active;}

    public void setActive(int active) {this.active = active;}
}
