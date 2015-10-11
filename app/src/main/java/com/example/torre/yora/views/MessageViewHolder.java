package com.example.torre.yora.views;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.torre.yora.R;
import com.example.torre.yora.services.entities.Message;
import com.squareup.picasso.Picasso;

public class MessageViewHolder extends RecyclerView.ViewHolder
{
    private ImageView avatar;
    private TextView displayName;
    private TextView createdAt;
    private CardView cardView;
    private TextView sentReceived;
    private View backgroundView;

    public MessageViewHolder(View view)
    {
        super(view);

        cardView = (CardView) view;
        avatar = (ImageView) view.findViewById(R.id.list_item_message_avatar);
        displayName = (TextView) view.findViewById(R.id.list_item_message_displayName);
        createdAt = (TextView) view.findViewById(R.id.list_item_message_createdAt);
        sentReceived = (TextView) view.findViewById(R.id.list_item_message_sentReceived);
        backgroundView= view.findViewById(R.id.list_item_message_background);
    }

    public View getBackgroundView()
    {
        return backgroundView;
    }

    public void populate(Context context, Message message)
    {
        itemView.setTag(message);

        Picasso.with(context).load(message.getOtherUser().getAvatarUrl()).into(avatar);

        String createdAt = DateUtils.formatDateTime(context, message.getCreatedAt().getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        sentReceived.setText(message.isFromUs() ? "Sent " : "Received ");
        displayName.setText(message.getOtherUser().getDisplayName());
        this.createdAt.setText(createdAt);

        int colorResourceId;
        if (message.isSelected())
        {
            colorResourceId = R.color.list_item_message_background_selected;
            cardView.setCardElevation(5);
        }
        else if (message.isRead())
        {
            colorResourceId = R.color.list_item_message_background;
            cardView.setCardElevation(2);
        }
        else
        {
            colorResourceId = R.color.list_item_message_background_unread;
            cardView.setCardElevation(3);
        }

        cardView.setCardBackgroundColor(context.getResources().getColor(colorResourceId));
    }
}
