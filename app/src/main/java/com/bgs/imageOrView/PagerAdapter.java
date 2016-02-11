package com.bgs.imageOrView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bgs.extended.TabFragmentList;
import com.bgs.extended.TabFragmentMap;

/**
 * Created by SND on 27/01/2016.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs,cat_id;
    double radius, latitude, longitude;
    String kategori;

    public PagerAdapter(FragmentManager fm, int NumOfTabs,int cat_id, double radius, double latitude, double longitude, String kategori) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.cat_id = cat_id;
        this.radius = radius;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kategori = kategori;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragmentList tab1 = new TabFragmentList();
                Bundle paket = new Bundle();
                paket.putInt("cat_id",cat_id);
                paket.putDouble("radius",radius);
                paket.putDouble("latitude",latitude);
                paket.putDouble("longitude",longitude);
                paket.putString("kategori", kategori);
                tab1.setArguments(paket);
                return tab1;
            case 1:
                TabFragmentMap tab2 = new TabFragmentMap();
                Bundle pack = new Bundle();
                pack.putInt("cat_id",cat_id);
                pack.putDouble("radius",radius);
                pack.putDouble("latitude",latitude);
                pack.putDouble("longitude",longitude);
                pack.putString("kategori", kategori);
                tab2.setArguments(pack);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
