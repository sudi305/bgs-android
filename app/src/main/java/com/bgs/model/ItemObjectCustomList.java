package com.bgs.model;

/**
 * Created by SND on 27/01/2016.
 */
public class ItemObjectCustomList {
    String loc_name, loc_address,loc_pic;
    int loc_promo, id_loc;
    double loc_distance;

    public ItemObjectCustomList(int id_loc, String loc_name, String loc_address, int loc_promo, double loc_distance, String loc_pic) {
        this.id_loc = id_loc;
        this.loc_name = loc_name;
        this.loc_address = loc_address;
        this.loc_promo = loc_promo;
        this.loc_distance = loc_distance;
        this.loc_pic = loc_pic;
    }

    public int getId_loc() {
        return id_loc;
    }

    public void setId_loc(int id_loc) {
        this.id_loc = id_loc;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    public String getLoc_address() {
        return loc_address;
    }

    public void setLoc_address(String loc_address) {
        this.loc_address = loc_address;
    }

    public int getLoc_promo() {
        return loc_promo;
    }

    public void setLoc_promo(int loc_promo) {
        this.loc_promo = loc_promo;
    }

    public double getLoc_distance() {
        return loc_distance;
    }

    public void setLoc_distance(double loc_distance) {
        this.loc_distance = loc_distance;
    }

    public String getLoc_pic() {
        return loc_pic;
    }

    public void setLoc_pic(String loc_pic) {
        this.loc_pic = loc_pic;
    }

    /*private String name;
    private int imageId;

    public ItemObjectCustomList(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }*/
}

