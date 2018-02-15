package com.matemeup.matemeup.entities;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class JWT {

    public static final String PREFS_NAME = "com.matemeup.matemeup.PREFS";

    public static String get(Context ctx)
    {
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("JWT", null);

        return token;
    }

    public static String put(Context ctx, String token)
    {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("JWT", token);
        editor.apply();

        return token;
    }
}
