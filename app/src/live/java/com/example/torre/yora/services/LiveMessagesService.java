package com.example.torre.yora.services;

import com.example.torre.yora.infrastructure.RetrofitCallbackPost;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.squareup.otto.Subscribe;

import java.io.File;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

/**
 * Created by Jeffrey Torres on 12/10/2015.
 */
public class LiveMessagesService extends BaseLiveService
{
    protected LiveMessagesService(YoraWebService api, YoraApplication application)
    {
        super(api, application);
    }

    @Subscribe
    public void sendMessage(Messages.SendMessageRequest request)
    {
        api.sendMessage(
                new TypedString(request.getMessage()),
                new TypedString(Integer.toString(request.getRecipient().getId())),
                new TypedFile("image/jpeg", new File(request.getImagePath().getPath())),
                new RetrofitCallbackPost<>(Messages.SendMessageResponse.class, bus));
    }

    @Subscribe
    public void searchMessages(Messages.SearchMessagesRequest request)
    {
        if (request.fromContactId != -1)
        {
            api.searchMessages(
                    request.fromContactId,
                    request.includeSentMessages,
                    request.includeReceivedMessages,
                    new RetrofitCallbackPost<>(Messages.SearchMessagesResponse.class, bus));
        }
        else
        {
            api.searchMessages(
                    request.includeSentMessages,
                    request.includeReceivedMessages,
                    new RetrofitCallbackPost<>(Messages.SearchMessagesResponse.class, bus));
        }
    }

    @Subscribe
    public void deleteMessage(Messages.DeleteMessageRequest request)
    {
        api.deleteMessage(request.messageId, new RetrofitCallbackPost<>(Messages.DeleteMessageResponse.class, bus));
    }

    @Subscribe
    public void markMessageAsRead(Messages.MarkMessageAsReadRequest request)
    {
        api.markMessageAsRead(request.messageId, new RetrofitCallbackPost<>(Messages.MarkMessageAsReadResponse.class, bus));
    }

    @Subscribe
    public void getMessageDetails(Messages.GetMessageDetailsRequest request)
    {
        api.getMessageDetails(request.id, new RetrofitCallbackPost<>(Messages.GetMessageDetailsResponse.class, bus));
    }
}
