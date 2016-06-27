package com.bgs.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import com.bgs.chat.services.ChatClientService;
import com.bgs.chat.services.ChatTaskService;
import com.bgs.common.Constants;
import com.bgs.common.Utility;
import com.bgs.dheket.App;
import com.bgs.dheket.MainMenuActivity;
import com.bgs.dheket.R;
import com.bgs.model.UserApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainChatActivity extends AppCompatActivity {
    ViewPager viewPager;
    private ImageButton goBackButton;
    private ChatTabPagerAdapter pagerAdapter;
    private static Map<String, Emitter.Listener> CHAT_EVENT_LISTENERS = new LinkedHashMap<String, Emitter.Listener>();
    private ChatClientService chatClientService;

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
                    ChatTaskService.startActionUpdateContact(getActivity());
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

        App app = App.getInstance();
        /*
        if ( app.getUserApp() == null ) {
            String id = NativeUtilities.getDeviceUniqueID(getContentResolver());
            app.setUserApp(new UserApp(id, id, "", id + "@zmail.com", ""));
        }
        */
        TextView title = (TextView) findViewById(R.id.user_app);
        String titleText = title.getText() + ( app.getUserApp() == null ? "" : " - " + app.getUserApp().getName());
        title.setText(titleText);


        Log.d(Constants.TAG_CHAT,"ON CREATE");
        chatClientService = App.getChatClientService();
        Log.d(Constants.TAG_CHAT,"chatClientService = " + chatClientService);
        chatClientService.registerReceivers(makeReceivers());
        attemptLogin();

    }

    public Map<String, BroadcastReceiver> makeReceivers(){
        Map<String, BroadcastReceiver> map = new HashMap<String, BroadcastReceiver>();
        map.put(ChatClientService.SocketEvent.CONNECT, connectReceiver);
        map.put(ChatClientService.SocketEvent.USER_JOIN, userJoinReceiver);
        map.put(ChatClientService.SocketEvent.NEW_MESSAGE, newMessageReceiver);
        map.put(ChatClientService.SocketEvent.LIST_CONTACT, listContactReceiver);
        map.put(ChatClientService.SocketEvent.UPDATE_CONTACT, updateContactReceiver);
        return map;
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
        if ( !chatClientService.isLogin() ) {
            JSONObject user = new JSONObject();
            try {
                String name = Utility.getDeviceUniqueID(getContentResolver());
                UserApp userApp = App.getInstance().getUserApp();
                user.put("name", userApp.getName());
                user.put("email", userApp.getEmail());
                user.put("phone", userApp.getPhone());
                chatClientService.emit(ChatClientService.SocketEmit.DO_LOGIN, user);
            } catch (JSONException e) {
                Log.e(Constants.TAG_CHAT, e.getMessage(), e);
            }
        }
    }

    private BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            attemptLogin();
        }
    };

    private BroadcastReceiver userJoinReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            JSONObject user;
            try {
                JSONObject joData = new JSONObject(data);
                user = joData.getJSONObject("user");
                Log.d(Constants.TAG_CHAT, "User Join " + user.getString("email"));
            } catch (JSONException e) {
                Log.e(Constants.TAG_CHAT, e.getMessage(), e);
                return;
            }
            //retrive contact
            ChatTaskService.startActionUpdateContact(getActivity());
        }
    };

    private BroadcastReceiver newMessageReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            JSONObject from;
            String message;
            try {
                JSONObject joData = new JSONObject(data);
                from = joData.getJSONObject("from");
                message = joData.getString("message");

                //String id = from.getString("id");
                String name = from.getString("name");
                String email = from.getString("email");
                String phone = from.getString("phone");

                //Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT );
                Log.d(Constants.TAG_CHAT, "message2 = " + message);
                //removeTyping(username);
                final ChatContactFragment fragment0 = (ChatContactFragment) pagerAdapter.getItem(1);
                ChatContact contact = fragment0.getContact(email);
                if (contact == null) {
                    contact = new ChatContact(null, name, "", email, phone, ChatContactType.PRIVATE);
                }
                if (contact.getActive() != 1) {
                    final ChatMessage lastMessage = ChatHelper.createMessage(message, MessageType.IN);
                    final ChatContactHistoryFragment fragment1 = (ChatContactHistoryFragment) pagerAdapter.getItem(0);
                    fragment1.updateContactHistory(contact, 1, lastMessage);
                }

            } catch (JSONException e) {
                Log.e(Constants.TAG_CHAT, e.getMessage(), e);
            }

        }
    };

    private BroadcastReceiver listContactReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");
            JSONArray contacts = new JSONArray();
            try {
                JSONObject joData = new JSONObject(data);
                contacts = joData.getJSONArray("contacts");
                //Toast.makeText(getApplicationContext(), "Login1 " + isLogin, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e(Constants.TAG_CHAT, e.getMessage(), e);
                return;
            }
            Log.d(Constants.TAG_CHAT, "list contacts = " + contacts);
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
                    Log.d(Constants.TAG_CHAT,e.getMessage(), e);
                }
            }

            ChatContactFragment fragment = (ChatContactFragment)pagerAdapter.getItem(1);
            fragment.updateContact(contactList);

        }
    };

    private BroadcastReceiver updateContactReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("data");

            boolean remove = false;
            JSONObject contact = null;
            try {
                JSONObject joData = new JSONObject(data);
                remove =joData.getBoolean("remove");
                contact = joData.getJSONObject("contact");

                Log.d(Constants.TAG_CHAT, "update contact = " + contact);
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
                Log.e(Constants.TAG_CHAT, e.getMessage(), e);
                return;
            }
        }
    };

    //END SOCKET METHOD BLOCK

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG_CHAT,"ON RESUME");
        //reset page adapter
        //pagerAdapter = new ChatTabPagerAdapter(getSupportFragmentManager());
        //viewPager.setAdapter(pagerAdapter);
        Log.d(Constants.TAG_CHAT, "chatClientService=" + chatClientService);
        chatClientService.registerReceivers(makeReceivers());

        //retrive contact
        ChatTaskService.startActionUpdateContact(getActivity());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatClientService.unregisterReceivers();
    }


}
