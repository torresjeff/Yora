package com.example.torre.yora.services;


import com.example.torre.yora.infrastructure.YoraApplication;
import com.example.torre.yora.services.entities.ContactRequest;
import com.example.torre.yora.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class InMemoryContactsService extends BaseInMemoryService
{
    public InMemoryContactsService(YoraApplication application)
    {
        super(application);
    }

    @Subscribe
    public void getContactsRequest(Contacts.GetContactRequestsRequest request)
    {
        Contacts.GetContactRequestsResponse response = new Contacts.GetContactRequestsResponse();
        response.requests = new ArrayList<>();

        for (int i = 0; i < 3; ++i)
        {
            response.requests.add(new ContactRequest(i, request.fromUs, createFakeUser(i, false), new GregorianCalendar()));
        }

        postDelayed(response);

    }

    @Subscribe
    public void getContacts(Contacts.GetContactRequestsRequest request)
    {
        Contacts.GetContactsResponse response = new Contacts.GetContactsResponse();
        response.contacts = new ArrayList<>();

        for (int i = 0; i < 10; ++i)
        {
            response.contacts.add(createFakeUser(i, true));
        }

        postDelayed(response);
    }

    @Subscribe
    public void sendContactRequest(Contacts.SendContactRequestRequest request)
    {
        //simulate an error if the userId we want to add is 2
        if (request.userId == 2)
        {
            Contacts.SendContactRequestResponse response = new Contacts.SendContactRequestResponse();
            response.setOperationError("Something bad happened");
            return;
        }

        postDelayed(new Contacts.SendContactRequestResponse());
    }

    @Subscribe
    public void respondToContactRequest(Contacts.RespondToContactRequestRequest request)
    {
        postDelayed(new Contacts.RespondToContactRequestResponse());
    }

    private UserDetails createFakeUser(int id, boolean isContact)
    {
        String idString = Integer.toString(id);
        return new UserDetails(id, isContact, "Contact " + idString, "Contact" + idString, "http://www.gravatar.com/avatar/" + idString + "?d=identicon&s=64");
    }
}
