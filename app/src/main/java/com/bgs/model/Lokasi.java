package com.bgs.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhufre on 6/21/2016.
 */
public class Lokasi implements Parcelable {
    int id;
    String name;
    String address;
    double latitude;
    double longitude;
    String phone;
    int isPromo;
    String idLocationHere;
    String description;
    String locationTag;
    double distance;

    Category category;
    Merchant merchant;

    public Lokasi(){}

    public Lokasi(int id, String name, String address, double latitude, double longitude,
            String phone, int isPromo, String idLocationHere, String description, String locationTag, double distance,
                  Category category) {
        this(id, name, address, latitude, longitude, phone,
                isPromo, idLocationHere, description, locationTag, distance, category, null);
    }
    public Lokasi(int id, String name, String address, double latitude, double longitude,
                  String phone, int isPromo, String idLocationHere, String description, String locationTag, double distance,
                  Category category, Merchant merchant) {
        this.id = id;
        this.name = name;
        this.address =address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.isPromo = isPromo;
        this.idLocationHere = idLocationHere;
        this.description = description;
        this.locationTag = locationTag;
        this.distance = distance;
        this.category = category;
        this.merchant = merchant;

    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getIsPromo() { return isPromo; }
    public void setIsPromo(int isPromo) { this.isPromo = isPromo; }
    public String getIdLocationHere() { return idLocationHere; }
    public void setIdLocationHere(String idLocationHere) { this.idLocationHere = idLocationHere; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocationTag() { return locationTag; }
    public void setLocationTag(String locationTag) { this.locationTag = locationTag; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Merchant getMerchant() { return merchant; }
    public void setMerchant(Merchant merchant) { this.merchant = merchant; }

    protected Lokasi(Parcel in) {
        id = in.readInt();
        this.name = in.readString();
        this.address =in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.phone = in.readString();
        this.isPromo = in.readInt();
        this.idLocationHere = in.readString();
        this.description = in.readString();
        this.locationTag = in.readString();
        this.distance = in.readDouble();
        category = in.readParcelable(Category.class.getClassLoader());
        merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(phone);
        dest.writeInt(isPromo );
        dest.writeString(idLocationHere);
        dest.writeString(description);
        dest.writeString(locationTag);
        dest.writeDouble(distance);
        dest.writeParcelable(category, flags);
        dest.writeParcelable(merchant, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Lokasi> CREATOR = new Creator<Lokasi>() {
        @Override
        public Lokasi createFromParcel(Parcel in) {
            return new Lokasi(in);
        }

        @Override
        public Lokasi[] newArray(int size) {
            return new Lokasi[size];
        }
    };
}
