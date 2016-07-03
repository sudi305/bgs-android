package com.bgs.chat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bgs.chat.viewmodel.ChatHistory;
import com.bgs.dheket.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatContactHistoryListAdapter extends BaseAdapter {

    private ArrayList<ChatHistory> chatContactHistories;
    private Context context;
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm");

    public ChatContactHistoryListAdapter(ArrayList<ChatHistory> chatContactHistories, Context context) {
        this.chatContactHistories = chatContactHistories;
        this.context = context;

    }

    @Override
    public int getCount() {
        return chatContactHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return chatContactHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        ChatHistory contact = chatContactHistories.get(position);
        ViewHolder holder;

        if (convertView == null) {
            v = LayoutInflater.from(context).inflate(R.layout.chat_contact_history_item, null, false);
            holder = new ViewHolder();
            holder.picture = (ImageView) v.findViewById(R.id.chat_contact_picture);
            holder.nameTextView = (TextView) v.findViewById(R.id.chat_contact_name);
            //holder.emailTextView = (TextView) v.findViewById(R.id.chat_contact_email);
            holder.timeTextView = (TextView) v.findViewById(R.id.chat_contact_time);
            holder.messageTextView = (TextView) v.findViewById(R.id.chat_contact_msg);
            holder.msgCountTextView = (TextView) v.findViewById(R.id.chat_contact_msg_count);

            v.setTag(holder);
        } else {
            v = convertView;
            holder = (ViewHolder) v.getTag();
        }

        if ( contact != null ) {
            //holder1.picture.setBackground();
            holder.nameTextView.setText(contact.getContact().getName());
            holder.messageTextView.setText("");
            if ( contact.getLastChatMessage() != null ) {
                String msg = contact.getLastChatMessage().getMessageText();
                if ( msg.length() > 40 ) msg = msg.substring(0, 40) + "...";
                holder.messageTextView.setText( msg);
            }
            if ( contact.getNewMessageCount() > 0 ) {
                holder.msgCountTextView.setVisibility(TextView.VISIBLE);
                holder.msgCountTextView.setText(String.valueOf(contact.getNewMessageCount()));
            }

        }

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatHistory contact = chatContactHistories.get(position);
        return contact.getContact().getContactType().ordinal();
    }


    private class ViewHolder {
        public ImageView picture;
        public TextView nameTextView;
        //public TextView emailTextView;
        public TextView messageTextView;
        public TextView timeTextView;
        public TextView msgCountTextView;

    }
}
