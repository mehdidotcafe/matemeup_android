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

public class ChatFragment extends ListFragment{
    public ChatFragment(){}

    private WebSocket ws;
    private static final String GET_USER_MSG = "global.chat.user.normal.get";
    private List<UserChat> users;

    private void getUsers(List<UserChat> _users)
    {
        users = _users;
        UserChatAdapter adapter = new UserChatAdapter(getActivity().getApplicationContext(), R.layout.chat_user_list, users);
        //ListView list = getActivity().findViewById(R.id.list);

        setListAdapter(adapter);
    }

    public void goToChat(int position)
    {
        System.out.println(users.get(position).name);
        IntentManager.goTo(getActivity().getApplicationContext(), ChatActivity.class);
    }

    public void initSocket()
    {
        ws = new MMUWebSocket(getActivity()) {
            @Override
            public void onMessage(final String message, final Object... args)
            {
                JSONArray users = (JSONArray)args[0] ;
                if (message == GET_USER_MSG)
                {
                    getUsers(UserChat.fromJson(users));
                }
            }
        };
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        goToChat(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSocket();
        ws.emit(GET_USER_MSG, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }
}
