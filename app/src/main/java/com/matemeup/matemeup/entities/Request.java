package com.matemeup.matemeup.entities;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private static final String BASE_URL = "http://192.168.0.14:8000/api/";
    private static Map<String, String> urlParams = new HashMap<String, String>();

    public static void addQueryString(String key, String value)
    {
        urlParams.put(key, value);
    }


    public void success(JsonObject result)
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

    private void setHeaders(JsonObject header)
    {

    }

    public void send(Context ctx, String route, String method, JsonObject header, JsonObject body)
    {
        com.koushikdutta.ion.builder.Builders.Any.B req = Ion.with(ctx)
                .load(setDefaultUrlParams(BASE_URL + route));

        if (body != null)
            req.setJsonObjectBody(body);
        //if (header != null)
        //    req.setHeader("foo", "bar");
        req.asJsonObject()
            .setCallback(new FutureCallback<JsonObject>() {
        @Override
        public void onCompleted(Exception e, JsonObject result) {
            System.out.println(result);
            Boolean isSucceed = result != null && result.get("success") != null && result.get("success").getAsBoolean();

            if (isSucceed) {
                success(result.get("data").getAsJsonObject());
            }
            else
            {
                JsonElement ret = result != null ? result.get("message") : null;

                if (result != null && ret == null)
                    ret = result.get("error");
                fail(ret != null ? ret.getAsString() : "unknowError");
            }
        }
     });
    }

}
