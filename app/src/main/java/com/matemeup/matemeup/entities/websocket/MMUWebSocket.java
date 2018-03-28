package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;

import com.github.nkzawa.emitter.Emitter;
import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.JWT;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.containers.Triple;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MMUWebSocket extends WebSocket {
    private static final String URL = "http://192.168.0.103:8888/";
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
           Request req = Request.getInstance();

           isInit = true;
           Callback cb = new Callback()
           {
               @Override
               public void success(Object objData)
               {
                   JSONObject data = (JSONObject)objData;
                   String token;

                   try {
                       token = data.getString("token");
                   } catch (JSONException e) {
                       token = "";
                   }

                   System.out.println("dans success");
                   emit(EHLO, token, new WebSocketCallback() {
                       public void onMessage(String message, final Object... args) {
                           if (hasReceiveEhlo == false)
                           {
                               if ((Boolean)args[0] == true)
                               {
                                   hasReceiveEhlo = true;
                                   System.out.println("emit to all cached");
                                   emitToAllCached();
                               }
                           }
                       }
                   });
               }

               @Override
               public void fail(String error) {
                   System.out.println("error getting mm token " + error);
               }
           };

           System.out.println("WebSocket init req");
           req.send(activity.getApplicationContext(), "matchmaker/token", "GET", null, null, cb);
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
        for (int i = 0; i < cachedEmit.size(); i++)
        {
            emit(cachedEmit.get(i).first, cachedEmit.get(i).second, cachedEmit.get(i).third);
        }
        cachedEmit.clear();
    }

    @Override
    public Emitter.Listener emit(String message, Object data, WebSocketCallback callback)
    {
        if (hasReceiveEhlo == false && !message.equals(EHLO))
        {
            cachedEmit.add(new Triple(message, data, callback));
            return null;
        }
        else
        {
            return super.emit(message, data, callback);
        }
    }
}
