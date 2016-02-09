package com.bgs.imageOrView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.bgs.extended.TabFragDetChat;
import com.bgs.extended.TabFragDetLoc;
import com.bgs.extended.TabFragDetPromo;
import com.bgs.extended.TabFragmentList;
import com.bgs.extended.TabFragmentMap;

/**
 * Created by SND on 27/01/2016.
 */
public class PagerAdapterDetailLoc extends FragmentStatePagerAdapter {
    int mNumOfTabs,cat_id,loc_id;
    double radius, latitude, longitude;
    String kategori;

    public PagerAdapterDetailLoc(FragmentManager fm, int NumOfTabs,int loc_id,int cat_id, double radius, double latitude, double longitude, String kategori) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.loc_id = loc_id;
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
                TabFragDetLoc tab1 = new TabFragDetLoc();
                Bundle paket = new Bundle();
                paket.putInt("loc_id",loc_id);
                paket.putInt("cat_id",cat_id);
                paket.putDouble("radius",radius);
                paket.putDouble("latitude",latitude);
                paket.putDouble("longitude",longitude);
                paket.putString("kategori", kategori);
                tab1.setArguments(paket);
                /*Bundle paket = new Bundle();
                tab1.setArguments(paket);*/
                return tab1;
            case 1:
                TabFragDetChat tab2 = new TabFragDetChat();
                Bundle paket2 = new Bundle();
                paket2.putInt("loc_id",loc_id);
                paket2.putInt("cat_id", cat_id);
                paket2.putDouble("radius",radius);
                paket2.putDouble("latitude",latitude);
                paket2.putDouble("longitude", longitude);
                paket2.putString("kategori", kategori);
                tab2.setArguments(paket2);
                return tab2;
            case 2:
                TabFragDetPromo tab3 = new TabFragDetPromo();
                Bundle paket3 = new Bundle();
                paket3.putInt("loc_id",loc_id);
                tab3.setArguments(paket3);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
