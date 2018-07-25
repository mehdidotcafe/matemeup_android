package com.matemeup.matemeup.entities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;

import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.security.AccessController.getContext;

public class IntentManager {

    private static java.lang.Class currentActivity;

    public static java.lang.Class getCurrentActivity() {
        return currentActivity;
    }

    public static void setCurrentActivity(java.lang.Class activity) {
        currentActivity = activity;
    }

    public static void setExtrasToIntent(Intent activity, JSONObject extras)
    {
        activity.putExtra("params", Serializer.serialize(extras));
    }

    public static void goTo(Context packageContext, Class<?> cls, JSONObject obj)
    {
        Intent activity = new Intent(packageContext, cls);

        if (obj != null)
            IntentManager.setExtrasToIntent(activity, obj);
        activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(packageContext,
            android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        packageContext.startActivity(activity, bundle);
    }

    public static void goTo(Context packageContext, Class<?> cls)
    {
        goTo(packageContext, cls, null);
    }

    public static void replace(Context packageContext, Class<?> cls)
    {
        Intent intent = new Intent(packageContext, cls);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(packageContext,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        packageContext.startActivity(intent, bundle);
        ((Activity)packageContext).finish();

    }

    public static void goToRet(Activity activity, Context packageContext, Class<?> cls) {
        goToRet(activity, packageContext, cls, null);
    }

    public static void goToRet(Activity activity, Context packageContext, Class<?> cls, JSONObject obj) {
        Intent intent = new Intent(packageContext, cls);

        if (obj != null)
            IntentManager.setExtrasToIntent(intent, obj);
        activity.startActivityForResult(intent, 42);
    }

    public static void end(Activity activity) {
        end(activity, null);
    }

    public static void end(Activity activity, JSONObject obj) {
        Intent output = new Intent();
        if (obj != null)
            IntentManager.setExtrasToIntent(output, obj);
        activity.setResult(RESULT_OK, output);
        activity.finish();
    }
}
