package com.matemeup.matemeup.entities.rendering;

import android.widget.ImageView;

import com.koushikdutta.ion.Ion;
import com.matemeup.matemeup.entities.Constants;

public class ChatRemoteImageLoader extends RemoteImageLoader {
    public static final String URL = Constants.chatImageUrl;

    public static void load(ImageView view, String name) {
        load(view, URL, name);
    }
}
