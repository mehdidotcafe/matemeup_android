package com.matemeup.matemeup.entities.rendering;

import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

public class RemoteImageLoader {

    public static void load(ImageView view, String url, String name)
    {
        String loadUrl = url;

        if (name.startsWith("/")) {
            loadUrl += name;
        }
        else
            loadUrl += '/' + name;
        Ion.with(view)
                .load(loadUrl );
    }
}
