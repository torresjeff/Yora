package com.example.torre.yora.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Contacts;
import com.example.torre.yora.services.Messages;
import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.services.entities.UserDetails;
import com.example.torre.yora.views.MessagesAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * This Activity shows the details of a specific contact
 */
public class ContactActivity extends BaseAuthenticatedActivity implements MessagesAdapter.OnMessageClickedListener
{
    public static final String EXTRA_USER_DETAILS = "EXTRA_USER_DETAILS"; //Passed as an extra in the intent that starts this Activity, so it knows what information to display about the specific user.

    private static final int REQUEST_SEND_MESSAGE = 1;

    private UserDetails userDetails;
    private MessagesAdapter adapter;
    private ArrayList<Message> messages;
    private View progressFrame;
    public static final int RESULT_USER_REMOVED = 101;

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_contact);
        userDetails = getIntent().getParcelableExtra(EXTRA_USER_DETAILS);


        //if the person starting this Activity sent the user details through an extra, then use that data. If not make a fake user for testing purposes.
        //This is to run directly through the IDE: run the ContactActivity directly
        if (userDetails == null)
        {
            userDetails = new UserDetails(1, true, "A contact", "a_contact", "http://www.gravatar.com/avatar/1.jpg");
        }

        getSupportActionBar().setTitle(userDetails.getDisplayName());
        toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        adapter = new MessagesAdapter(this, this);
        messages = adapter.getMessages();

        progressFrame = findViewById(R.id.activity_contact_progressFrame);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_contact_messages);

        if (isTablet)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        recyclerView.setAdapter(adapter);



        scheduler.postEveryMilliseconds(new Messages.SearchMessagesRequest(userDetails.getId(), true, true), 1000*60*3);
    }

    @Override
    public void onMessageClicked(Message message)
    {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Subscribe
    public void onMessagesReceived(final Messages.SearchMessagesResponse response)
    {
        scheduler.invokeOnResume(Messages.SearchMessagesResponse.class, new Runnable()
        {
            @Override
            public void run()
            {
                progressFrame.setVisibility(View.GONE);

                if (!response.succeeded())
                {
                    response.showErrorToast(ContactActivity.this);
                    return;
                }

                int oldSize = messages.size();
                messages.clear();
                adapter.notifyItemRangeRemoved(0, oldSize);

                messages.addAll(response.messages);
                adapter.notifyItemRangeInserted(0, messages.size());
            }
        });
    }

    private void doRemoveContact()
    {
        progressFrame.setVisibility(View.VISIBLE);
        bus.post(new Contacts.RemoveContactRequest(userDetails.getId()));
    }

    @Subscribe
    public void onRemovedContact(final Contacts.RemoveContactResponse response)
    {
        scheduler.invokeOnResume(Contacts.RemoveContactResponse.class, new Runnable()
        {
            @Override
            public void run()
            {
                if (!response.succeeded())
                {
                    response.showErrorToast(ContactActivity.this);
                    progressFrame.setVisibility(View.VISIBLE);
                    return;
                }

                setResult(RESULT_USER_REMOVED);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.activity_contact_menu_newMessage)
        {
            Intent intent = new Intent(this, NewMessageActivity.class);
            intent.putExtra(NewMessageActivity.EXTRA_CONTACT, userDetails);
            startActivityForResult(intent, REQUEST_SEND_MESSAGE);
            return true;
        }

        if (id == R.id.activity_contact_menu_removeFriend)
        {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Remove friend?")
                    .setPositiveButton("Remove", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            doRemoveContact();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();

            dialog.show();
            return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_SEND_MESSAGE && resultCode == RESULT_OK)
        {
            progressFrame.setVisibility(View.VISIBLE);
            bus.post(new Messages.SearchMessagesRequest(userDetails.getId(), true, true));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
