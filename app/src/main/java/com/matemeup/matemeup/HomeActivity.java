package com.matemeup.matemeup;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.view.MenuItem;
import android.view.View;

import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.model.UserChatNotif;
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
    private WebSocket               ws;
    private int                     currentTabIndex;
    private ViewPagerAdapter        tabsAdapter;
    private List<Callback>          onNewChatMessageCallbacks = new ArrayList<>();
    private List<Callback>          onNewChatInvitationCallbacks = new ArrayList<>();
    private UserChatListFragment    chatFragment;
    private UserChatListFragment    invitationFragment;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 42 && resultCode == RESULT_OK && data != null) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("user", Serializer.unserialize(data.getStringExtra("params")));
                obj.put("isInvitation", currentTabIndex == 1);
                IntentManager.goTo(this, ChatActivity.class, obj);
            } catch (JSONException e) {}
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
                ((UserChatListFragment)tabsAdapter.getItem(currentTabIndex)).setUsers((List<UserChatNotif>)(List<?>)UserChatNotif.fromJson((JSONArray)args[0]));
                }
            });
        } catch (JSONException e) {}
    }


    private void initSocket() {
        ws = MMUWebSocket.getInstance(this);
    }

    private void initTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        tabsAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        chatFragment = new ChatFragment();
        invitationFragment = new InvitationFragment();

        tabsAdapter.addFragment(chatFragment, getResources().getString(R.string.chat));
        tabsAdapter.addFragment(invitationFragment , getResources().getString(R.string.invitation));
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

    public void notifyMessageListener(HistoryChat msg) {
        for (int i = 0; i < onNewChatMessageCallbacks.size(); i++)
            onNewChatMessageCallbacks.get(i).success(msg);
    }

    public void notifyInvitationListener(HistoryChat msg) {
        for (int i = 0; i < onNewChatInvitationCallbacks.size(); i++)
            onNewChatInvitationCallbacks.get(i).success(msg);
    }

    public void subscribeNewChatMessage(Callback cb, Boolean isInvitation) {
        if (isInvitation)
            onNewChatInvitationCallbacks.add(cb);
        else
            onNewChatMessageCallbacks.add(cb);
    }

    @Override
    public void onNewSelfChatMessage(HistoryChat msg) {
        if (msg.isInvitation)
        {
            if (!invitationFragment.contains(msg.receiverUserId))
                invitationFragment.addUserFromMessageReceiver(msg);
        }
        else {
            if (!chatFragment.contains(msg.receiverUserId))
            {
                chatFragment.addUserFromMessageReceiver(msg);
                if (invitationFragment.contains(msg.receiverUserId)) {
                    invitationFragment.removeUserFromMessageReceiver(msg);
                }
            }
        }
    }

    @Override
    public void onNewChatMessage(HistoryChat msg) {
        super.onNewChatMessage(msg);
        if (msg.isInvitation)
        {
            notifyInvitationListener(msg);
            if (!msg.isUser && !invitationFragment.contains(msg.senderUserId))
                invitationFragment.addUserFromMessageSender(msg);
        }
        else {
            notifyMessageListener(msg);
            if (!msg.isUser && !chatFragment.contains(msg.senderUserId))
                chatFragment.addUserFromMessageSender(msg);
        }
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
    protected void onResume() {
        super.onResume();
        IntentManager.setCurrentActivity(HomeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home);

        IntentManager.setCurrentActivity(HomeActivity.class);
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
