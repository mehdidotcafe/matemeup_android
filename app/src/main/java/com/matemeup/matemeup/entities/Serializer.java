package com.matemeup.matemeup.entities;
import com.matemeup.matemeup.entities.model.Serializable;
import com.matemeup.matemeup.entities.model.UserChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Serializer
{

    public static String serialize(JSONObject obj)
    {
        return obj.toString();
    }

    public static JSONObject unserialize(String obj)
    {
        try {
            return new JSONObject(obj);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray unserializeArray(String obj)
    {
        try {
            return new JSONArray(obj);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject fromMap(Map<String, Object> map)
    {
        JSONObject obj = new JSONObject();

        try {
            for (Map.Entry<String, Object> entry : map.entrySet())
            {
                obj.put(entry.getKey(), entry.getValue().toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }

        return obj;
    }

    public static Map<String, Object> toMap(String json)
    {

        Map<String, Object> map = new HashMap<>();


        return map;
    }

    public static List<Serializable> JSONArrayToList(JSONArray array, Factory factory) {
        List<Serializable> res = new ArrayList<Serializable>();

//        for (int i = 0; i < array.length(); i++) {
//            res.add(factory.newInstanceFromJSONObject(array.getJSONObject(i)));
//        }
        return res;
    }
}
