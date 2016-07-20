package com.bgs.dheket;

import android.app.Application;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import com.bgs.chat.services.ChatClientService;
import com.bgs.chat.services.ChatEngine;
import com.bgs.common.Constants;
import com.bgs.common.NativeLoader;
import com.bgs.model.UserApp;
import com.facebook.FacebookSdk;

/**
 * Created by madhur on 3/1/15.
 */
public class App extends Application {
    private static UserApp mUserApp;
    private static LocationManager mLocationManager;
    private static App mInstance;
    public static volatile Handler applicationHandler = null;
    private static ChatEngine mChatEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.TAG, getClass().getCanonicalName() + " => ON CREATE" );

        FacebookSdk.sdkInitialize(getApplicationContext());
        /*
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bgs.dheket",  // replace with your unique package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        */
        mInstance = this;
        applicationHandler = new Handler(getInstance().getMainLooper());
        mChatEngine = new ChatEngine(getApplicationContext());
        NativeLoader.initNativeLibs(App.getInstance());

        Intent startServiceIntent = new Intent(this, ChatClientService.class);
        startService(startServiceIntent);
    }

    public static App getInstance() {
        return mInstance;
    }
    public static void updateUserApp(UserApp userApp) {
        mUserApp = userApp;
    }
    public static UserApp getUserApp() { return mUserApp; }

    public static void setLocationManager(LocationManager locationManager) { mLocationManager = locationManager; }
    public static LocationManager getLocationManager() {
        return mLocationManager;
    }
    public static ChatEngine getChatEngine() {
        return mChatEngine;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(Constants.TAG, getClass().getCanonicalName() + " => ON TERMINATE" );

    }
}
