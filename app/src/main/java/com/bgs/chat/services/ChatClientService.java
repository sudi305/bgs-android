package com.bgs.chat.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bgs.chat.viewmodel.ChatHelper;
import com.bgs.common.Constants;
import com.bgs.dheket.App;
import com.bgs.domain.chat.model.ChatContact;
import com.bgs.domain.chat.model.ChatMessage;
import com.bgs.domain.chat.model.MessageType;
import com.bgs.domain.chat.model.UserType;
import com.bgs.domain.chat.repository.ContactRepository;
import com.bgs.domain.chat.repository.IContactRepository;
import com.bgs.domain.chat.repository.IMessageRepository;
import com.bgs.domain.chat.repository.MessageRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhufre on 6/26/2016.
 */
public class ChatClientService extends Service {
    private ChatEngine chatEngine;
    private IMessageRepository messageRepository;
    private IContactRepository contactRepository;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(Constants.TAG_CHAT, "onStartCommand");
        messageRepository = new MessageRepository(getApplicationContext());
        contactRepository = new ContactRepository(getApplicationContext());

        chatEngine = App.getChatEngine();
        chatEngine.registerReceivers( makeReceivers());

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public Map<String, BroadcastReceiver> makeReceivers(){
        Map<String, BroadcastReceiver> map = new HashMap<String, BroadcastReceiver>();
        map.put(ChatEngine.SocketEvent.NEW_MESSAGE, newMessageReceiver);
        return map;
    }

    private BroadcastReceiver newMessageReceiver = new BroadcastReceiver()  {
        @Override
        public void onReceive(Context context, Intent intent){
            JSONObject joData;
            JSONObject from;
            String message, email, name, phone, picture, type;
            try {
                String data = intent.getStringExtra("data");
                joData = new JSONObject(data);
                from = joData.getJSONObject("from");
                message = joData.getString("message");

                name = from.getString("name");
                email = from.getString("email");
                phone = from.getString("phone");
                picture = from.getString("picture");
                type = from.getString("type");

                Log.d(Constants.TAG_CHAT, "message2 = " + message);
                //update new message count - option menu


            } catch (JSONException e) {
                Log.e(Constants.TAG_CHAT,e.getMessage(), e);
                return;
            }

            ChatContact contact = contactRepository.getContactByEmail(email, UserType.parse(type));
            Log.d(Constants.TAG_CHAT, String.format("from=%s\r\nmessage=%s ", from, message));
            if ( contact == null) {
                contact = new ChatContact(name, picture, email, phone, UserType.parse(type));
            } else {
                contact.setName(name);
                contact.setPicture(picture);
                contact.setPhone(phone);
                contact.setUserType(UserType.parse(type));

            }
            contactRepository.createOrUpdate(contact);
            //removeTyping(username);
            ChatMessage msg = ChatHelper.createMessage(contact.getId(), message, MessageType.IN);
            messageRepository.createOrUpdate(msg);
        }
    };


}

