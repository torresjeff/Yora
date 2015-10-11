package com.example.torre.yora.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.services.Contacts;
import com.example.torre.yora.views.ContactRequestsAdapter;
import com.squareup.otto.Subscribe;

public class PendingContactRequestsFragment extends BaseFragment
{
    private View progressFrame;
    private ContactRequestsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pending_contact_requests, container, false);

        progressFrame = view.findViewById(R.id.fragment_pending_contact_requests_progressFrame);
        adapter = new ContactRequestsAdapter((BaseActivity)getActivity());

        ListView listView = (ListView) view.findViewById(R.id.fragment_pending_contact_requests_list);
        listView.setAdapter(adapter);

        //we want to get the friend requests that we've sent
        bus.post(new Contacts.GetContactRequestsRequest(true));

        return view;
    }

    @Subscribe
    public void onGetContactsRequests(final Contacts.GetContactRequestsResponse response)
    {
        scheduler.invokeOnResume(Contacts.GetContactRequestsResponse.class, new Runnable()
        {
            @Override
            public void run()
            {
                progressFrame.animate().alpha(0).setDuration(250).withEndAction(new Runnable()
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
                adapter.addAll(response.requests);
            }
        });
    }
}
