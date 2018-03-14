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

public class FriendsActivity extends BackToolbarActivity {
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
            }
        }, new Callback() {
            @Override
            public void success(Object obj) {
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


    private void getFriends() {
        ws.emit("friend.accepted.get", new JSONObject(), new WebSocketCallback()
        {
            public void onMessage(final String message, final Object... args)
            {
                friends.addAll(UserChat.fromJson((JSONArray)args[0]));
                renderFriends();
            }
        });
    }

    private void getPendings() {
        ws.emit("friend.pending.get", new JSONObject(), new WebSocketCallback()
        {
            public void onMessage(final String message, final Object... args)
            {
                pendings.addAll(UserChat.fromJson((JSONArray)args[0]));
                renderPendings();
            }
        });
    }

    private void initSocket() {
        ws = new MMUWebSocket(this);

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
                        System.out.println(resp.getBoolean("state"));
                        Alert.ok(self, "Ajout d'un ami", resp.getBoolean("state") == true ? "Demande d'ami envoyée" : "Vous avez deja cette personne en ami", new AlertCallback() {});
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(args[0]);
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

        friends = new ArrayList<UserChat>();
        pendings = new ArrayList<UserChat>();
        initSocket();
        initNewButton();
        getFriends();
        getPendings();
    }
}
