package com.matemeup.matemeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.matemeup.matemeup.adapters.FriendAdapter;
import com.matemeup.matemeup.adapters.PendingFriendAdapter;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.navigation.BackButton;
import com.matemeup.matemeup.entities.rendering.Alert;
import com.matemeup.matemeup.entities.rendering.AlertCallback;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends Layout{
    private WebSocket ws;
    private PendingFriendAdapter pendingAdapter;
    private FriendAdapter friendAdapter;
    private List<UserChat> friends;
    private List<UserChat> pendings;

    private void renderFriends() {
        friendAdapter = new FriendAdapter(FriendsActivity.this, R.layout.item_friend, friends);

        RecyclerView fl = (RecyclerView)findViewById(R.id.friend_list);
        fl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fl.setAdapter(friendAdapter);
    }

    private void renderPendings() {
        pendingAdapter = new PendingFriendAdapter(FriendsActivity.this, R.layout.item_pending_friend, pendings, new Callback() {
            @Override
            public void success(Object obj) {
                pendings.remove(obj);
                pendingAdapter.notifyDataSetChanged();
                decrFriendNotifCount();
            }
        }, new Callback() {
            @Override
            public void success(Object obj) {
                decrFriendNotifCount();
                pendings.remove(obj);
                pendingAdapter.notifyDataSetChanged();
            }
        });
        RecyclerView pl = (RecyclerView)findViewById(R.id.pending_list);
        pl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        pl.setAdapter(pendingAdapter);
        if (pendings.size() == 0)
        {
            findViewById(R.id.no_pending_friend_message).setVisibility(View.VISIBLE);
            findViewById(R.id.pending_list).setVisibility(View.GONE);
        }
    }


    private Boolean userNotInList(List<UserChat> list, UserChat user) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == user.id)
                return false;
        }
        return true;
    }

    private void getFriends() {
        ws.emit("friend.accepted.get", new JSONObject(), new WebSocketCallback()
        {
            public void onMessage(final String message, final Object... args)
            {
                List<UserChat> nfriends = UserChat.fromJson((JSONArray)args[0]);

                for (int i= 0; i < nfriends.size(); i++) {
                    if (userNotInList(friends, nfriends.get(i)))
                        friends.add(nfriends.get(i));
                }
                renderFriends();
            }
        });
    }

    private void getPendings() {
        ws.emit("friend.pending.get", new JSONObject(), new WebSocketCallback()
        {
            public void onMessage(final String message, final Object... args)
            {
                List<UserChat> npendings = UserChat.fromJson((JSONArray)args[0]);

                for (int i = 0; i < npendings.size(); i++) {
                    if (userNotInList(pendings, npendings.get(i)))
                        pendings.add(npendings.get(i));
                }
                renderPendings();
            }
        });
    }

    private void initSocket() {
        ws = MMUWebSocket.getInstance(this);

        ws.on("friend.accept.new", new WebSocketCallback() {
            @Override
            public void onMessage(String message, Object... args) {
                friends.add(new UserChat((JSONObject)args[0]));
                friendAdapter.notifyItemInserted(friends.size() - 1);
            }
        });

        ws.on("friend.add.new", new WebSocketCallback() {
            @Override
            public void onMessage(String message, Object... args) {
                if (findViewById(R.id.no_pending_friend_message).getVisibility() == View.VISIBLE)
                {
                    findViewById(R.id.no_pending_friend_message).setVisibility(View.GONE);
                    findViewById(R.id.pending_list).setVisibility(View.VISIBLE);
                }

                pendings.add(new UserChat((JSONObject)args[0]));
                pendingAdapter.notifyItemInserted(pendings.size() - 1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            final FriendsActivity self = this;
            JSONObject obj = new JSONObject();
            UserChat user = new UserChat(Serializer.unserialize(data.getStringExtra("params")));

            try {
                obj.put("friendId", user.id);
            } catch (JSONException e) {}
            ws.emit("friend.add", obj, new WebSocketCallback() {
                @Override
                public void onMessage(String message, Object... args) {
                    JSONObject resp = (JSONObject)args[0];

                    try {
                        Alert.ok(self, getResources().getString(R.string.add_friend), resp.getBoolean("state") ? getResources().getString(R.string.friend_invitation_sent) : getResources().getString(R.string.user_already_friend), new AlertCallback() {});
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void goToUserSelectView() {
        IntentManager.goToRet(this, this, UserSelectActivity.class);
    }

    public void initNewButton() {
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.new_friend_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserSelectView();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_friends);

        IntentManager.setCurrentActivity(FriendsActivity.class);
        friends = new ArrayList<UserChat>();
        pendings = new ArrayList<UserChat>();
        initSocket();
        initNewButton();
        getFriends();
        getPendings();
    }
}
