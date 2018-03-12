package com.matemeup.matemeup.entities.model;

import org.json.JSONException;
import org.json.JSONObject;

public class HistoryChat implements Serializable<HistoryChat> {
    public int senderUserId;
    public String senderUserName;
    public String senderUserAvatar;
    public int receiverUserId;
    public String receiverUserName;
    public int type;
    public String message;

    public HistoryChat(JSONObject obj) {
        fromJSON(obj);
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("senderUserId", senderUserId);
            obj.put("senderUserAvatar", senderUserAvatar);
            obj.put("receiverUserId", receiverUserId);
            obj.put("senderUserName", senderUserName);
            obj.put("receiverUserName", receiverUserName);
            obj.put("message", message);
            obj.put("type", type);
        } catch (JSONException e) {return null;}

        return obj;
    }

    @Override
    public HistoryChat fromJSON(JSONObject obj) {
        try {
            senderUserId = obj.getInt("senderUserId");
            receiverUserId = obj.getInt("receiverUserId");
            senderUserName = obj.getString("senderUserName");
            receiverUserName = obj.getString("receiverUserName");
            senderUserAvatar = obj.getString("senderUserAvatar");
            type = obj.getInt("type");
            message = obj.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return this;
    }
}
