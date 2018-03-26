package com.matemeup.matemeup.entities;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class MMRequest extends Request {

    private static final String MM_URL = "http://192.168.0.100:8011/";

    MMRequest(Context ctx) {
        super(MM_URL);
    }

    public void sendFile(Context ctx, String route, String pathname) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("token", JWT.getMM(ctx));
        } catch (JSONException e) {}
        super.sendFile(ctx, route, pathname, obj);
    }

    public void send(Context ctx, String route, String method, JSONObject body, Callback cb) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("token", JWT.getMM(ctx));
        } catch (JSONException e) {}
        super.send(ctx, route, method, obj, body, cb);
    }
}
