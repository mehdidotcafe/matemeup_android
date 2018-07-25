package com.matemeup.matemeup.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.matemeup.matemeup.ChatActivity;
import com.matemeup.matemeup.HomeActivity;
import com.matemeup.matemeup.Layout;
import com.matemeup.matemeup.R;
import com.matemeup.matemeup.adapters.OnItemClickListener;
import com.matemeup.matemeup.adapters.UserChatNotifAdapter;
import com.matemeup.matemeup.entities.Callback;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.model.UserChatNotif;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserChatListFragment extends Fragment {
    protected String                GET_USER_MSG = "";
    protected Boolean               isInvitation = false;
    private WebSocket               ws;
    private List<UserChatNotif>     users;
    private UserChatNotifAdapter    adapter = null;
    private String                  currentNeedle = "";

    public void setNeedle(String needle) {
        currentNeedle = needle;
    }

    public void removeUserFromMessageReceiver(HistoryChat chat) {
        int userId = chat.receiverUserId;

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).id == userId) {
                users.remove(i);
                adapter.notifyItemRemoved(i);
                break ;
            }
        }
    }

    public void addUserFromMessageReceiver(HistoryChat chat) {
        if (chat.receiverUserName.startsWith(currentNeedle)) {
            users.add(new UserChatNotif(chat.receiverUserId, chat.receiverUserName, chat.receiverUserAvatar, 0));
            if (adapter != null) {
                adapter.notifyItemInserted(users.size() - 1);
            }
        }
    }

    public void addUserFromMessageSender(HistoryChat chat) {
        if (chat.senderUserAvatar.startsWith(currentNeedle)) {
            users.add(new UserChatNotif(chat.senderUserId, chat.senderUserName, chat.senderUserAvatar, 1));
            if (adapter != null) {
                adapter.notifyItemInserted(users.size() - 1);
            }
        }
    }

    public Boolean contains(int userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).id == userId)
                return true;
        }
        return false;
    }

    public int getUserIndex(int userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).id == userId)
                return i;
        }
        return -1;
    }

    public List<UserChatNotif> getUsers() {
        return users;
    }

    private void initAdapter() {
        RecyclerView rv = getView().findViewById(R.id.recycler_view);
        adapter = new UserChatNotifAdapter(getActivity().getApplicationContext(), R.layout.item_chat_user, users, new OnItemClickListener() {
            @Override
            public void onItemClick(Object item) {
                goToChat((UserChatNotif)item);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(adapter);
    }

    private void setUsersFromUserChat(List<UserChatNotif> newUsers) {
        users.clear();
        users.addAll(newUsers);
    }

    private Map<Integer, Integer> notifMapFromUserArray(List<UserChatNotif> users) {
        Map<Integer, Integer> ret = new HashMap<Integer, Integer>();

        for (int i = 0; i < users.size(); i++)
            if (users.get(i).notif > 0)
                ret.put(users.get(i).id, users.get(i).notif);
        return ret;
    }

    public void setUsers(List<UserChatNotif> newUsers) {

        setUsersFromUserChat(newUsers);

        if (adapter == null)
            initAdapter();
        else
            adapter.setList(users);
    }

    private void getUsers(List<UserChatNotif> _users)
    {
        setUsersFromUserChat(_users);
        ((Layout)getActivity()).addChatNotif(notifMapFromUserArray(_users));
        if (adapter == null)
            initAdapter();
    }

    public void goToChat(UserChatNotif user)
    {
        JSONObject obj = new JSONObject();
        int userIdx = getUserIndex(user.id);

        if (user.notif > 0)
            ((HomeActivity)getActivity()).decrChatNotifCount(user.id);
        user.notif = 0;
        if (userIdx >= 0) {
            adapter.notifyItemChanged(userIdx);
        }

        try {
            obj.put("user", user.toJSONObject());
            obj.put("isInvitation", isInvitation);
            IntentManager.goTo(getActivity().getApplicationContext(), ChatActivity.class, obj);
        } catch (JSONException e) {}
    }

    public int getPositionFromId(int id) {
        for (int i = 0; i < users.size(); i++)
            if (users.get(i).id == id)
                return i;
        return -1;
    }

    public void initSocket()
    {
        ws = MMUWebSocket.getInstance(getActivity());
    }

    public void onCreate(Bundle savedInstanceState, Boolean isInvitation) {
        HomeActivity activity = (HomeActivity)getActivity();
        users = new ArrayList();

        super.onCreate(savedInstanceState);
        initSocket();
        activity.subscribeNewChatMessage(new Callback() {
            @Override
            public void success(Object obj) {
                HistoryChat msg = (HistoryChat)obj;
                UserChatNotif user;
                RecyclerView rv = getView().findViewById(R.id.recycler_view);
                int position;
                int userId = msg.senderUserId;

                position = getPositionFromId(userId);
                if (position != -1) {
                    user = users.get(position);
                    ++user.notif;
                    TextView tv = rv.getChildAt(position).findViewById(R.id.user_notif_count);
                    tv.setText(String.valueOf(user.notif));
                    tv.setVisibility(View.VISIBLE);
                }

            }
        }, isInvitation);
                ws.emit(GET_USER_MSG, null, new WebSocketCallback() {
                    @Override
                    public void onMessage(final String message, final Object... args)
                    {
                JSONArray users = (JSONArray)args[0];
                if (getActivity() != null){
                    getUsers((List<UserChatNotif>)(List<?>)UserChatNotif.fromJson(users));
                }
            }
        });
    }

}
