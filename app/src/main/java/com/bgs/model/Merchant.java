package com.bgs.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhufre on 6/22/2016.
 */
public class Merchant implements Parcelable {
    int id;
    String name;
    String userName;
    String email;
    String gender;
    String birthDay;
    String phone;
    String address;
    String facebookPhoto;

    public Merchant() {}

    public Merchant(int id, String name, String userName, String email, String gender,
            String birthDay, String phone, String address, String facebookPhoto) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.email = email;
        this.gender = gender;
        this.birthDay = birthDay;
        this.phone = phone;
        this.address = address;
        this.facebookPhoto = facebookPhoto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getBirthDay() { return birthDay; }
    public void setBirthDay(String birthDay) { this.birthDay = birthDay; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getFacebookPhoto() { return facebookPhoto; }
    public void setFacebookPhoto(String facebookPhoto) { this.facebookPhoto = facebookPhoto; }

    protected Merchant(Parcel in) {
        id = in.readInt();
        name = in.readString();
        userName = in.readString();
        email = in.readString();
        gender = in.readString();
        birthDay = in.readString();
        phone = in.readString();
        address = in.readString();
        facebookPhoto = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(gender);
        dest.writeString(birthDay);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(facebookPhoto);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Merchant> CREATOR = new Creator<Merchant>() {
        @Override
        public Merchant createFromParcel(Parcel in) {
            return new Merchant(in);
        }

        @Override
        public Merchant[] newArray(int size) {
            return new Merchant[size];
        }
    };
}
