package com.matemeup.matemeup.entities.rendering;

import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

public class RemoteImageLoader {
    private static final String URL = "https://matemeup.com/img/avatars/";

    public static void load(ImageView view, String name)
    {
        Ion.with(view)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                .load(URL + name);
    }
}
