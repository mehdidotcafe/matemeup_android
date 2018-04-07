package com.matemeup.matemeup.entities.navigation;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;

public class UserToolbar {

    public static void handle(Activity activity, UserChat user) {
        activity.findViewById(R.id.toolbar_avatar_container).setClipToOutline(true);
        AvatarRemoteImageLoader.load((ImageView)activity.findViewById(R.id.toolbar_avatar_container), user.avatar);
        ((TextView)activity.findViewById(R.id.toolbar_username_container)).setText(user.name);
    }
}
