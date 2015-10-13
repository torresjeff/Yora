package com.example.torre.yora.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Contacts;
import com.example.torre.yora.services.Events;
import com.example.torre.yora.services.Messages;
import com.example.torre.yora.services.entities.ContactRequest;
import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.services.entities.UserDetails;
import com.example.torre.yora.views.MainActivityAdapter;
import com.example.torre.yora.views.MainNavDrawer;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MainActivity extends BaseAuthenticatedActivity implements View.OnClickListener, MainActivityAdapter.MainActivityListener
{
    private static final int REQUEST_SHOW_MESSAGE = 1;
    private MainActivityAdapter adapter;
    private List<Message> messages;
    private List<ContactRequest> contactRequests;

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Timeline");
        setNavDrawer(new MainNavDrawer(this));

        findViewById(R.id.activity_main_newMessageButton).setOnClickListener(this);

        adapter = new MainActivityAdapter(this, this);
        messages = adapter.getMessages();
        contactRequests = adapter.getContactRequests();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerView);
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        scheduler.invokeEveryMilliseconds(new Runnable()
        {
            @Override
            public void run()
            {
                onRefresh();
            }
        }, 1000 * 60 * 3);


    }

    @Override
    public void onRefresh()
    {
        swipeRefresh.setRefreshing(true);
        bus.post(new Messages.SearchMessagesRequest(false, true));
        bus.post(new Contacts.GetContactRequestsRequest(false));
    }

    @Subscribe
    public void onMessagesLoaded(final Messages.SearchMessagesResponse response)
    {
        scheduler.invokeOnResume(Messages.SearchMessagesResponse.class, new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefresh.setRefreshing(false);

                if (!response.succeeded())
                {
                    response.showErrorToast(MainActivity.this);
                    return;
                }

                messages.clear();
                messages.addAll(response.messages);

                adapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    public void onContactRequestsLoaded(final Contacts.GetContactRequestsResponse response)
    {
        scheduler.invokeOnResume(Contacts.GetContactRequestsResponse.class, new Runnable()
        {
            @Override
            public void run()
            {
                swipeRefresh.setRefreshing(false);

                if (!response.succeeded())
                {
                    response.showErrorToast(MainActivity.this);
                    return;
                }

                contactRequests.clear();
                contactRequests.addAll(response.requests);
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.activity_main_newMessageButton:
                startActivity(new Intent(this, NewMessageActivity.class));
                return;
        }
    }

    @Override
    public void onMessageClicked(Message message)
    {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivityForResult(intent, REQUEST_SHOW_MESSAGE);
    }

    @Override
    public void onContactRequestClicked(final ContactRequest request, final int position)
    {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_display, null);
        ImageView avatar = (ImageView) dialogView.findViewById(R.id.dialog_user_display_avatar);
        TextView displayName = (TextView) dialogView.findViewById(R.id.dialog_user_display_displayName);

        UserDetails user = request.getUser();
        displayName.setText(user.getDisplayName());
        Picasso.with(this).load(user.getAvatarUrl()).into(avatar);

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == dialog.BUTTON_NEUTRAL)
                {
                    return;
                }

                boolean doAccept = (which == dialog.BUTTON_POSITIVE);

                contactRequests.remove(request);
                adapter.notifyItemRemoved(position + 1);

                if (contactRequests.size() == 0)
                {
                    adapter.notifyItemRemoved(0);
                }

                bus.post(new Contacts.RespondToContactRequestRequest(request.getUser().getId(), doAccept));
            }
        };

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Respond to contact request")
                .setPositiveButton("Accept", clickListener)
                .setNeutralButton("Cancel", clickListener)
                .setNegativeButton("Reject", clickListener)
                .setCancelable(false)
                .create();

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_SHOW_MESSAGE)
        {
            int messageId = data.getIntExtra(MessageActivity.RESULT_EXTRA_MESSAGE_ID, -1);

            if (messageId == -1)
            {
                return;
            }

            for (int i = 0 ; i < messages.size(); ++i)
            {
                Message message = messages.get(i);
                if (message.getId() == messageId)
                {
                    if (resultCode == MessageActivity.REQUEST_IMAGE_DELETED)
                    {
                        messages.remove(message);
                    }
                    else
                    {
                        message.setIsRead(true);
                    }

                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }
    }

    @Subscribe
    public void onNotification(final Events.OnNotificationReceivedEvent event)
    {
        scheduler.invokeOnResume(event.getClass(), new Runnable()
        {
            @Override
            public void run()
            {
                if (event.entityOwnerId == application.getAuth().getUser().getId())
                {
                    return;
                }

                if (event.entityType == Events.ENTITY_MESSAGE)
                {
                    if (event.operationType == Events.OPERATION_CREATED)
                    {
                        bus.post(new Messages.SearchMessagesRequest(false, true));
                    }
                    else
                    {
                        for (int i = 0; i < messages.size(); ++i)
                        {
                            if (messages.get(i).getId() == event.entityId)
                            {
                                messages.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
                else if (event.entityType == Events.ENTITY_CONTACT_REQUEST)
                {
                    if (event.operationType == Events.OPERATION_CREATED)
                    {
                        bus.post(new Contacts.GetContactsRequest(false));
                    }
                    else
                    {
                        for (int i = 0; i < contactRequests.size(); ++i)
                        {
                            if (contactRequests.get(i).getUser().getId() == event.entityId)
                            {
                                contactRequests.remove(i);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        });

    }
}
