package com.matemeup.matemeup;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.matemeup.matemeup.adapters.UserAutocompleteAdapter;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;
import com.matemeup.matemeup.fragments.ChatFragment;
import com.matemeup.matemeup.fragments.InvitationFragment;
import com.matemeup.matemeup.fragments.UserChatListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends SearchToolbarActivity {
    private HomeActivity        self;
    private WebSocket           ws;
    private int                 currentTabIndex;
    private ViewPagerAdapter    tabsAdapter;


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//        return true;
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            UserChat user = new UserChat(Serializer.unserialize(data.getStringExtra("params")));

            System.out.println("user " + user);
            IntentManager.goTo(this, ChatActivity.class, Serializer.unserialize(data.getStringExtra("params")));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_button:
                return true;

              default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onTextChange(String needle) {
        String[] msgs = {"global.chat.user.normal.get", "global.invitation.user.normal.get"};

            JSONObject obj = new JSONObject();

            try {
                obj.put("needle", needle);
                ws.emit(msgs[currentTabIndex], obj, new WebSocketCallback() {
                    @Override
                    public void onMessage(String message, Object... args) {
                        ((UserChatListFragment)tabsAdapter.getItem(currentTabIndex)).setUsers(UserChat.fromJson((JSONArray)args[0]));
                    }
                });
            } catch (JSONException e) {}
    }


    private void initSocket() {
        ws = new MMUWebSocket(this);
    }

    private void initTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        tabsAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        tabsAdapter.addFragment(new ChatFragment(), "Chat");
        tabsAdapter.addFragment(new InvitationFragment(), "Invitation");
        viewPager.setAdapter(tabsAdapter);

        currentTabIndex = 0;
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabIndex = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void goToUserSelectView() {
        IntentManager.goToRet(this, this, UserSelectActivity.class);
    }

    private void setNewChatListener() {
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.new_chat_button);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUserSelectView();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home);

        self = this;

        initTabs();
        initSocket();

        setNewChatListener();
    }

    // Adapter for the viewpager using FragmentPagerAdapter
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
