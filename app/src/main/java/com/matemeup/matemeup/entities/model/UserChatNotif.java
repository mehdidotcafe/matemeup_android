package com.matemeup.matemeup.entities.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserChatNotif extends UserChat {
    public int notif;


    public UserChatNotif(int _id, String _name, String _avatar, int _notif)
    {
        super(_id, _name, _avatar);
        notif = _notif;
    }

    public UserChatNotif(UserChat user) {
        super(user);
        notif = 0;
    }

    public UserChatNotif(JSONObject obj) {
        super(obj);
        try {
            notif = obj.getInt("unseen_messages_count");
        } catch (JSONException e) {
            notif = 0;
        }
    }

    public static List<UserChat> fromJson(JSONArray array)
    {
        List<UserChat> users = new ArrayList<>();

        try {
            for(int i=0; i<array.length(); i++){
                JSONObject jsonUser = array.getJSONObject(i);

                users.add(new UserChatNotif(jsonUser));
            }
        } catch (JSONException e) {}
        return users;
    }
}
