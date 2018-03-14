package com.matemeup.matemeup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matemeup.matemeup.R;
import com.matemeup.matemeup.entities.model.HistoryChat;
import com.matemeup.matemeup.entities.rendering.AvatarRemoteImageLoader;
import com.matemeup.matemeup.entities.rendering.ChatRemoteImageLoader;

import java.util.List;

public class HistoryChatAdapter extends RecyclerView.Adapter<HistoryChatAdapter.ViewHolder> {
    private Context context;
    private List<HistoryChat> history;
    private int userId;
    private int messageViewResourceId;
    private int imageViewResourceId;

    public HistoryChatAdapter(Context ctx, int mvri, int ivri, List<HistoryChat> his, int id){
//        super(ctx, textViewResourceId, his);
        context = ctx;
        history = his;
        userId = id;
        messageViewResourceId = mvri;
        imageViewResourceId = ivri;
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    @Override
    public int getItemViewType(int position) {
        return history.get(position).type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType == 1 ? messageViewResourceId : imageViewResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HistoryChat chat = history.get(position);
        holder.display(chat);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent)
//    {
//        View view = convertView;
//        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        HistoryChat msg = history.get(position);
//        if (view == null)
//        {
//            view = inflater.inflate(R.layout.item_chat_text_history, null, false);
//        }
//
//        if (msg.senderUserId == userId)
//        {
//            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//            view.findViewById(R.id.message_container).setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//        }
//        else
//        {
//            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//            view.findViewById(R.id.message_container).setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//        }
//        if (position + 1 >= history.size() || (history.get(position + 1).senderUserId != msg.senderUserId))
//            view.findViewById(R.id.avatar_container).setVisibility(View.VISIBLE);
//        else
//            view.findViewById(R.id.avatar_container).setVisibility(View.INVISIBLE);
//
//        ((TextView)view.findViewById(R.id.message_container)).setText(msg.message);
//        AvatarRemoteImageLoader.load((ImageView)view.findViewById(R.id.avatar_container), msg.senderUserAvatar);
//        return view;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageContainer;
        private final TextView  messageContainer;
        private final ImageView avatarContainer;
        private HistoryChat currentHistory;

        public ViewHolder(final View itemView) {
            super(itemView);

            messageContainer = itemView.findViewById(R.id.message_container);
            imageContainer = itemView.findViewById(R.id.message_image_container);
            avatarContainer = itemView.findViewById(R.id.avatar_container);
        }

        public void display(HistoryChat h) {
            currentHistory = h;
            System.out.println(currentHistory.message);
            int containerId = currentHistory.type == 1 ? R.id.message_container : R.id.message_image_container;
            int sizeInDP = 16;

            int marginInDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP, context.getResources()
                            .getDisplayMetrics());

            int marginInDp2 = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, sizeInDP / 2, context.getResources()
                            .getDisplayMetrics());

            avatarContainer.setClipToOutline(true);
            AvatarRemoteImageLoader.load(avatarContainer, currentHistory.senderUserAvatar);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            if (currentHistory.senderUserId == userId)
            {
                itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                itemView.findViewById(containerId).setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            else
            {
                itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                itemView.findViewById(containerId).setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            if (getAdapterPosition() + 1 >= history.size() || (history.get(getLayoutPosition()+ 1).senderUserId != currentHistory.senderUserId))
                itemView.findViewById(R.id.avatar_container).setVisibility(View.VISIBLE);
            else
                itemView.findViewById(R.id.avatar_container).setVisibility(View.INVISIBLE);

            if (messageContainer != null) {
                messageContainer.setText(currentHistory.message);
                params.setMargins(marginInDp, marginInDp2, marginInDp, marginInDp2);

                messageContainer.setLayoutParams(params);
            }
            else if (imageContainer != null) {
                System.out.println("ON EST ICI");
                ChatRemoteImageLoader.load(imageContainer, currentHistory.message);
            }
        }
    }

}
