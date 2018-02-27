package com.matemeup.matemeup.entities.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserChat implements Serializable<UserChat>{
    public int id;
    public String name;
    public String avatar;

    public UserChat(int _id, String _name, String _avatar)
    {
        id = _id;
        name = _name;
        avatar = _avatar;
    }

    public UserChat(UserChat uc) {
        id = uc.id;
        name = uc.name;
        avatar = uc.avatar;
    }

    public String toString() {
        return id + " " + name + " " + avatar;
    }

    public UserChat(JSONObject obj) {
        fromJSON(obj);
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("id", id);
            obj.put("name", name);
            obj.put("avatar", avatar);
        } catch (JSONException e) {return null;}

        return obj;
    }

    public UserChat fromJSON(JSONObject obj)
    {
        try {
            id = obj.getInt("id");
            name = obj.getString("name");
            avatar = obj.getString("avatar");
        } catch (JSONException e) {return null;}
        return this;
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
