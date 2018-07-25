package com.matemeup.matemeup.entities;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {
    protected String BASE_URL = Constants.apiUrl;
    private  Map<String, String> urlParams = new HashMap();
    private static Request instance = null;

    public static Request getInstance() {
        if (instance == null) {
            instance = new Request();
        }
        return instance;
    }

    public void unsetQueryString() {
        urlParams = new HashMap();
    }

    public void addQueryString(String key, String value)
    {
        urlParams.put(key, value);
    }


    public void success(JSONObject result)
    {
    }

    public void fail(String result)
    {
    }

    private String setDefaultUrlParams(String route, JSONObject obj)
    {
        Boolean isFirst = true;

        for (Map.Entry<String, String> entry : urlParams.entrySet()) {
            if (isFirst) {
                route += '?' ;
                isFirst = false;
            }
            else
                route += '&';
            route += entry.getKey() + '=' + entry.getValue();
        }

        if (obj != null) {
            try {
                for (int i = 0; i < obj.names().length(); i++) {
                    if (isFirst) {
                        route += '?' ;
                        isFirst = false;
                    }
                    else
                        route += '&';
                    route += obj.names().getString(i) + '=' + obj.get(obj.names().getString(i));
                }
            } catch (JSONException e) {}
        }
        return route;
    }

    private void setHeaders(JSONObject header)
    {

    }

    private JsonObject format(JSONObject json) {
        JsonObject ret = new JsonObject();

        Iterator<String> iter = json.keys();
        try {
            while (iter.hasNext()) {
                String key = iter.next();
                    ret.addProperty(key, (String)json.get(key));

            }
        } catch (JSONException e) {
            // Something went wrong!
        }

        return ret;
    }

    public void sendFile(Context ctx, String route, String pathname, JSONObject queryString) {
        Ion.with(ctx)
                .load(setDefaultUrlParams(BASE_URL + route, queryString))
                .setMultipartFile("file", "application/octet", new File(pathname))
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Boolean isSucceed = result != null && result.get("success") != null && result.get("success").getAsBoolean();

                        try {
                            if (isSucceed) {
                                success(result.get("data") != null ? new JSONObject(result.get("data").toString()) : null);
                            }
                            else
                            {
                                JsonElement ret = result != null ? result.get("message") : null;

                                if (result != null && ret == null)
                                    ret = result.get("error");
                                fail(ret != null ? ret.getAsString() : "unknowError");
                            }
                        } catch (JSONException error) {}
                    }
                });
    }

    public void send(Context ctx, String route, String method, JSONObject queryString, JSONObject body, final Callback cb)
    {
        com.koushikdutta.ion.builder.Builders.Any.B req = Ion.with(ctx)
                .load(method, setDefaultUrlParams(BASE_URL + route, queryString));

        if (body != null)
            req.setJsonObjectBody(format(body));
        req.asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
        @Override
        public void onCompleted(Exception e, JsonObject result) {
            Boolean isSucceed = result != null && result.get("success") != null && result.get("success").getAsBoolean();

            try {
                if (isSucceed) {
                    cb.success(result.get("data") != null ? new JSONObject(result.get("data").toString()) : null);
                }
                else
                {
                    JsonElement ret = result != null ? result.get("message") : null;

                    if (result != null && ret == null)
                        ret = result.get("error");
                    cb.fail(ret != null ? ret.getAsString() : "unknowError");
                }
            } catch (JSONException error) {}
        }
     });
    }

}
