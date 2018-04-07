package com.matemeup.matemeup.entities.navigation;

import android.app.Activity;
import android.view.View;

import com.matemeup.matemeup.R;

public class BackButton {
    private static boolean onSupportNavigateUp(Activity activity) {
        activity.onBackPressed();
        return true;
    }

    public static void handle(final Activity activity) {
        activity.findViewById(R.id.toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp(activity);
            }
        });
    }
}
