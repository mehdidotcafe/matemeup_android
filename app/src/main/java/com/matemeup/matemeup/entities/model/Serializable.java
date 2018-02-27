package com.matemeup.matemeup.entities.model;

import org.json.JSONObject;

public interface Serializable<T> {
    JSONObject toJSONObject();
    T fromJSON(JSONObject obj);
}
