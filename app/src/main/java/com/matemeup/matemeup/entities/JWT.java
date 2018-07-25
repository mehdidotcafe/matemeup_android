package com.matemeup.matemeup.entities;

import android.content.Context;

public class JWT extends Storage
{
    private static final String API_KEY = "JWT_API";
    private static final String MM_KEY = "JWT_MM";

    public static String getAPI(Context ctx)
    {
        return JWT.get(ctx, API_KEY);
    }

    public static String putAPI(Context ctx, String token)
    {
        JWT.put(ctx, API_KEY, token);

        return token;
    }

    public static String unsetAPI(Context ctx) {
        JWT.put(ctx, API_KEY, null);

        return "";
    }

    public static String getMM(Context ctx)
    {
        return JWT.get(ctx, MM_KEY);
    }

    public static String putMM(Context ctx, String token)
    {
        JWT.put(ctx, MM_KEY, token);

        return token;
    }

    public static String unsetMM(Context ctx) {
        JWT.put(ctx, MM_KEY, null);

        return "";
    }

}
