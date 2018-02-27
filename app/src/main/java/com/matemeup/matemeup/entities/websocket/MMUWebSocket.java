package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.containers.Triple;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MMUWebSocket extends WebSocket {
    private static final String URL = "http://192.168.0.39:8011/";
    private static final String EHLO = "ehlo";
    private static Boolean isInit = false;
    private static Boolean hasReceiveEhlo = false;
    private static List<MMUWebSocket> instances = new ArrayList();
    private List<Triple<String, Object, WebSocketCallback>> cachedEmit;

    public MMUWebSocket(Activity activity)
    {
       super(activity, URL);
       cachedEmit = new ArrayList();
       instances.add(this);
       if (isInit == false)
       {
           isInit = true;
           Request req = new Request()
           {
               @Override
               public void success(JSONObject data)
               {
                   String token;

                   try {
                       token = data.getString("token");
                   } catch (JSONException e) {
                       token = "";
                   }

                   emit(EHLO, token, new WebSocketCallback() {
                       public void onMessage(String message, final Object... args) {
                           if (hasReceiveEhlo == false)
                           {
                               if ((Boolean)args[0] == true)
                               {
                                   hasReceiveEhlo = true;
                                   emitToAllCached();
                               }
                           }
                       }
                   });
               }
           };

           req.send(activity.getApplicationContext(), "matchmaker/token", "GET", null, null);
       }
    }

    private void emitToAllCached() {
        for (int i = 0; i < instances.size(); i++)
        {
            instances.get(i).emitCached();
        }
    }

    private void emitCached()
    {
        System.out.println("emitCached header " + this);
        for (int i = 0; i < cachedEmit.size(); i++)
        {
            emit(cachedEmit.get(i).first, cachedEmit.get(i).second, cachedEmit.get(i).third);
        }
        cachedEmit.clear();
    }

    @Override
    public void emit(String message, Object data, WebSocketCallback callback)
    {
        if (hasReceiveEhlo == false && !message.equals(EHLO))
        {
            System.out.println("Caching " + message);
            cachedEmit.add(new Triple(message, data, callback));
        }
        else
        {
            System.out.println("emitting " + message);
            super.emit(message, data, callback);
        }
    }
}
