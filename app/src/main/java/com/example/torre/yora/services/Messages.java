package com.example.torre.yora.services;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.services.entities.UserDetails;

import java.util.List;

public final class Messages
{
    private Messages()
    {}

    public static class DeleteMessageRequest
    {
        public int messageId;

        public DeleteMessageRequest(int messageId)
        {
            this.messageId = messageId;
        }
    }

    public static class DeleteMessageResponse extends ServiceResponse
    {
        public int messageId;
    }

    public static class SearchMessagesRequest
    {
        public int fromContactId;
        public boolean includeSentMessages;
        public boolean includeReceivedMessages;

        public SearchMessagesRequest(int fromContactId, boolean includeSentMessages, boolean includeReceivedMessages)
        {
            this.fromContactId = fromContactId;
            this.includeSentMessages = includeSentMessages;
            this.includeReceivedMessages = includeReceivedMessages;
        }

        public SearchMessagesRequest(boolean includeSentMessages, boolean includeReceivedMessages)
        {
            this.fromContactId = -1;
            this.includeSentMessages = includeSentMessages;
            this.includeReceivedMessages = includeReceivedMessages;
        }
    }

    public static class SearchMessagesResponse extends ServiceResponse
    {
        public List<Message> messages;
    }

    public static class SendMessageRequest implements Parcelable
    {
        private UserDetails recipient;
        private Uri imagePath;
        private String message;

        public SendMessageRequest()
        {}

        protected SendMessageRequest(Parcel in)
        {
            recipient = in.readParcelable(UserDetails.class.getClassLoader());
            imagePath = in.readParcelable(Uri.class.getClassLoader());
            message = in.readString();
        }

        public static final Creator<SendMessageRequest> CREATOR = new Creator<SendMessageRequest>()
        {
            @Override
            public SendMessageRequest createFromParcel(Parcel in)
            {
                return new SendMessageRequest(in);
            }

            @Override
            public SendMessageRequest[] newArray(int size)
            {
                return new SendMessageRequest[size];
            }
        };

        @Override
        public void writeToParcel(Parcel dest, int flags)
        {
            dest.writeParcelable(recipient, 0);
            dest.writeParcelable(imagePath, 0);
            dest.writeString(message);
        }

        @Override
        public int describeContents()
        {
            return 0;
        }

        public UserDetails getRecipient()
        {
            return recipient;
        }

        public void setRecipient(UserDetails recipient)
        {
            this.recipient = recipient;
        }

        public Uri getImagePath()
        {
            return imagePath;
        }

        public void setImagePath(Uri imagePath)
        {
            this.imagePath = imagePath;
        }

        public String getMessage()
        {
            return message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }

    public static class SendMessageResponse extends ServiceResponse
    {
        public Message message;
    }

    public static class MarkMessageAsReadRequest
    {
        public int messageId;

        public MarkMessageAsReadRequest(int messageId)
        {
            this.messageId = messageId;
        }
    }

    public static class MarkMessageAsReadResponse extends ServiceResponse
    {
    }

    public static class GetMessageDetailsRequest
    {
        public int id;

        public GetMessageDetailsRequest(int id)
        {
            this.id = id;
        }
    }

    public static class GetMessageDetailsResponse extends ServiceResponse
    {
        public Message message;
    }
}
