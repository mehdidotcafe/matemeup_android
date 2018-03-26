package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebSocket {
    protected Socket socket;
    protected Activity activity;
    protected List<String> currentRequest;

    public WebSocket(Activity _activity, String url)
    {
        try {
            socket = IO.socket(url);
            currentRequest = new ArrayList();
        } catch (URISyntaxException e) {
            System.out.println("Connection fail");
            return ;
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        socket.connect();
        activity = _activity;
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("SOCKET ERROR " + args[0]);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "Unable to connect to NodeJS server", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    public void off(String message, Emitter.Listener callback) {
        socket.off(message, callback);
    }

    public Emitter.Listener on(String message, WebSocketCallback callback) {
        return on(message, callback, false);
    }

    public Emitter.Listener on(String message, WebSocketCallback callback, final Boolean isOneTime)
    {
        final String _msg = message;
        final WebSocketCallback _callback = callback;

        Emitter.Listener emitterCallback = new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                currentRequest.remove(_msg);
                final Emitter.Listener self = this;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        _callback.onMessage(_msg, args);
                        if (isOneTime == true)
                            socket.off(_msg, self);
                    }
                });
            }
        };

        socket.on(message, emitterCallback);
        return emitterCallback;
    }

    public Emitter.Listener emit(String message, Object data, WebSocketCallback callback)
    {
        final String msg = message;
        Emitter.Listener cb = null;

        if (callback != null)
            cb = on(message, callback, true);
        if (!currentRequest.contains(msg))
        {
            if (callback != null)
                currentRequest.add(msg);
            socket.emit(message, data);
        }
        else
            System.out.println("NOT EMITTING");

        return cb;
    }
}
