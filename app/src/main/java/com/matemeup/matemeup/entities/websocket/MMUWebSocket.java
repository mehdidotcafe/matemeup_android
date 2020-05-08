package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;

import com.matemeup.matemeup.MainActivity;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.Constants;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.containers.Triple;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class MMUWebSocket extends WebSocket{
    private static final String URL = Constants.url;
    private static final String EHLO = "ehlo";
    private static MMUWebSocket instance = null;

    private Boolean isInit = false;
    private Boolean hasReceiveEhlo = false;
    private List<Triple<String, Object, WebSocketCallback>> cachedEmit;

    public static MMUWebSocket getInstance(Activity activity) {
        if (instance == null)
            instance = new MMUWebSocket(activity);
        return instance;
    }

    @Override
    protected void onConnectError() {
        MMUWebSocket.delInstance();
    }

    public static void delInstance() {
        instance = null;
    }

    private MMUWebSocket(Activity activity)
    {
       super(activity, URL);
       cachedEmit = new ArrayList();
       if (!isInit)
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

                   emit(EHLO, token, new WebSocketCallback() {
                       public void onMessage(String message, final Object... args) {
                           if (!hasReceiveEhlo)
                           {
                               if ((Boolean)args[0]) {
                                   hasReceiveEhlo = true;
                                   execCachedEmit();
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

           req.send(activity.getApplicationContext(), "matchmaker/token", "GET", null, null, cb);
       }
    }

    public void execCachedEmit() {
        for (int i = 0; i < cachedEmit.size(); i++) {
            emit(cachedEmit.get(i).first, cachedEmit.get(i).second, cachedEmit.get(i).third);
        }
        cachedEmit.clear();
    }



    @Override
    public Emitter.Listener emit(String message, Object data, WebSocketCallback callback)
    {
        if (!hasReceiveEhlo && !message.equals(EHLO))
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
