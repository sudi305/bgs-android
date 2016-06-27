package com.bgs.dheket;

import android.app.Application;
import android.location.LocationManager;
import android.os.Handler;

import com.bgs.chat.services.ChatClientService;
import com.bgs.common.Constants;
import com.bgs.common.NativeLoader;
import com.bgs.model.UserApp;

import java.net.URISyntaxException;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by madhur on 3/1/15.
 */
public class App extends Application {
    private static UserApp mUserApp;
    LocationManager locationManager;
    private static App Instance;
    public static volatile Handler applicationHandler = null;
    private static ChatClientService mChatClientService;

    @Override
    public void onCreate() {
        super.onCreate();
        Instance = this;
        applicationHandler = new Handler(getInstance().getMainLooper());
        mChatClientService = new ChatClientService(getApplicationContext());
        NativeLoader.initNativeLibs(App.getInstance());

    }

    public static App getInstance() {
        return Instance;
    }
    public static void updateUserApp(UserApp userApp) {
        mUserApp = userApp;
    }
    public static UserApp getUserApp() { return mUserApp; }

    public void setLocationManager(LocationManager locationManager) { this.locationManager = locationManager; }
    public LocationManager getLocationManager() {
        return locationManager;
    }
    public static ChatClientService getChatClientService() { return mChatClientService; }


}
