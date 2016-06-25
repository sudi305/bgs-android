package com.bgs.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bgs.chat.adapters.ChatTabPagerAdapter;
import com.bgs.chat.fragments.ChatContactFragment;
import com.bgs.chat.fragments.ChatContactHistoryFragment;
import com.bgs.chat.model.ChatContact;
import com.bgs.chat.model.ChatContactType;
import com.bgs.chat.model.ChatHelper;
import com.bgs.chat.model.ChatMessage;
import com.bgs.chat.model.MessageType;
import com.bgs.chat.services.ChatService;
import com.bgs.common.Constants;
import com.bgs.dheket.App;
import com.bgs.dheket.MainMenuActivity;
import com.bgs.dheket.R;
import com.bgs.model.UserApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainChatActivity extends AppCompatActivity {
    ViewPager viewPager;
    private Socket socket;
    //private boolean isLogin = false;
    //private boolean isConnect = false;

    private ImageButton goBackButton;
    private ChatTabPagerAdapter pagerAdapter;
    //private App app;

    /*
    public boolean isLogin() {
        return this.isLogin;
    }
    */

    private static Map<String, Emitter.Listener> CHAT_EVENT_LISTENERS = new LinkedHashMap<String, Emitter.Listener>();
    private boolean mLogin = false;
    private boolean mConnect = false;

    public boolean isConnect() {
        return mConnect;
    }
    public void setConnect(boolean mConnect) {
        this.mConnect = mConnect;
    }
    public boolean isLogin() {
        return mLogin;
    }
    public void setLogin(boolean mLogin) {
        this.mLogin = mLogin;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainchat);

        goBackButton = (ImageButton) findViewById(R.id.go_back_button);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMainMenu();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ChatTabPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        // Attach the page change listener to tab strip and **not** the view pager inside the activity
        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(MainChatActivity.this, "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                //retrive contact
                if ( position == 1)
                    ChatService.startActionUpdateContact(getActivity());
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

        //Log.d(getResources().getString(R.string.app_name), "count=" + tabsStrip.getChildCount());

        App app = (App) getApplication();
        /*
        if ( app.getUserApp() == null ) {
            String id = NativeUtilities.getDeviceUniqueID(getContentResolver());
            app.setUserApp(new UserApp(id, id, "", id + "@zmail.com", ""));
        }
        */
        TextView title = (TextView) findViewById(R.id.user_app);
        String titleText = title.getText() + ( app.getUserApp() == null ? "" : " - " + app.getUserApp().getName());
        title.setText(titleText);

        CHAT_EVENT_LISTENERS.putAll(new LinkedHashMap<String, Emitter.Listener>(){{
            put(Socket.EVENT_CONNECT, onConnect);
            put(Socket.EVENT_DISCONNECT, onDisconnect);
            put(Socket.EVENT_CONNECT_ERROR, onConnectError);
            put(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            put(App.SOCKET_EVENT_LOGIN, onLogin);
            put(App.SOCKET_EVENT_USER_JOIN, onUserJoin);
            put(App.SOCKET_EVENT_LIST_CONTACT, onListContact);
            put(App.SOCKET_EVENT_UPDATE_CONTACT, onUpdateContact);
            put(App.SOCKET_EVENT_NEW_MESSAGE, onNewMessage);
        }});
        socket = app.startChatSocket(CHAT_EVENT_LISTENERS);

    }

    public void toMainMenu(){
        Intent mainMenu = new Intent(this,MainMenuActivity.class);
        startActivity(mainMenu);
        finish();
    }

    private Activity getActivity() {
        return this;
    }

    private void attemptLogin() {
        if (isConnect()) {
            if (isLogin()) return;
            UserApp userApp = ((App) getApplication()).getUserApp();
            if (userApp != null) {

                try {
                    //String name = NativeUtilities.getDeviceUniqueID(getContentResolver());
                    JSONObject user = new JSONObject();
                    user.put("name", userApp.getName());
                    user.put("email", userApp.getEmail());
                    user.put("phone", userApp.getPhone());
                    socket.emit(App.SOCKET_EVENT_DO_LOGIN, user);
                } catch (JSONException e) {
                    Log.e(Constants.TAG_CHAT, e.getMessage(), e);
                }
            }
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnect()) {
                        setConnect(true);
                        Log.d(Constants.TAG_CHAT, getResources().getString(R.string.connect));
                    }


                    attemptLogin();
                    //if(null!=userContact) socket.emit("add user", userContact.getName());
                    //isLogin = true;
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setConnect(false);
                    setLogin(false);
                    //Toast.makeText(getApplicationContext(), R.string.disconnect, Toast.LENGTH_LONG).show();
                    Log.d(Constants.TAG_CHAT, getResources().getString(R.string.disconnect));
                    //attemptLogin();
                    while (isConnect() == false)
                        ChatService.startActionKeepConnection(getActivity());
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setConnect(false);
                    setLogin(false);
                    //Toast.makeText(getApplicationContext(), R.string.error_connect, Toast.LENGTH_LONG).show();
                    Log.d(Constants.TAG_CHAT, getResources().getString(R.string.error_connect));
                    //attemptLogin();

                }
            });
        }
    };

    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (isLogin()) return;

            JSONObject data = (JSONObject) args[0];
            try {
                setLogin(data.getBoolean("success"));
                //Toast.makeText(getApplicationContext(), "Login1 " + isLogin, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e(Constants.TAG_CHAT, e.getMessage(), e);
                return;
            }
            Log.d(Constants.TAG_CHAT, "Login " + isLogin());
            //Toast.makeText(getApplicationContext(), "Login2 " + isLogin, Toast.LENGTH_SHORT).show();

            //retrive contact
            if (isLogin()) {
                ChatService.startActionUpdateContact(getActivity());
                //socket.emit("get contacts");
            }
        }
    };


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    JSONObject from;
                    String message;
                    try {
                        from = data.getJSONObject("from");
                        message = data.getString("message");

                        //String id = from.getString("id");
                        String name = from.getString("name");
                        String email = from.getString("email");
                        String phone = from.getString("phone");

                        //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT );
                        Log.d(getResources().getString(R.string.app_name), "message2 = " + message);
                        //removeTyping(username);
                        final ChatContactFragment fragment0 = (ChatContactFragment) pagerAdapter.getItem(1);
                        ChatContact contact = fragment0.getContact(email);
                        if ( contact == null) {
                            contact = new ChatContact(null, name, "", email, phone, ChatContactType.PRIVATE);
                        }
                        if ( contact.getActive() != 1) {
                            final ChatMessage lastMessage = ChatHelper.createMessage(message, MessageType.IN);
                            final ChatContactHistoryFragment fragment1 = (ChatContactHistoryFragment) pagerAdapter.getItem(0);
                            fragment1.updateContactHistory(contact, 1, lastMessage);
                        }

                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };


    private Emitter.Listener onUserJoin = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = (JSONObject) args[0];
            JSONObject user;
            try {
                user = data.getJSONObject("user");
                Log.d(getResources().getString(R.string.app_name), "User Join " + user.getString("email"));
            } catch (JSONException e) {
                Log.e(getResources().getString(R.string.app_name), e.getMessage(), e);
                return;
            }


            //retrive contact
            ChatService.startActionUpdateContact(getActivity());
        }
    };

    private Emitter.Listener onListContact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Log.d(getResources().getString(R.string.app_name), "list contact ");
            JSONObject data = (JSONObject) args[0];
            JSONArray contacts = new JSONArray();
            try {
                contacts = data.getJSONArray("contacts");
                //Toast.makeText(getApplicationContext(), "Login1 " + isLogin, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e(getResources().getString(R.string.app_name), e.getMessage(), e);
                return;
            }
            Log.d(getResources().getString(R.string.app_name), "list contacts = " + contacts);
            //Toast.makeText(getApplicationContext(), "Login2 " + isLogin, Toast.LENGTH_SHORT).show();


            final ArrayList<ChatContact> contactList = new ArrayList<ChatContact>();
            for(int i = 0; i < contacts.length(); i++) {
                try {
                    JSONObject contact = contacts.getJSONObject(i);
                    String id = contact.getString("id");
                    String name = contact.getString("name");
                    String email = contact.getString("email");
                    String phone = contact.getString("phone");
                    //skip contact for current app user

                    //if ( email.equalsIgnoreCase(app.getUserApp().getEmail())) continue;
                    contactList.add(new ChatContact(id, name, "", email, phone, ChatContactType.PRIVATE));
                } catch (JSONException e) {
                    Log.d(getResources().getString(R.string.app_name),e.getMessage(), e);
                }
            }

            ChatContactFragment fragment = (ChatContactFragment)pagerAdapter.getItem(1);
            fragment.updateContact(contactList);

        }
    };

    private Emitter.Listener onUpdateContact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            boolean remove = false;
            JSONObject data = (JSONObject) args[0];
            JSONObject contact = null;
            try {
                remove = data.getBoolean("remove");
                contact = data.getJSONObject("contact");
                //Toast.makeText(getApplicationContext(), "Login1 " + isLogin, Toast.LENGTH_SHORT).show();

                Log.d(getResources().getString(R.string.app_name), "update contact = " + contact);
                //Toast.makeText(getApplicationContext(), "Login2 " + isLogin, Toast.LENGTH_SHORT).show();
                if ( contact != null ) {

                    String id = contact.getString("id");
                    String name = contact.getString("name");
                    String email = contact.getString("email");
                    String phone = contact.getString("phone");

                    ChatContactFragment fragment = (ChatContactFragment) pagerAdapter.getItem(1);
                    if (remove) {
                        fragment.removeContact(email);
                    } else {
                        fragment.updateContact(new ChatContact(id, name, "", email, phone, ChatContactType.PRIVATE));
                    }
                }

            } catch (JSONException e) {
                Log.e(getResources().getString(R.string.app_name), e.getMessage(), e);
                return;
            }


        }
    };

    //END SOCKET METHOD BLOCK

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG_CHAT, "OnResume");
        //reset page adapter
        //pagerAdapter = new ChatTabPagerAdapter(getSupportFragmentManager());
        //viewPager.setAdapter(pagerAdapter);

        App app = (App)getApplication();
        socket = app.resumeChatSocket();
        //retrive contact
        ChatService.startActionUpdateContact(getActivity());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App app = (App)getApplication();
        app.stopChatSocket(CHAT_EVENT_LISTENERS, false);
    }


}
