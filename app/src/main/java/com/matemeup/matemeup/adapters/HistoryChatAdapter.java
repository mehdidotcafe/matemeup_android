package com.matemeup.matemeup.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.model.UserChat;
import com.matemeup.matemeup.entities.rendering.RemoteImageLoader;

import java.util.List;

public class HistoryChatAdapter extends ArrayAdapter<HistoryChat> {
    private Context context;
    private List<HistoryChat> history;

    public HistoryChatAdapter(Context ctx, int textViewResourceId, List<HistoryChat> his){
        super(ctx, textViewResourceId, his);
        context = ctx;
        history = his;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        View view = convertView;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.chat_history_list, null, false);
        HistoryChat msg = history.get(position);
        System.out.println("Hello dans getView " + msg.message);

        ((TextView)view.findViewById(R.id.username_container)).setText(msg.senderUserName);
        ((TextView)view.findViewById(R.id.message_container)).setText(msg.message);
        return view;
    }

}
