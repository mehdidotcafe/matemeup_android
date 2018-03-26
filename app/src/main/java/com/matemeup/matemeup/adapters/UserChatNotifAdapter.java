package com.matemeup.matemeup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.model.UserChatNotif;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;

import java.util.List;

public class UserChatNotifAdapter extends RecyclerView.Adapter<UserChatNotifAdapter.ViewHolder> {
    private Context context;
    private List<UserChatNotif> users;
    private int textViewResourceId;
    private final OnItemClickListener listener;

    public UserChatNotifAdapter(Context ctx, int textViewResourceId, List<UserChatNotif> u, OnItemClickListener listener){
        this.textViewResourceId = textViewResourceId;
        this.listener = listener;
        context = ctx;
        users = u;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public UserChatNotifAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(textViewResourceId, parent, false);
        return new UserChatNotifAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserChatNotif chat = users.get(position);
        holder.bind(chat, listener);
        holder.display(chat);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView        notifCount;
        private UserChatNotif   currentUser;

        public ViewHolder(final View itemView) {
            super(itemView);
            notifCount = itemView.findViewById(R.id.user_notif_count);
        }

        public void bind(final UserChatNotif item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

        public void display(UserChatNotif user) {
            System.out.println("user adapter " + user);
            currentUser = user;
            View view = itemView;
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (view == null)
                view = inflater.inflate(R.layout.item_chat_user, null, false);
            ((TextView)view.findViewById(R.id.username_container)).setText(user.name);
            ImageView img = view.findViewById(R.id.avatar_container);

            if (user.notif > 0) {
                if (notifCount.getVisibility() != View.VISIBLE)
                    notifCount.setVisibility(View.VISIBLE);
                notifCount.setText(String.valueOf(user.notif));
            } else if (notifCount.getVisibility() == View.VISIBLE)
                notifCount.setVisibility(View.INVISIBLE);
            img.setClipToOutline(true);
            AvatarRemoteImageLoader.load(img, user.avatar);
        }
    }


    public void setList(List<UserChatNotif> u) {
        users = u;
        notifyDataSetChanged();
    }
}
