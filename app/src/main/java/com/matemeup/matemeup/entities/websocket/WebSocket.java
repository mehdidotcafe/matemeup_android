package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class WebSocket {
    protected Socket socket;
    protected Activity activity;

    public WebSocket(Activity _activity, String url)
    {
        try {
            socket = IO.socket(url);
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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "Unable to connect to NodeJS server", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    public void on(String message, WebSocketCallback callback) {
        on(message, callback, false);
    }

    public void on(String message, WebSocketCallback callback, final Boolean isOneTime)
    {
        final String _msg = message;
        final WebSocketCallback _callback = callback;
        socket.on(message, new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
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
        });
    }

    public void emit(String message, Object data, WebSocketCallback callback)
    {
        final String msg = message;

        if (callback != null)
            on(message, callback, true);
        socket.emit(message, data);
    }


}
