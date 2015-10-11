package com.example.torre.yora.views;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.services.entities.Message;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> implements View.OnClickListener
{
    private final LayoutInflater layoutInflater;
    private final BaseActivity activity;
    private final OnMessageClickedListener listener; //we create this class ourselves
    private final ArrayList<Message> messages;

    public MessagesAdapter(BaseActivity activity, OnMessageClickedListener listener)
    {
        this.activity = activity;
        this.listener = listener;
        messages = new ArrayList<>();
        layoutInflater = activity.getLayoutInflater();
    }

    public ArrayList<Message> getMessages()
    {
        return messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View view = layoutInflater.inflate(R.layout.list_item_message, viewGroup, false);
        view.setOnClickListener(this);

        MessageViewHolder viewHolder = new MessageViewHolder(view);
        viewHolder.getBackgroundView().setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int i)
    {
        Message message = messages.get(i);
        messageViewHolder.getBackgroundView().setTag(message);
        messageViewHolder.populate(activity, message); //Delegate the population of the View to the ViewHolder
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

    @Override
    public void onClick(View view)
    {
        if (view.getTag() instanceof Message)
        {
            Message message = (Message) view.getTag();
            listener.onMessageClicked(message);
        }
    }

    public interface OnMessageClickedListener
    {
        void onMessageClicked(Message message);
    }
}
