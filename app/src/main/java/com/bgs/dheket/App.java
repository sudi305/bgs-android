package com.bgs.dheket;

import android.app.Application;
import android.os.Handler;

import com.bgs.model.UserApp;
import com.bgs.common.Constants;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by madhur on 3/1/15.
 */
public class App extends Application {
    private UserApp userApp;
    private static App Instance;
    public static volatile Handler applicationHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Instance=this;

        applicationHandler = new Handler(getInstance().getMainLooper());

        NativeLoader.initNativeLibs(App.getInstance());

    }

    public static App getInstance()
    {
        return Instance;
    }

    private Socket mSocket;
    {
        initSocket();
        /*
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void initSocket(){
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
    public void setUserApp(UserApp userApp) {
        this.userApp = userApp;
    }
    public UserApp getUserApp() {
        return userApp;
    }
}