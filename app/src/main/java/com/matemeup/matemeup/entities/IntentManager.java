package com.matemeup.matemeup.entities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class IntentManager {

    public static void setExtrasToIntent(Intent activity, JSONObject extras)
    {
        System.out.println("data " + Serializer.serialize(extras));
        activity.putExtra("params", Serializer.serialize(extras));
    }

    public static void goTo(Context packageContext, Class<?> cls, JSONObject obj)
    {
        Intent activity = new Intent(packageContext, cls);

        if (obj != null)
            IntentManager.setExtrasToIntent(activity, obj);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        packageContext.startActivity(activity);
    }

    public static void goTo(Context packageContext, Class<?> cls)
    {
        goTo(packageContext, cls, null);
    }

    public static void replace(Context packageContext, Class<?> cls)
    {
        Intent intent = new Intent(packageContext, cls);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        packageContext.startActivity(intent);
        ((Activity)packageContext).finish();
    }
}
