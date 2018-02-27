package com.matemeup.matemeup.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.matemeup.matemeup.ChatActivity;
import com.matemeup.matemeup.R;
import com.matemeup.matemeup.adapters.UserChatAdapter;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;

import org.json.JSONArray;

import java.util.List;

public class ChatFragment extends UserChatListFragment {
    public ChatFragment() {
        super();
        GET_USER_MSG = "global.chat.user.normal.get";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}
