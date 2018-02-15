package com.matemeup.matemeup.entities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentManager {
    public static void goTo(Context packageContext, Class<?> cls)
    {
        Intent activity = new Intent(packageContext, cls);

        packageContext.startActivity(activity);
    }

    public static void replace(Context packageContext, Class<?> cls)
    {
        Intent intent = new Intent(packageContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        packageContext.startActivity(intent);
        ((Activity)packageContext).finish();
    }
}
