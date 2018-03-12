package com.matemeup.matemeup;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.rendering.RemoteImageLoader;

public class UserToolbarActivity extends BackToolbarActivity {
    public void setUser(UserChat user) {
        findViewById(R.id.toolbar_avatar_container).setClipToOutline(true);
        RemoteImageLoader.load((ImageView)findViewById(R.id.toolbar_avatar_container), user.avatar);
        ((TextView)findViewById(R.id.toolbar_username_container)).setText(user.name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, int activity) {
        super.onCreate(savedInstanceState, activity);
    }
}
