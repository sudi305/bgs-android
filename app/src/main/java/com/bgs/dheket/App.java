package com.bgs.dheket;

import android.app.Application;
import android.location.LocationManager;
import android.os.Handler;

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

    private UserApp userApp;
    LocationManager locationManager;
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
        try {
            IO.Options options = new IO.Options();
            //options.reconnectionAttempts = 1;
            //options.forceNew = true;
            options.reconnectionDelay = 1000;
            options.reconnectionDelayMax = 2000;
            options.timeout = 5000;
            mSocket = IO.socket(Constants.CHAT_SERVER_URL, options);
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
    public LocationManager getLocationManager() {return locationManager;}

    public final static String SOCKET_EVENT_LOGIN = "login";
    public final static String SOCKET_EVENT_USER_JOIN = "user join";
    public final static String SOCKET_EVENT_USER_LEFT = "user left";
    public final static String SOCKET_EVENT_NEW_MESSAGE = "new message";
    public final static String SOCKET_EVENT_TYPING = "typing";
    public final static String SOCKET_EVENT_STOP_TYPING = "stop typing";
    public final static String SOCKET_EVENT_LIST_CONTACT  = "list contact";
    public final static String SOCKET_EVENT_UPDATE_CONTACT = "update contact";
    //EVENT OUT
    public final static String SOCKET_EVENT_DO_LOGIN = "do login";
    public final static String SOCKET_EVENT_GET_CONTACTS = "get contacts";

    private String[] SOCKET_EVENTS = {Socket.EVENT_CONNECT,  Socket.EVENT_DISCONNECT,
                                        Socket.EVENT_CONNECT_ERROR, Socket.EVENT_CONNECT_TIMEOUT,
                                        SOCKET_EVENT_LOGIN, SOCKET_EVENT_USER_JOIN,
                                        SOCKET_EVENT_USER_LEFT, SOCKET_EVENT_NEW_MESSAGE,
                                        SOCKET_EVENT_TYPING, SOCKET_EVENT_STOP_TYPING,
                                        SOCKET_EVENT_LIST_CONTACT, SOCKET_EVENT_UPDATE_CONTACT
                                };

    public Socket startChatSocket(Map<String, Emitter.Listener> listener) {
        for(String event : listener.keySet() ) {
            mSocket.on(event, listener.get(event));
        }
        if (mSocket.connected() == false)
            mSocket.connect();

        return mSocket;
    }

    public Socket resumeChatSocket() {
        if (mSocket.connected() == false)
            mSocket.connect();

        return mSocket;
    }

    public void stopChatSocket(Map<String, Emitter.Listener> listener, boolean disconnect) {
        if ( disconnect )
            mSocket.disconnect();

        for(String event : listener.keySet() ) {
            mSocket.off(event, listener.get(event));
        }
    }
}
