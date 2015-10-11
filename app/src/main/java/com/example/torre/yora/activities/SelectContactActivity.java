package com.example.torre.yora.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Contacts;
import com.example.torre.yora.services.entities.UserDetails;
import com.example.torre.yora.views.UserDetailsAdapter;
import com.squareup.otto.Subscribe;

public class SelectContactActivity extends BaseAuthenticatedActivity implements AdapterView.OnItemClickListener
{
    public static final String RESULT_CONTACT = "RESULT_CONTACT";
    public static final int REQUEST_ADD_FRIEND = 1;

    private UserDetailsAdapter adapter;
    private View progressBar;

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_select_contact);
        getSupportActionBar().setTitle("Select contact");

        progressBar = findViewById(R.id.activity_select_contact_progressFrame);

        adapter = new UserDetailsAdapter(this);
        ListView listView = (ListView) findViewById(R.id.activity_select_contact_listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        bus.post(new Contacts.GetContactsRequest(true));
    }

    @Subscribe
    public void onContactsReceived(Contacts.GetContactsResponse response)
    {
        response.showErrorToast(this);

        progressBar.setVisibility(View.GONE);

        adapter.clear();
        adapter.addAll(response.contacts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //TODO: verificar que esto esta bien
        UserDetails user = adapter.getItem(position);
        Intent intent = new Intent();
        intent.putExtra(RESULT_CONTACT, user);
        setResult(RESULT_OK, intent);
        finish();
    }
}
