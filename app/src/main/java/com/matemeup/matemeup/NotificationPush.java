package com.matemeup.matemeup;

import com.matemeup.matemeup.entities.Callback;

import java.util.ArrayList;

public class NotificationPush {
    public static NotificationPush instance = null;
    private String token = null;
    public ArrayList<Callback> callbacks = new ArrayList<>();

    public static NotificationPush getInstance() {
        if (instance == null) {
            instance = new NotificationPush();
        }
        return instance;
    }

    public void execCallbacks() {
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.get(i).success(token);
        }
        callbacks.clear();
    }

    public void setToken(String token) {
        this.token = token;
        execCallbacks();
    }

    public void whenToken(Callback cb) {
        if (token == null)
            callbacks.add(cb);
        else
            cb.success(token);
    }
}
