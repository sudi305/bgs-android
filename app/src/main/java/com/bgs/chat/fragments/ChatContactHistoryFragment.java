package com.bgs.chat.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bgs.chat.ChatPageActivity;
import com.bgs.dheket.R;
import com.bgs.chat.adapters.ChatContactHistoryListAdapter;
import com.bgs.chat.model.ChatContact;
import com.bgs.chat.model.ChatContactHistory;
import com.bgs.chat.model.ChatMessage;

import java.util.ArrayList;

public class ChatContactHistoryFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "num";

    private int mNum;
    private ListView contactHistoryListView;
    private ChatContactHistoryListAdapter listAdapter;
    private ArrayList<ChatContactHistory> chatContactHistories;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ChatContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatContactHistoryFragment newInstance(int param1) {
        ChatContactHistoryFragment fragment = new ChatContactHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNum = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_chatcontact_history, container, false);

        chatContactHistories = new ArrayList<ChatContactHistory>();

        contactHistoryListView = (ListView) rootView.findViewById(R.id.chat_contact_history_list_view);
        contactHistoryListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        contactHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatPageActivity.class);
                ChatContact contact = (chatContactHistories.get((int)id)).getContact();
                //Toast.makeText(getActivity(), contact.getName(), Toast.LENGTH_LONG).show();
                contact.setActive(1);
                intent.putExtra("chatContact", contact);
                getActivity().startActivity(intent);
            }
        });

        listAdapter = new ChatContactHistoryListAdapter(chatContactHistories, getActivity());
        contactHistoryListView.setAdapter(listAdapter);

        /*
        ChatContactHistory history = new ChatContactHistory();
        history.setContact(new ChatContact() {{
            setName("User2");
            setEmail("usertwo@gmail.com");
            setContactType(ChatContactType.PRIVATE);
        }});
        history.setNewMessageCount(13);
        history.setLastChatMessage(new ChatMessage() {{
            setMessageText("test");
            setSenderName("");
            setMessageType(MessageType.SEND);
            setMessageStatus(MessageStatus.SENT);
        }});
        chatContactHistories.add(history);
        */

        return rootView;
    }

    private void showEmptyMessage() {
        if ( chatContactHistories.size() == 0 ) {
            TextView emptyView = (TextView) getView().findViewById(R.id.chat_contact_history_empty);
            if ( emptyView != null ) emptyView.setVisibility(TextView.VISIBLE);
        }
    }

    public void updateHistory(final ArrayList<ChatContactHistory> contactHistoriesList) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatContactHistories.clear();
                for(ChatContactHistory contactHistory : contactHistoriesList ) {
                    chatContactHistories.add(contactHistory);
                    listAdapter.notifyDataSetChanged();
                    //Log.d(getResources().getString(R.string.app_name), "c:" + contact.getName());
                }
                showEmptyMessage();
            }
        });
    }

    public void updateContactHistory(final ChatContact contact, final int newMessageCount, final ChatMessage lastChatMessage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boolean exist = false;
                ChatContactHistory _contactHistory = null;
                for(int i = chatContactHistories.size() - 1; i>=0; i-- ) {
                    _contactHistory = chatContactHistories.get(i);
                    if(_contactHistory.getContact().getEmail().equalsIgnoreCase(contact.getEmail())) {
                        exist = true;
                        //update other member
                        _contactHistory.setNewMessageCount(newMessageCount);
                        _contactHistory.setLastChatMessage(lastChatMessage);
                        break;
                    }
                }
                //add if not exist
                if ( !exist) {
                    //create new
                    ChatContactHistory history = new ChatContactHistory();
                    history.setContact(contact);
                    history.setNewMessageCount(newMessageCount);
                    history.setLastChatMessage(lastChatMessage);
                    chatContactHistories.add(history);
                }

                listAdapter.notifyDataSetChanged();
                //Log.d(getResources().getString(R.string.app_name), "c:" + contact.getName());
                showEmptyMessage();
            }
        });

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        for(ChatContactHistory item : chatContactHistories) {
            item.getContact().setActive(0);
        }
    }
}
