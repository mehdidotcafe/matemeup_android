package com.matemeup.matemeup;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.matemeup.matemeup.adapters.OnItemClickListener;
import com.matemeup.matemeup.adapters.UserChatAdapter;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.model.UserChatNotif;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserSelectActivity extends SearchToolbarActivity {
    private UserChatAdapter adapter;
    private List<UserChat> users;

    private void initAdapter() {
        final Context self = this;
        users = new ArrayList<>();
        adapter = new UserChatAdapter(this, R.layout.item_chat_user, users, new OnItemClickListener() {
            @Override
            public void onItemClick(Object item) {
                IntentManager.end((Activity)self, ((UserChat)item).toJSONObject());
            }
        });
        RecyclerView rv = (RecyclerView)findViewById(R.id.user_recycler_view);

        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
    }

    public void onTextChange(String needle) {
        JSONObject body = new JSONObject();
        String route = "users/chat/get";
        Request req = Request.getInstance();
        Callback cb = new Callback()
        {
            public void success(Object data) {
                JSONObject result = (JSONObject)data;
                try {
                    users = UserChat.fromJson(result.getJSONArray("users"));
                    adapter.setList(users);
                } catch (JSONException e) {}
            }
        };

        try {
            body.put("needle", needle);
            req.send(this, route, "GET", null, body, cb);
        } catch (JSONException e) {}
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_user_select);
        IntentManager.setCurrentActivity(UserSelectActivity.class);
        initAdapter();
    }
}
