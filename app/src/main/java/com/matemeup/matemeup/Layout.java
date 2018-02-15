package com.matemeup.matemeup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.matemeup.matemeup.HomeActivity;
import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.IntentManager;

public class Layout extends AppCompatActivity {

    private static java.lang.Class currentActivity = HomeActivity.class;

    @Override
    public void onBackPressed() {
        finish();
    }

    protected void onCreate(Bundle savedInstanceState, int activity)
    {
        super.onCreate(savedInstanceState);
        setContentView(activity);
    }

    private void replace(View view, java.lang.Class cls)
    {
        if (currentActivity != cls)
        {
            IntentManager.replace(this, cls);
            currentActivity = cls;
        }
    }

    public void replaceChat(View view)
    {
        replace(view, HomeActivity.class);
    }

    public void replaceProfile(View view)
    {
        replace(view, ProfileActivity.class);
    }

    public void replaceFriends(View view)
    {
        replace(view, FriendsActivity.class);
    }
}
