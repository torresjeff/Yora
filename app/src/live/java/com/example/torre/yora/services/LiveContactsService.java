package com.example.torre.yora.services;

import com.example.torre.yora.infrastructure.RetrofitCallbackPost;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.squareup.otto.Subscribe;

/**
 * Created by Jeffrey Torres on 12/10/2015.
 */
public class LiveContactsService extends BaseLiveService
{
    public LiveContactsService(YoraWebService api, YoraApplication application)
    {
        super(api, application);
    }

    @Subscribe
    public void searchUsers(Contacts.SearchUsersRequest request)
    {
        api.searchUsers(request.query, new RetrofitCallbackPost<>(Contacts.SearchUsersResponse.class, bus));
    }

    @Subscribe
    public void sendContactRequest(Contacts.SendContactRequestRequest request)
    {
        api.sendContactRequest(request.userId, new RetrofitCallbackPost<>(Contacts.SendContactRequestResponse.class, bus));
    }

    @Subscribe
    public void getContactRequests(Contacts.GetContactRequestsRequest request)
    {
        if (request.fromUs)
        {
            api.getContactRequestsFromUs(new RetrofitCallbackPost<>(Contacts.GetContactRequestsResponse.class, bus));
        }
        else
        {
            api.getContactRequestsToUs(new RetrofitCallbackPost<>(Contacts.GetContactRequestsResponse.class, bus));
        }
    }

    @Subscribe
    public void respondToContactRequest(Contacts.RespondToContactRequestRequest request)
    {
        String response;
        if (request.accept)
        {
            response = "accept";
        }
        else
        {
            response = "reject";
        }

        api.respondToContactRequest(
                request.contactRequestId,
                new YoraWebService.RespondToContactRequest(response),
                new RetrofitCallbackPost<>(Contacts.RespondToContactRequestResponse.class, bus));
    }

    @Subscribe
    public void getContacts(Contacts.GetContactsRequest request)
    {
        api.getContacts(new RetrofitCallbackPost<>(Contacts.GetContactsResponse.class, bus));
    }

    @Subscribe
    public void removeContact(Contacts.RemoveContactRequest request)
    {
        api.removeContact(request.contactId, new RetrofitCallbackPost<>(Contacts.RemoveContactResponse.class, bus));
    }
}
