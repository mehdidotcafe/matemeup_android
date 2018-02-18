package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;

import com.google.gson.JsonObject;
import com.matemeup.matemeup.entities.Request;

import java.util.HashMap;
import java.util.Map;

public class MMUWebSocket extends WebSocket {
    private static final String URL = "http://192.168.0.14:8011/";
    private static final String EHLO = "ehlo";
    private static Boolean isInit = false;
    private Boolean hasReceiveEhlo = false;
    private Map<String, Object> cachedEmit;

    public MMUWebSocket(Activity activity)
    {
       super(activity, URL);
       cachedEmit = new HashMap();
       if (isInit == false)
       {
           isInit = true;
           Request req = new Request()
           {
               @Override
               public void success(JsonObject data)
               {
                   String token = data.get("token").getAsString();

                   emit(EHLO, token);
               }
           };

           req.send(activity.getApplicationContext(), "matchmaker/token", "GET", null, null);
       }
    }

    private void emitCached()
    {
        for (Map.Entry<String, Object> entry : cachedEmit.entrySet())
        {
            emit(entry.getKey(), entry.getValue());
        }
    }

    public void onMessage(final String message, final Object... args)
    {

    }

    @Override
    public void listener(final String message, final Object... args)
    {
        if (hasReceiveEhlo == false)
        {
            if (message == EHLO && (Boolean)args[0] == true)
            {
                hasReceiveEhlo = true;
                emitCached();
            }
        }
        else
            onMessage(message, args);
    }

    @Override
    public void emit(String message, Object data)
    {
        System.out.println("emitting " + message);
        if (hasReceiveEhlo == false && !message.equals(EHLO))
        {
            cachedEmit.put(message, data);
        }
        else
            super.emit(message, data);
    }
}
