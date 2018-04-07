package com.matemeup.matemeup.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectedUser {
    public String      name = "";
    public String      avatar = "";
    public String      birthdate = "";
    public String      firstname =  "";
    public String      lastname = "";
    public int         gender = 0;
    public String      location = "";
    public Boolean     isOpenChatDisabled = false;
    private static ConnectedUser instance = null;

    public static ConnectedUser getInstance() {
        if (instance == null)
            instance = new ConnectedUser();
        return instance;
    }

    public static ConnectedUser getInstance(JSONObject user) {
        ConnectedUser newInstance = getInstance();

        try {
            newInstance.name = user.getString("name");
            newInstance.avatar = user.getString("avatar");
            newInstance.birthdate = user.getString("birthdate");
            newInstance.firstname = user.getString("firstname");
            newInstance.lastname = user.getString("lastname");
            newInstance.gender = user.getInt("gender");
            newInstance.location = user.getString("location");
            newInstance.isOpenChatDisabled = user.getInt("open_chat_disabled") == 1;
        } catch (JSONException e) {
            System.out.println("ca throw ici");
            System.out.println(e);
        }
        return newInstance;
    }

    public static ConnectedUser set(JSONObject user) {
        return getInstance(user);
    }
}
