package com.example.torre.yora.services;


import com.example.torre.yora.services.entities.ContactRequest;
import com.example.torre.yora.services.entities.UserDetails;

import java.util.List;

public class Contacts
{
    private Contacts()
    {
    }

    public static class GetContactRequestsRequest
    {
        public boolean fromUs;

        public GetContactRequestsRequest(boolean fromUs)
        {
            this.fromUs = fromUs;
        }
    }

    public static class GetContactRequestsResponse extends ServiceResponse
    {
        public List<ContactRequest> requests;
    }

    public static class GetContactsRequest
    {
        public boolean includePendingContacts; //do we want to include people that we've sent contact requests TO?

        public GetContactsRequest(boolean includePendingContacts)
        {
            this.includePendingContacts = includePendingContacts;
        }
    }

    public static class GetContactsResponse extends ServiceResponse
    {
        public List<UserDetails> contacts;
    }

    //This is the request we send when we want to add a friend/contact.
    public static class SendContactRequestRequest
    {
        public int userId;

        public SendContactRequestRequest(int userId)
        {
            this.userId = userId;
        }
    }

    public static class SendContactRequestResponse extends ServiceResponse
    {
    }

    public static class RespondToContactRequestRequest
    {
        public int contactRequestId;
        public boolean accept;

        public RespondToContactRequestRequest(int contactRequestId, boolean accept)
        {
            this.contactRequestId = contactRequestId;
            this.accept = accept;
        }
    }

    public static class RespondToContactRequestResponse extends ServiceResponse
    {
    }

}