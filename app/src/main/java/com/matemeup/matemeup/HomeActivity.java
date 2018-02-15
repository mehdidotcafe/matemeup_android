package com.matemeup.matemeup;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Layout {

    private void getUsers()
    {
        List<String> users = new ArrayList();

        users.add("aaaaaa");
        users.add("bbbbbb");
        users.add("cccccc");
        users.add("dddddd");
        users.add("eeeeee");
        users.add("ffffff");
        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.chat_user_list, users);
        ListView list = findViewById(R.id.chat_user_listview);

        list.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home);
        getUsers();
    }
}
