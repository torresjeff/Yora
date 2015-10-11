package com.example.torre.yora.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.AddContactActivity;
import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.activities.ContactActivity;
import com.example.torre.yora.activities.ContactsActivity;
import com.example.torre.yora.services.Contacts;
import com.example.torre.yora.services.entities.UserDetails;
import com.example.torre.yora.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class ContactsFragment extends BaseFragment implements AdapterView.OnItemClickListener
{
    private UserDetailsAdapter adapter;
    private View progressFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        setHasOptionsMenu(true); //let Android know that our Fragment has an options menu

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        adapter = new UserDetailsAdapter((BaseActivity)getActivity());
        progressFrame = view.findViewById(R.id.fragment_contacts_progressFrame);

        ListView listView = (ListView) view.findViewById(R.id.fragment_contacts_contactList);
        listView.setEmptyView(view.findViewById(R.id.fragment_contacts_emptyList)); //set a view when the list is empty
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        bus.post(new Contacts.GetContactsRequest(false));

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        UserDetails details = adapter.getItem(position);

        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(ContactActivity.EXTRA_USER_DETAILS, details);
        startActivity(intent);
    }

    @Subscribe
    public void onContactsResponse(final Contacts.GetContactsResponse response)
    {
        scheduler.invokeOnResume(Contacts.GetContactsResponse.class, new Runnable()
        {
            @Override
            public void run()
            {
                progressFrame.animate()
                        .alpha(0)
                        .setDuration(250)
                        .withEndAction(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                progressFrame.setVisibility(View.GONE);
                            }
                        }).start();

                if (!response.succeeded())
                {
                    response.showErrorToast(getActivity());
                    return;
                }

                adapter.clear();
                adapter.addAll(response.contacts);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.fragment_contacts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.fragment_contacts_menu_addContact:
                startActivity(new Intent(getActivity(), AddContactActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
