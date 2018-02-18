package com.matemeup.matemeup.entities.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserChat {
    public final int id;
    public final String name;
    public final String avatar;

    public UserChat(int _id, String _name, String _avatar)
    {
        id = _id;
        name = _name;
        avatar = _avatar;
    }

    public static List<UserChat> fromJson(JSONArray array)
    {
        List<UserChat> users = new ArrayList<>();

        try {
            for(int i=0; i<array.length(); i++){
                JSONObject jsonUser = array.getJSONObject(i);

                users.add(new UserChat(jsonUser.getInt("id"), jsonUser.getString("name"), jsonUser.getString("avatar")));
            }
        } catch (JSONException e) {}
        return users;
    }
}
