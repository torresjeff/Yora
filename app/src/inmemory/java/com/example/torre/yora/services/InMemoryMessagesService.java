package com.example.torre.yora.services;

import com.example.torre.yora.infrastructure.YoraApplication;
import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;


public class InMemoryMessagesService extends BaseInMemoryService
{
    public InMemoryMessagesService(YoraApplication application)
    {
        super(application);
    }

    @Subscribe
    public void deleteMessage (Messages.DeleteMessageRequest request)
    {
        Messages.DeleteMessageResponse response = new Messages.DeleteMessageResponse();
        response.messageId = request.messageId;
        postDelayed(response);
    }

    @Subscribe
    public void searchMessages(Messages.SearchMessagesRequest request)
    {
        Messages.SearchMessagesResponse response = new Messages.SearchMessagesResponse();
        response.messages = new ArrayList<>();

        UserDetails[] users = new UserDetails[10];

        for (int i = 0; i < users.length; ++i)
        {
            String stringId = Integer.toString(i);
            users[i] = new UserDetails(i, true, "User " + stringId, "user_" + stringId, "http://www.gravatar.com/avatar/" + stringId + "?d=identicon");

        }

        Random random = new Random();
        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, -100); //go back 100 days

        for (int i = 0; i < 100; ++i)
        {
            boolean isFromUs;

            if (request.includeReceivedMessages && request.includeSentMessages)
            {
                isFromUs = random.nextBoolean();
            }
            else
            {
                isFromUs = !request.includeReceivedMessages;
            }

            date.set(Calendar.MINUTE, random.nextInt(60*24));

            String numberString = Integer.toString(i);
            response.messages.add(new Message(i, (Calendar)date.clone(), "Short Message " + numberString, "Long message " + numberString, "", users[random.nextInt(users.length)],
                    isFromUs, i > 4));
        }

        postDelayed(response, 2000);
    }

    @Subscribe
    public void sendMessage(Messages.SendMessageRequest request)
    {
        postDelayed(new Messages.SendMessageResponse(), 1500, 3000);
    }
}
