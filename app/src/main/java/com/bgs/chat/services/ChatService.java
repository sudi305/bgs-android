package com.bgs.chat.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bgs.dheket.App;

import io.socket.client.Socket;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ChatService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_UPDATE_CONTACT = "in.co.madhur.chatbubblesdemo.action.UPDATE_CONTACT";
    private static final String ACTION_KEEP_CONNECTION = "in.co.madhur.chatbubblesdemo.action.KEEP_CONNECTION";

    //private static final String EXTRA_PARAM1 = "in.co.madhur.chatbubblesdemo.extra.PARAM1";
    //private static final String EXTRA_PARAM2 = "in.co.madhur.chatbubblesdemo.extra.PARAM2";

    private Socket socket;

    public ChatService() {
        super("ChatService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateContact(Context context) {
        Intent intent = new Intent(context, ChatService.class);
        intent.setAction(ACTION_UPDATE_CONTACT);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void startActionKeepConnection(Context context) {
        Intent intent = new Intent(context, ChatService.class);
        intent.setAction(ACTION_KEEP_CONNECTION);
        //intent.putExtra(EXTRA_PARAM1, param1);
        //intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d("DHEKET", "service starting");
        return super.onStartCommand(intent,flags,startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.d("DHEKET", "handle intent...");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_CONTACT.equals(action)) {
                //final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                //final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionUpdateContact();
            } else if (ACTION_KEEP_CONNECTION.equals(action)) {
                handleActionKeepConnection();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateContact() {
        try {
            App app = (App) getApplication();
            socket = app.getSocket();
            //Log.d("DHEKET", "IS LOGIN "+ ((MainChatActivity)getApplicationContext()).isLogin());
            //Log.d("DHEKET", "run background services get contacts...");
            if ( socket.connected() ) socket.emit(App.SOCKET_EVENT_GET_CONTACTS);

        }catch (Exception e) {
            Log.e("DHEKET",e.getMessage(), e);
        }
    }


    private void handleActionKeepConnection() {
        try {
            App app = (App) getApplication();
            socket = app.getSocket();
            //Log.d("DHEKET", "IS LOGIN "+ ((MainChatActivity)getApplicationContext()).isLogin());
            Log.d("DHEKET", "run background services keep connection...");
            if ( !socket.connected() ) {
                //re init
                //app.initSocket();
                Log.d("DHEKET", "Try reconnect...");
                socket.connect();
            }

        }catch (Exception e) {
            Log.e("DHEKET",e.getMessage(), e);
        }
    }
}
