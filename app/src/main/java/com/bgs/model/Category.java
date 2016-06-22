package com.bgs.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhufre on 6/21/2016.
 */
public class Category implements Parcelable {
    int id;
    //double latitude;
    //double longitude;
    String icon;
    String name;
    double radius;

    public Category(int id, /*double latitude, double longitude, */String icon, String name, double radius) {
        this.id = id;
        //this.latitude = latitude;
        //this.longitude = longitude;
        this.icon = icon;
        this.name = name;
        this.radius = radius;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    /*
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    */
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }

    protected Category(Parcel in) {
        id = in.readInt();
        //latitude = in.readDouble();
        //longitude = in.readDouble();
        icon = in.readString();
        name = in.readString();
        radius = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        //dest.writeDouble(latitude);
        //dest.writeDouble(longitude);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeDouble(radius);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
