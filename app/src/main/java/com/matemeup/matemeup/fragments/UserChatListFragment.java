package com.matemeup.matemeup.fragments;

import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.matemeup.matemeup.ChatActivity;
import com.matemeup.matemeup.R;
import com.matemeup.matemeup.adapters.UserChatAdapter;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONArray;

import java.util.List;

public class UserChatListFragment extends ListFragment {
    private WebSocket ws;
    protected String GET_USER_MSG = "";
    private List<UserChat> users;
    private UserChatAdapter adapter = null;

    private void initAdapter() {
        adapter = new UserChatAdapter(getActivity().getApplicationContext(), R.layout.chat_user_list, users);

        setListAdapter(adapter);
    }

    public void setUsers(List<UserChat> newUsers) {
        users = newUsers;

        if (adapter == null)
            initAdapter();
        else
            adapter.setList(users);
    }

    private void getUsers(List<UserChat> _users)
    {
        users = _users;
        if (adapter == null)
            initAdapter();
    }

    public void goToChat(int position)
    {
        UserChat user = users.get(position);

        IntentManager.goTo(getActivity().getApplicationContext(), ChatActivity.class, user.toJSONObject());
    }

    public void initSocket()
    {
        ws = new MMUWebSocket(getActivity());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        goToChat(position);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSocket();
        ws.emit(GET_USER_MSG, null, new WebSocketCallback() {
            @Override
            public void onMessage(final String message, final Object... args)
            {
                JSONArray users = (JSONArray)args[0];
                if (getActivity() != null)
                {
                    getUsers(UserChat.fromJson(users));
                }
            }
        });
    }

}
