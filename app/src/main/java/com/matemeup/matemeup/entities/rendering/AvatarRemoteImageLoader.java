package com.matemeup.matemeup.entities.rendering;

import android.widget.ImageView;

public class AvatarRemoteImageLoader extends RemoteImageLoader {

    public static final String URL = "https://beta.matemeup.com/img/avatars";
    //public static final String URL = "http://192.168.0.103:8000/img/avatars";

    public static void load(ImageView view, String name) {
        load(view, URL, name);
    }
}
