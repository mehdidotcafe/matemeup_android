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
        System.out.println("url " + loadUrl);
        Ion.with(view)
                //.placeholder(R.drawable.placeholder_image)
                //.error(R.drawable.error_image)
                //.animateLoad(spinAnimation)
                //.animateIn(fadeInAnimation)
                .load(loadUrl );
    }
}
