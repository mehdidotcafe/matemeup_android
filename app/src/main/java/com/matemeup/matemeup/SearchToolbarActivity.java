package com.matemeup.matemeup;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public abstract class SearchToolbarActivity extends Layout {

    abstract void onTextChange(String str);

    private void initSearchListener() {
        final EditText edittext = (EditText)findViewById(R.id.search_input_toolbar);

        edittext.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (s.length() > 0)
                {
                    findViewById(R.id.toolbar_cross_icon).setVisibility(View.VISIBLE);
                    findViewById(R.id.toolbar_search_icon).setVisibility(View.INVISIBLE);
                }
                else
                {
                    findViewById(R.id.toolbar_cross_icon).setVisibility(View.INVISIBLE);
                    findViewById(R.id.toolbar_search_icon).setVisibility(View.VISIBLE);
                }
                onTextChange(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void initCrossIconListener() {
        findViewById(R.id.toolbar_cross_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText)findViewById(R.id.search_input_toolbar)).setText("");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, int activity) {
        super.onCreate(savedInstanceState, activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        initSearchListener();
        initCrossIconListener();
    }
}
