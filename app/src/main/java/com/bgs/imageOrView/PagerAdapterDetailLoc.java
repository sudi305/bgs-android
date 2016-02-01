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
    int mNumOfTabs,cat_id;
    double radius, latitude, longitude;

    public PagerAdapterDetailLoc(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabFragDetLoc tab1 = new TabFragDetLoc();
                /*Bundle paket = new Bundle();
                tab1.setArguments(paket);*/
                return tab1;
            case 1:
                TabFragDetChat tab2 = new TabFragDetChat();
                return tab2;
            case 2:
                TabFragDetPromo tab3 = new TabFragDetPromo();
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
