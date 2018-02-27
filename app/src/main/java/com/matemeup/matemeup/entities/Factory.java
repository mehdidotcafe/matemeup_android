package com.matemeup.matemeup.entities;

import com.matemeup.matemeup.entities.model.Serializable;
import com.matemeup.matemeup.entities.model.UserChat;

import org.json.JSONObject;

public class Factory{
    private String name;

    public Factory(String n) {
        name = n;
    }

    Serializable newInstanceFromJSONObject(JSONObject obj) {
        if (name == "UserChat")
            return new UserChat(obj);
        return null;
    }
}
