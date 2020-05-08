package com.matemeup.matemeup.entities.websocket;

import android.app.Activity;
import android.widget.Toast;


import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

public class WebSocket {
    protected Socket socket;
    protected Activity activity;
    protected List<String> currentRequest;

    public IO.Options getOptions() {
        IO.Options opts;
        SSLContext mySSLContext = null;

        TrustManager[] trustAllCerts= new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                System.out.println("ERROR");
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
                System.out.println("ERROR");
            }
        } };

        try {
            mySSLContext = SSLContext.getInstance("TLS");
            mySSLContext.init(null, trustAllCerts, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            System.out.println("NO SUCH ALGORITHM");
        }

        HostnameVerifier myHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .hostnameVerifier(myHostnameVerifier)
                .sslSocketFactory(mySSLContext.getSocketFactory(), new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[] {};
                    }

                    public void checkClientTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                        System.out.println("ERROR");
                    }

                    public void checkServerTrusted(X509Certificate[] chain,
                                                   String authType) throws CertificateException {
                        System.out.println("ERROR");
                    }
                })
                .build();

// default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
        IO.setDefaultOkHttpCallFactory(okHttpClient);

// set as an option
        opts = new IO.Options();
        opts.reconnection = true;
        opts.callFactory = okHttpClient;
        opts.webSocketFactory = okHttpClient;

        return opts;
    }

    public WebSocket(Activity _activity, String url)
    {
        try {
            socket = IO.socket(url, this.getOptions());
            currentRequest = new ArrayList();
        } catch (URISyntaxException e) {
            System.out.println("Connection fail");
            return ;
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectErrorFx);
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectErrorFx);
        socket.on(Socket.EVENT_RECONNECT_ERROR, onConnectErrorFx);
        socket.on(Socket.EVENT_DISCONNECT, onConnectErrorFx);
        socket.connect();
        activity = _activity;
    }

    private Emitter.Listener onConnectErrorFx = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            currentRequest.clear();
            onConnectError();
        }
    };

    protected void onConnectError() {

    }

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
        else {
        }

        return cb;
    }
}
