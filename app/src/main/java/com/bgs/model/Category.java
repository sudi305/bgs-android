package com.bgs.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhufre on 6/21/2016.
 */
public class Category implements Parcelable {
    //single
    int id;
    String icon;
    String name;
    double radius;
    int totalLokasi;
    int totalPromo;

    public Category() {}

    public Category(int id, String icon, String name, double radius) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.radius = radius;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    public int getTotalLokasi() { return totalLokasi; }
    public void setTotalLokasi(int totalLokasi) { this.totalLokasi = totalLokasi; }
    public int getTotalPromo() { return totalPromo; }
    public void setTotalPromo(int totalPromo) { this.totalPromo = totalPromo; }

    protected Category(Parcel in) {
        id = in.readInt();
        icon = in.readString();
        name = in.readString();
        radius = in.readDouble();
        totalLokasi = in.readInt();
        totalPromo = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeDouble(radius);
        dest.writeInt(totalLokasi);
        dest.writeInt(totalPromo);
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
