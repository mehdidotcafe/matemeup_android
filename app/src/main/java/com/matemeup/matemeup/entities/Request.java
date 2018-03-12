package com.matemeup.matemeup.entities;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Request {
    protected String BASE_URL = "http://192.168.0.100:8000/api/";
    private static Map<String, String> urlParams = new HashMap<String, String>();

    public Request(String url) {
        BASE_URL = url;
    }

    public Request() {}

    public static void addQueryString(String key, String value)
    {
        urlParams.put(key, value);
    }


    public void success(JSONObject result)
    {
        System.out.println("successStr");
        System.out.println(result);
    }

    public void fail(String result)
    {
        System.out.println("errorStr");
        System.out.println(result);

    }

    private String setDefaultUrlParams(String route)
    {
        Boolean isFirst = true;

        for (Map.Entry<String, String> entry : urlParams.entrySet())
        {
            if (isFirst)
            {
                route += '?' ;
                isFirst = false;
            }
            else
                route += '&';
            route += entry.getKey() + '=' + entry.getValue();
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

    public void send(Context ctx, String route, String method, JSONObject header, JSONObject body)
    {
        com.koushikdutta.ion.builder.Builders.Any.B req = Ion.with(ctx)
                .load(method, setDefaultUrlParams(BASE_URL + route));

        if (body != null)
            req.setJsonObjectBody(format(body));
        //if (header != null)
        //    req.setHeader("foo", "bar");
        req.asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
        @Override
        public void onCompleted(Exception e, JsonObject result) {
            System.out.println("result " + result);
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

}
