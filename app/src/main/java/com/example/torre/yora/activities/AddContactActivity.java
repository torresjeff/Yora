package com.example.torre.yora.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Contacts;
import com.example.torre.yora.services.entities.UserDetails;
import com.example.torre.yora.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class AddContactActivity extends BaseAuthenticatedActivity implements AdapterView.OnItemClickListener
{
    public static final String RESULT_CONTACT = "RESULT_CONTACT";

    private UserDetailsAdapter adapter;
    private View progressFrame;
    private Handler handler; //Puts a delay to the query. Prevents a request every time a character is typed
    private SearchView searchView; //Edit text of our toolbar
    private String lastQuery; //For example if we query at a specific time, then before the response we query again, we need to keep track of the queries.
                            //That's why the response "echoes" back the query that was sent to the server.
    private UserDetails selectedUser;

    private Runnable searchRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            lastQuery = searchView.getQuery().toString();
            progressFrame.setVisibility(View.VISIBLE);
            bus.post(new Contacts.SearchUsersRequest(lastQuery));
        }
    };

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_add_contact);

        adapter = new UserDetailsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.activity_add_contact_usersListView);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);

        progressFrame = findViewById(R.id.activity_add_contact_progresFrame);
        progressFrame.setVisibility(View.GONE);

        handler = new Handler();
        searchView = new SearchView(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //removes the title and shows the custom view that we're going to assign
        actionBar.setCustomView(searchView);

        searchView.setIconified(false); //Turns into its actual full view. Instead of clicking an icon and then displaying the Search View.
        searchView.setQueryHint("Search for users...");
        searchView.setLayoutParams(new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            //When the user presses enter?
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return true;
            }

            //Whenever the user types something into the searchview
            @Override
            public boolean onQueryTextChange(String query)
            {
                //If the query is less than 3 characters long, then don't do anything
                if (query.length() < 3)
                {
                    return true;
                }


                handler.removeCallbacks(searchRunnable); //If the handler has this runnable scheduled, remove it so it's not invoked
                handler.postDelayed(searchRunnable, 750); //reschedule it to happen in 750 milliseconds
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            //When the user closes the searchview
            @Override
            public boolean onClose()
            {
                //When the user clicks the "X" in the SearchView, close the whole Activity
                setResult(RESULT_CANCELED);
                finish();
                return true;
            }
        });
    }

    @Subscribe
    public void onUsersSearched(Contacts.SearchUsersResponse response)
    {
        progressFrame.setVisibility(View.GONE);

        if (!response.succeeded())
        {
            response.showErrorToast(this);
            return;
        }

        //Discard the response if it's not the last query we entered
        if (!response.query.equals(lastQuery))
        {
            return;
        }

        adapter.clear();
        adapter.addAll(response.users);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Do you want to add this person?")
                .setPositiveButton("Send contact request", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        sendContactRequest(adapter.getItem(position));
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void sendContactRequest(UserDetails user)
    {
        selectedUser = user;
        progressFrame.setVisibility(View.VISIBLE);
        bus.post(new Contacts.SendContactRequestRequest(user.getId()));
    }

    @Subscribe
    public void onFriendRequestSent(Contacts.SendContactRequestResponse response)
    {
        if (!response.succeeded())
        {
            response.showErrorToast(this);
            progressFrame.setVisibility(View.GONE);
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(RESULT_CONTACT, selectedUser);
        setResult(RESULT_OK, intent); //pass the Intent we created as data, for the onActivityResult method in the class that called this Activity

        finish();
    }
}
