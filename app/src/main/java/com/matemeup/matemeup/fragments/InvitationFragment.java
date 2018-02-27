package com.matemeup.matemeup.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matemeup.matemeup.R;

public class InvitationFragment extends UserChatListFragment {
    public InvitationFragment() {
        super();
        GET_USER_MSG = "global.chat.user.invitation.get";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invitation, container, false);
    }
}

