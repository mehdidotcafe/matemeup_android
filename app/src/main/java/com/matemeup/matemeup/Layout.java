package com.matemeup.matemeup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Layout extends AppCompatActivity {

    private WebSocket                       ws;
    private static Map<Integer, Integer>    chatNotifCount = new HashMap<>();
    private static int                      friendNotifCount = 0;
    private WebSocketCallback               friendCallback;
    private Emitter.Listener                friendAddEmitter;
    private WebSocketCallback               chatCallback;
    private Emitter.Listener                chatEmitter;
    protected Boolean                       initSocket = true;

    @Override
    public void onBackPressed() {
        finish();
    }

    private void renderNotifCount(TextView view, int count) {
        if (count == 0 && view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);
        }
        else if (count > 0) {
            view.setText(String.valueOf(count));
            if (view.getVisibility() != View.VISIBLE)
                view.setVisibility(View.VISIBLE);
        }
    }

    public void addChatNotif(Map<Integer, Integer> newNotifs) {
        for (Map.Entry<Integer, Integer> entry : newNotifs.entrySet()) {
            if (chatNotifCount.get(entry.getKey()) != null)
                chatNotifCount.put(entry.getKey(), chatNotifCount.get(entry.getKey()) + entry.getValue());
            else
                chatNotifCount.put(entry.getKey(), entry.getValue());
        }
        renderChatNotifCount();
    }

    public void decrFriendNotifCount() {
        --friendNotifCount;
        renderFriendNotifCount();
    }

    public void decrChatNotifCount(int userId) {
        chatNotifCount.remove(userId);
        System.out.println(chatNotifCount);
        renderChatNotifCount();
    }

    protected void renderChatNotifCount() {
        View view = findViewById(R.id.chat_notif_count);

        if (view != null)
            renderNotifCount((TextView)view, chatNotifCount.size());
    }

    protected void renderFriendNotifCount() {
        View view = findViewById(R.id.friend_notif_count);

        if (view != null)
            renderNotifCount((TextView)view, friendNotifCount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (initSocket) {
            ws.off("friend.add.new", friendAddEmitter);
            ws.off("global.chat.new", chatEmitter);
        }
    }

    public void onNewSelfChatMessage(HistoryChat historyMessage) {

    }

    public void onNewChatMessage(HistoryChat historyMessage) {

        if (chatNotifCount.get(historyMessage.senderUserId) != null)
            chatNotifCount.put(historyMessage.senderUserId, chatNotifCount.get(historyMessage.senderUserId) + 1);
        else
            chatNotifCount.put(historyMessage.senderUserId, 1);
        renderChatNotifCount();
    }


    private boolean canShowChatNotif(int userId, Boolean isUser) {
        return (IntentManager.getCurrentActivity() != ChatActivity.class ||
                ChatActivity.getUser().id != userId) && !isUser;
    }

    protected void onCreate(Bundle savedInstanceState, int activity)
    {
        super.onCreate(savedInstanceState);
        setContentView(activity);

        final Layout self = this;
        if (initSocket) {
            ws = new MMUWebSocket(this);

            friendCallback = new WebSocketCallback() {
                @Override
                public void onMessage(String message, Object... args) {
                    ++friendNotifCount;
                    renderFriendNotifCount();
                }
            };

            chatCallback = new WebSocketCallback() {
                @Override
                public void onMessage(String message, Object... args) {
                    HistoryChat msg = new HistoryChat((JSONObject)args[0]);

                    if (canShowChatNotif(msg.senderUserId, msg.isUser)) {
                        self.onNewChatMessage(msg);
                    }
                    else
                        self.onNewSelfChatMessage(msg);

                }
            };

            friendAddEmitter = ws.on("friend.add.new", friendCallback);

            chatEmitter = ws.on("global.chat.new", chatCallback);

            ws.emit("friend.pending.get", null, new WebSocketCallback() {
                @Override
                public void onMessage(String message, Object... args) {
                    JSONArray pendings = (JSONArray)args[0];

                    friendNotifCount = pendings.length();
                    renderFriendNotifCount();
                }
            });
        }

        renderChatNotifCount();
        renderFriendNotifCount();
    }

    private void replace(java.lang.Class cls)
    {
        if (IntentManager.getCurrentActivity() != cls)
        {
            IntentManager.replace(this, cls);
        }
    }

    public void replaceChat(View view)
    {
        replace(HomeActivity.class);
    }

    public void replaceProfile(View view)
    {
        replace(ProfileActivity.class);
    }

    public void replaceFriends(View view)
    {
        replace(FriendsActivity.class);
    }
}
