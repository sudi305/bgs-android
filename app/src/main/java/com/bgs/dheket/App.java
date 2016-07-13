package com.bgs.dheket;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.bgs.chat.services.ChatClientService;
import com.bgs.common.Constants;
import com.bgs.common.NativeLoader;
import com.bgs.model.UserApp;
import com.facebook.FacebookSdk;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by madhur on 3/1/15.
 */
public class App extends Application {
    private static UserApp mUserApp;
    private static LocationManager mLocationManager;
    private static App mInstance;
    public static volatile Handler applicationHandler = null;
    private static ChatClientService mChatClientService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(Constants.TAG, getClass().getCanonicalName() + " => ON CREATE" );
        FacebookSdk.sdkInitialize(getApplicationContext());
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

        mInstance = this;
        applicationHandler = new Handler(getInstance().getMainLooper());
        mChatClientService = new ChatClientService(getApplicationContext());
        NativeLoader.initNativeLibs(App.getInstance());


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
    public static ChatClientService getChatClientService() { return mChatClientService; }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(Constants.TAG, getClass().getCanonicalName() + " => ON TERMINATE" );

    }
}
