package com.matemeup.matemeup.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemeup.matemeup.ChatActivity;
import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.IntentManager;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.rendering.Alert;
import com.matemeup.matemeup.entities.rendering.AlertCallback;
import com.matemeup.matemeup.entities.rendering.RemoteImageLoader;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private WebSocket ws;
    private Context context;
    private List<UserChat> friends;
    private int textViewResourceId;

    public FriendAdapter(Context ctx, int tvri, List<UserChat> f){
        context = ctx;
        friends = f;
        textViewResourceId = tvri;
        ws = new MMUWebSocket((Activity) context);
    }
    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(textViewResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserChat user = friends.get(position);
        holder.display(user);
    }


    private void setMpListener(View button, final UserChat user) {
        final FriendAdapter self = this;
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                IntentManager.goTo(self.context, ChatActivity.class, user.toJSONObject());
            }
        };

        button.setOnClickListener(listener);
    }

    private UserChat getFriendById(int userId) {
        for (int i = 0; i < friends.size(); i++) {
            if (friends.get(i).id == userId)
                return (friends.get(i));
        }
        return null;
    }

    private void setSupprListener(View button, final UserChat user) {
        final FriendAdapter self = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                self.createAlert("Voulez-vous vraiment supprimer " + user.name + " ?", user);
            }
        });
    }

    private void createAlert(String message, final UserChat user) {

        Alert.yesNo(context, "Supprimer un ami", message, new AlertCallback() {
            public void success() {
                JSONObject obj = new JSONObject();

                try {
                    obj.put("friendId", user.id);
                } catch (JSONException e) {}
                ws.emit("friend.refuse", obj, new WebSocketCallback() {
                    @Override
                    public void onMessage(String message, Object... args) {
                        JSONObject resp = (JSONObject)args[0];
                        Boolean state = false;
                        try {
                            state = resp.getBoolean("state");
                        } catch (JSONException e) {}

                        if (state == true) {
                            friends.remove(user);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
            public void fail() {}
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView  name;

        private UserChat currentUser;

        public ViewHolder(final View itemView) {
            super(itemView);


            img = itemView.findViewById(R.id.useravatar_container);
            name = itemView.findViewById(R.id.username_container);
        }

        public void display(UserChat user) {
            currentUser = user;
            setMpListener(itemView.findViewById(R.id.friend_container), currentUser);
            setMpListener(itemView.findViewById(R.id.user_mp_button), currentUser);
            setSupprListener(itemView.findViewById(R.id.user_suppr_button), currentUser);

            name.setText(currentUser.name);

            img.setClipToOutline(true);
            RemoteImageLoader.load(img, currentUser.avatar);
        }
    }

}