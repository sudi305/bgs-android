package com.bgs.common;

import android.location.Location;
import android.location.LocationManager;

import com.bgs.domain.chat.model.UserType;

/**
 * Created by madhur on 3/1/15.
 */
public class Constants {
    public static final UserType USER_TYPE = UserType.USER;

    public static final String TAG = "DHEKET-USER";
    public static final String TAG_GPS = "DHEKET-USER-GPS";
    public static final String TAG_CHAT = "DHEKET-USER-CHAT";
    //dheket chat server
    public static final String CHAT_SERVER_URL = "http://136.243.214.45:3300/";
    //local chat server
    //public static final String CHAT_SERVER_URL = "http://192.168.1.100:3300/";

    //public static final String CHAT_SERVER_URL = "http://192.168.43.228:3300/";
}
