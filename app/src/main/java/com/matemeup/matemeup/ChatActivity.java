package com.matemeup.matemeup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.matemeup.matemeup.adapters.HistoryChatAdapter;
import com.matemeup.matemeup.entities.Serializer;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends Layout {
    private UserChat user;
    private WebSocket ws;
    private String getURL = "global.chat.user.normal.history";
    private String sendURL = "global.chat";
    private static final int CHUNK_SIZE = 30;
    private static int index = 0;
    private HistoryChatAdapter adapter = null;

    private void sendText(String text) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("userId", user.id);
            obj.put("type", 1);
            obj.put("message", text);
        } catch (JSONException e) {return ;}


        ws.emit(sendURL, obj, null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
                try {
                    byte[] b = baos.toByteArray();
                    String temp = Base64.encodeToString(b, Base64.DEFAULT);
                } catch (NullPointerException e) {
                   System.out.println("bitmaperror1");
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    System.out.println("bitmaperror2");
                    e.printStackTrace();
                }

//                System.out.println("encoded " + encodedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendImageFromButton(View view) {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    public void sendTextFromButton(View view) {
        EditText input = (EditText)findViewById(R.id.chat_input);

        sendText(input.getText().toString());
        input.setText("");
    }

    private void bindWithAdapter(List<HistoryChat> list) {
        adapter = new HistoryChatAdapter(this, R.layout.chat_history_list, list);
        ListView listView = (ListView)findViewById(R.id.message_list);

        listView.setAdapter(adapter);
        listView.setSelection(adapter.getCount() - 1);

    }

    private void renderMessages(JSONObject data) throws JSONException {
        JSONArray messages;
        ArrayList<HistoryChat> list = new ArrayList();

        try {
            messages = new JSONArray(data.getString("history"));
        } catch (JSONException e) {
            messages = new JSONArray();
        }

        for (int i = 0; i < messages.length(); i++)
        {
            System.out.println(messages.getJSONObject(i));
            list.add(new HistoryChat(messages.getJSONObject(i)));
        }

        bindWithAdapter(list);
    }

    private void getChunk() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("userId", user.id);
            obj.put("chunkSize", CHUNK_SIZE);
            obj.put("index", index);
        } catch (JSONException e) {}

        ws.emit(getURL, obj, new WebSocketCallback()
        {
            public void onMessage(final String message, final Object... args)
            {
                JSONObject messages = (JSONObject)args[0];
                try {
                    renderMessages(messages);
                } catch (JSONException e) {}
            }
        });
    }

    private void onNewMessage(HistoryChat message) {
        ListView listView = (ListView)findViewById(R.id.message_list);
        adapter.add(message);
        adapter.notifyDataSetChanged();
        listView.setSelection(adapter.getCount() - 1);

    }

    private void initSocket() {
        ws = new MMUWebSocket(this);
        ws.on("global.chat.new", new WebSocketCallback() {
            @Override
            public void onMessage(final String message, final Object... args) {
                HistoryChat msg = new HistoryChat((JSONObject)args[0]);
                onNewMessage(msg);
            }
        });
    }

    private void setSendTextVisibility(Boolean isVisible) {
        Button imageButton = (Button)this.findViewById(R.id.send_image_button);
        Button textButton = (Button)this.findViewById(R.id.send_text_button);

        if (isVisible == true)
        {
            textButton.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.INVISIBLE);
        }
        else
        {
            textButton.setVisibility(View.INVISIBLE);
            imageButton.setVisibility(View.VISIBLE);
        }
    }

    private void getUser() {
        user = new UserChat(Serializer.unserialize(getIntent().getStringExtra("params")));

        System.out.println(user);
    }

    private void setMessageInputListener() {
        EditText input = (EditText)findViewById(R.id.chat_input);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setMessageInputListener();
        getUser();
        initSocket();
        getChunk();
    }
}
