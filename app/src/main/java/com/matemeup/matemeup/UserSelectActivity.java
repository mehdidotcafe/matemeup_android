package com.matemeup.matemeup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.matemeup.matemeup.adapters.UserChatAdapter;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.UserChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserSelectActivity extends SearchToolbarActivity {
    private UserChatAdapter adapter;
    private List<UserChat> users;
    private Class<?> nextActivity;

    private void initAdapter() {
        final Context self = this;
        users = new ArrayList<>();
        adapter = new UserChatAdapter(this, R.layout.chat_user_list, users);
        ListView lv = (ListView)findViewById(R.id.user_list_view);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserChat user = users.get(position);

                IntentManager.end((Activity)self, user.toJSONObject());
            }
        });
    }

    public void onTextChange(String needle) {
        JSONObject body = new JSONObject();
        String route = "users/chat/get";
        Request req = new Request() {
            public void success(JSONObject result) {
                try {
                    users = UserChat.fromJson(result.getJSONArray("users"));
                    System.out.println("users " + users);
                    adapter.setList(users);
                } catch (JSONException e) {}
            }
        };

        try {
            body.put("needle", needle);
            req.send(this, route, "GET", null, body);
        } catch (JSONException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_user_select);
        initAdapter();
    }
}
