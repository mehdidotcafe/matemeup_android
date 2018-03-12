package com.matemeup.matemeup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.matemeup.matemeup.R;

import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.rendering.RemoteImageLoader;

import java.util.List;

public class UserChatAdapter extends ArrayAdapter<UserChat> {
    private Context context;
    private List<UserChat> users;

    public UserChatAdapter(Context ctx, int textViewResourceId, List<UserChat> u){
        super(ctx, textViewResourceId, u);
        context = ctx;
        users = u;
    }

    public void setList(List<UserChat> u) {
        users = u;
        System.out.println("users " + users);
        notifyDataSetInvalidated();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.chat_user_list, null, false);
        UserChat user = users.get(position);
        ((TextView)view.findViewById(R.id.username_container)).setText(user.name);
        ImageView img = view.findViewById(R.id.avatar_container);
        img.setClipToOutline(true);
        RemoteImageLoader.load(img, user.avatar);
        return view;
    }
}

