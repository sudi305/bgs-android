package com.bgs.model;

/**
 * Created by SND on 27/01/2016.
 */
public class ItemObjectCustomList {
    private String name;
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
    }
}

