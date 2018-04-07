package com.matemeup.matemeup.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;
import com.matemeup.matemeup.entities.websocket.MMUWebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocket;
import com.matemeup.matemeup.entities.websocket.WebSocketCallback;
import com.matemeup.matemeup.entities.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PendingFriendAdapter extends RecyclerView.Adapter<PendingFriendAdapter.ViewHolder> {
    private Context context;
    private List<UserChat> pendings;
    private int textViewResourceId;
    private WebSocket ws;
    private Callback acceptCallback;
    private Callback refuseCallback;

    public PendingFriendAdapter(Context ctx, int tvri, List<UserChat> p, Callback acceptCb, Callback refuseCb){
        context = ctx;
        pendings = p;
        textViewResourceId = tvri;
        ws = MMUWebSocket.getInstance((Activity) context);
        acceptCallback = acceptCb;
        refuseCallback = refuseCb;
    }

    @Override
    public int getItemCount() {
        return pendings.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(textViewResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserChat user = pendings.get(position);
        holder.display(user);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView img;
        private final TextView  name;

        private UserChat currentUser;

        private void setAcceptListener(View btn) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("friendId", currentUser.id);
                    } catch (JSONException e) {}
                    ws.emit("friend.accept", obj, new WebSocketCallback() {
                        @Override
                        public void onMessage(String message, Object... args) {
                            JSONObject res = (JSONObject)args[0];

                            try {
                                if (res.getBoolean("state"))
                                    acceptCallback.success((Object)currentUser);
                                else
                                    acceptCallback.fail(res.getInt("error"));
                            } catch (JSONException e) {}
                        }
                    });
                }
            });
        }

        private void setRefuseListener(View btn) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("friendId", currentUser.id);
                    } catch (JSONException e) {}
                    ws.emit("friend.refuse", obj, new WebSocketCallback() {
                        @Override
                        public void onMessage(String message, Object... args) {
                            JSONObject res = (JSONObject)args[0];

                            try {
                                if (res.getBoolean("state"))
                                    refuseCallback.success((Object)currentUser);
                                else
                                    refuseCallback.fail(res.getInt("error"));
                            } catch (JSONException e) {}
                        }
                    });
                }
            });
        }

        public ViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.username_container);
            img = itemView.findViewById(R.id.useravatar_container);

            setAcceptListener(itemView.findViewById(R.id.accept_button));
            setRefuseListener(itemView.findViewById(R.id.refuse_button));
        }

        public void display(UserChat user) {
            currentUser = user;
            name.setText(user.name);
            img.setClipToOutline(true);
            AvatarRemoteImageLoader.load(img, user.avatar);
        }
    }

}
