package com.matemeup.matemeup.entities.rendering;

import android.widget.ImageView;

import com.matemeup.matemeup.entities.Constants;

public class AvatarRemoteImageLoader extends RemoteImageLoader {

    public static final String URL = Constants.avatarUrl;

    public static void load(ImageView view, String name) {
        load(view, URL, name);
    }
}
