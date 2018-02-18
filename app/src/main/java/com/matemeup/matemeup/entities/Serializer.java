package com.matemeup.matemeup.entities;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Serializer
{
    public static JsonObject fromMap(Map<String, Object> map)
    {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
           obj.addProperty(entry.getKey(), entry.getValue().toString());
        }

        return obj;
    }

    public static Map<String, Object> toMap(String json)
    {

        Map<String, Object> map = new HashMap<>();


        return map;
    }
}
