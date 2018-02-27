package com.matemeup.matemeup;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.matemeup.matemeup.adapters.UserAutocompleteAdapter;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.fragments.ChatFragment;
import com.matemeup.matemeup.fragments.InvitationFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Layout {

    public void setNewChatListener(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Ca clique");
            }
        });
    }

    private void setUserAutoCompletion() {
        List<UserChat> USERS = new ArrayList<UserChat>();

        USERS.add(new UserChat(42, "toto", "noavatar.png"));
        USERS.add(new UserChat(43, "totototo", "noavatar.png"));
        USERS.add(new UserChat(44, "meddou", "noavatar.png"));
        USERS.add(new UserChat(45, "Naru", "noavatar.png"));

        UserAutocompleteAdapter adapter = new UserAutocompleteAdapter(this,
                R.layout.username_autocomplete_dropdown_item, USERS);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.username_autocomplete);
        textView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_home);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new ChatFragment(), "Chat");
        adapter.addFragment(new InvitationFragment(), "Invitation");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setUserAutoCompletion();
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
