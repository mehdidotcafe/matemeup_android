package com.matemeup.matemeup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.matemeup.matemeup.adapters.HistoryChatAdapter;
import com.matemeup.matemeup.entities.BitmapGetter;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.navigation.BackButton;
import com.matemeup.matemeup.entities.navigation.UserToolbar;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

public class ChatActivity extends Layout {
    private static UserChat user;
    private WebSocket ws;
    private String getNormalURL = "global.chat.user.normal.history";
    private String getInvitationURL = "global.chat.user.invitation.history";
    private String sendURL = "global.chat";
    private String seenURL = "global.chat.message.seen";
    private static final int CHUNK_SIZE = 30;
    private static int index = 0;
    private HistoryChatAdapter adapter = null;
    private ArrayList<HistoryChat> list = new ArrayList();
    private int SCROLL_CEIL = 30;
    private Boolean isRequesting = false;
    private Boolean isInvitation = false;

    private void sendText(String text) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("userId", user.id);
            obj.put("type", 1);
            obj.put("message", text);
        } catch (JSONException e) {return ;}

        ws.emit(sendURL, obj, null);
    }

    public static UserChat getUser() {
        return user;
    }

    public void sendImage(String image) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("userId", user.id);
            obj.put("type", 2);
            obj.put("message", image);
        } catch (JSONException e) {return ;}

        ws.emit(sendURL, obj, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String image = BitmapGetter.get(this, data, 1000);
            sendImage(image);

        }
    }


    public void sendImageFromButton(View view) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)), 1);
    }

    public void sendTextFromButton(View view) {
        EditText input = (EditText)findViewById(R.id.chat_input);

        sendText(input.getText().toString());
        input.setText("");
    }

    private void renderMessages(JSONObject data, Boolean goToBottom) throws JSONException {
        JSONArray messages;

        try {
            messages = new JSONArray(data.getString("history"));
        } catch (JSONException e) {
            messages = new JSONArray();
        }

        index += messages.length();
        for (int i = 0; i <= messages.length() - 1; i++)
        {
            list.add(i, new HistoryChat(messages.getJSONObject(i)));
        }
        adapter.notifyItemRangeInserted(0, messages.length());

        if (goToBottom)
        {
            if (list.size() > 0)
                ((RecyclerView)findViewById(R.id.message_list)).smoothScrollToPosition(list.size() - 1);
        }
        isRequesting = false;
    }

    private void sendSeenRequest() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("userId", user.id);
            ws.emit(seenURL, obj, null);
        } catch (JSONException e) {}
    }

    private void getChunk(final Boolean goToBottom) {
        getChunk(goToBottom, false);
    }

    private void getChunk(final Boolean goToBottom, final Boolean sendSeen) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("userId", user.id);
            obj.put("chunkSize", CHUNK_SIZE);
            obj.put("index", index);
        } catch (JSONException e) {}

        ws.emit(isInvitation ? getInvitationURL : getNormalURL, obj, new WebSocketCallback()
        {
            public void onMessage(final String message, final Object... args)
            {
                JSONObject messages = (JSONObject)args[0];
                try {
                    renderMessages(messages, goToBottom);
                    if (sendSeen) {
                        sendSeenRequest();
                    }
                } catch (JSONException e) {}
            }
        });
    }

    private void onNewMessage(HistoryChat message) {
        RecyclerView rv = (RecyclerView)findViewById(R.id.message_list);

        list.add(message);
        adapter.notifyItemInserted(list.size() - 1);
        rv.smoothScrollToPosition(list.size() - 1);
        if (IntentManager.getCurrentActivity() == ChatActivity.class)
            sendSeenRequest();
    }

    private void initSocket() {
        ws = MMUWebSocket.getInstance(this);
        ws.on("global.chat.new", new WebSocketCallback() {
            @Override
            public void onMessage(final String message, final Object... args) {
                HistoryChat msg = new HistoryChat((JSONObject)args[0]);
                if (msg.senderUserId == user.id || msg.receiverUserId == user.id)
                    onNewMessage(msg);
            }
        });
    }

    private void setSendTextVisibility(Boolean isVisible) {
        View imageButton = findViewById(R.id.send_image_button);
        View textButton = findViewById(R.id.send_text_button);

        if (isVisible) {
            textButton.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            textButton.setVisibility(View.INVISIBLE);
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    private void setDataFromIntent() {
        JSONObject obj = Serializer.unserialize(getIntent().getStringExtra("params"));

        try {
            user = new UserChat(obj.getJSONObject("user"));
            UserToolbar.handle(this, user);
            isInvitation = obj.getBoolean("isInvitation");
        } catch (JSONException e) {
            isInvitation = false;
        }
    }

    private void setMessageInputListener() {
        EditText input =  (EditText)findViewById(R.id.chat_input);
        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    setSendTextVisibility(true);
                else
                    setSendTextVisibility(false);
            }
        });
    }

    private void initList(){
        final RecyclerView fl = findViewById(R.id.message_list);

        adapter = new HistoryChatAdapter(this, R.layout.item_chat_text_history, R.layout.item_chat_image_history, list, user.id);
        fl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        fl.setAdapter(adapter);

        fl.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
                if (newState == SCROLL_STATE_TOUCH_SCROLL)
                {
                    int offset = fl.computeVerticalScrollOffset();
                    int extent = fl.computeVerticalScrollExtent();
                    int range = fl.computeVerticalScrollRange();

                    int percentage = (int)(100.0 * offset / (float)(range - extent));

                    if (percentage <= SCROLL_CEIL && !isRequesting) {
                        getChunk(false);
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentManager.setCurrentActivity(ChatActivity.class);
        sendSeenRequest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_chat);
        index = 0;
        list = new ArrayList();
        isRequesting = false;
        BackButton.handle(this);
        setMessageInputListener();
        setDataFromIntent();
        initList();
        initSocket();
        getChunk(true, false);
    }
}
