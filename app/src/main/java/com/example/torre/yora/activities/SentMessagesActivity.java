package com.example.torre.yora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Messages;
import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.views.MainNavDrawer;
import com.example.torre.yora.views.MessagesAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;


public class SentMessagesActivity extends BaseAuthenticatedActivity implements MessagesAdapter.OnMessageClickedListener
{
    private static final int REQUEST_VIEW_MESSAGE = 1;
    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private View progressFrame;

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_sent_messages);
        setNavDrawer(new MainNavDrawer(this));
        getSupportActionBar().setTitle("Sent messages");

        adapter = new MessagesAdapter(this, this);
        messages = new ArrayList<>();
        messages = adapter.getMessages();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_sent_messages_messages);
        recyclerView.setAdapter(adapter);

        if (isTablet)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        progressFrame = findViewById(R.id.activity_sent_messages_progressFrame);

        scheduler.postEveryMilliseconds(new Messages.SearchMessagesRequest(true, false), 1000*60*3); //Every 3 minutes, refresh our messages
    }

    @Override
    public void onMessageClicked(Message message)
    {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_VIEW_MESSAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode != REQUEST_VIEW_MESSAGE || resultCode != MessageActivity.REQUEST_IMAGE_DELETED)
        {
            return;
        }

        int messageId = data.getIntExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID, -1); //Default value = -1, if RESULT_EXTRA_MESSAGE_ID is not found
        if (messageId == -1)
        {
            return;
        }

        for (int i = 0; i < messages.size(); ++i)
        {
            Message message = messages.get(i);
            if (message.getId() != messageId)
            {
                continue;
            }

            messages.remove(i);
            adapter.notifyItemRemoved(i);
            break;
        }
    }

    @Subscribe
    public void onMessagesLoad(Messages.SearchMessagesResponse response)
    {
        response.showErrorToast(this);

        int oldMessagesSize = messages.size();
        messages.clear();
        adapter.notifyItemRangeRemoved(0, oldMessagesSize);

        messages.addAll(response.messages);
        adapter.notifyItemRangeInserted(0, messages.size());

        progressFrame.setVisibility(View.GONE);
    }
}
