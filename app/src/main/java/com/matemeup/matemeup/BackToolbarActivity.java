package com.matemeup.matemeup;


import android.os.Bundle;
import android.view.View;

public class BackToolbarActivity extends Layout {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setBackButton() {
        findViewById(R.id.toolbar_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState, int activity) {
        super.onCreate(savedInstanceState, activity);
        setBackButton();
    }
}
