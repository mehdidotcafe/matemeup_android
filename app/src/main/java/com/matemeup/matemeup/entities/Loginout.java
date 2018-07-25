package com.matemeup.matemeup.entities;

import android.app.Activity;

import com.matemeup.matemeup.Layout;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class Loginout {

    public static void login(Activity a, JSONObject data) {
        String token;
        Request req = Request.getInstance();
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        String userId = status.getSubscriptionStatus().getUserId();
        JSONObject obj = new JSONObject();

        try {
            obj.put("id", userId);
            token = data.getString("token");
        } catch (JSONException e) {
            token = "";
        }

        JWT.putAPI(a, token);
        req.addQueryString("token", token);
        req.send(a, "users/notificationPush", "POST", null, obj, new Callback()
        {
            @Override
            public void success(Object objData)
            {
            }

            @Override
            public void fail(String error)
            {
            }
        });

        try {
            ConnectedUser.set(data.getJSONObject("user"));
        } catch (JSONException e) {}
    }

    public static void logout(Activity a) {
        JWT.unsetAPI(a);
        JWT.unsetMM(a);
        ConnectedUser.unset();
        Request.getInstance().unsetQueryString();
        MMUWebSocket.delInstance();
        Layout.unsetNotif();
    }
}
