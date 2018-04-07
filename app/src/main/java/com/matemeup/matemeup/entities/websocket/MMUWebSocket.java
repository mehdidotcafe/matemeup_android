package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;

import com.matemeup.matemeup.MainActivity;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.Request;
import com.matemeup.matemeup.entities.containers.Triple;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class MMUWebSocket extends WebSocket{
    private static final String URL = "https:/www.matemeup.com:8444/";
    //private static final String URL = "http://192.168.0.103:8888/";
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

    private MMUWebSocket(Activity activity)
    {
       super(activity, URL);
        cachedEmit = new ArrayList();
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

                   emit(EHLO, token, new WebSocketCallback() {
                       public void onMessage(String message, final Object... args) {
                           System.out.println("message " + message);
                           if (hasReceiveEhlo == false)
                           {
                               if ((Boolean)args[0] == true) {
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

           System.out.println("WebSocket init req");
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
        if (hasReceiveEhlo == false && !message.equals(EHLO))
        {
            cachedEmit.add(new Triple(message, data, callback));
            return null;
        }
        else
        {
            System.out.println("Ca emit sur la socket !!!!!!!!!!!!!!!!!!! " + message);
            return super.emit(message, data, callback);
        }
    }
}
