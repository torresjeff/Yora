package com.example.torre.yora.services.entities;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Message implements Parcelable
{
    private int id;
    private Calendar createdAt;
    private String shortMessage;
    private String longMessage;
    private String imageUrl;
    private UserDetails otherUser;
    private boolean isFromUs;
    private boolean isRead;
    private boolean isSelected;

    public Message(int id, Calendar createdAt, String shortMessage, String longMessage, String imageUrl, UserDetails otherUser, boolean isFromUs, boolean isRead)
    {
        this.id = id;
        this.createdAt = createdAt;
        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
        this.imageUrl = imageUrl;
        this.otherUser = otherUser;
        this.isFromUs = isFromUs;
        this.isRead = isRead;
    }

    public Message(Parcel parcel)
    {
        this.id = parcel.readInt();

        this.createdAt = new GregorianCalendar();
        this.createdAt.setTimeInMillis(parcel.readLong());

        this.shortMessage = parcel.readString();
        this.longMessage = parcel.readString();
        this.imageUrl = parcel.readString();
        this.otherUser = (UserDetails) parcel.readParcelable(UserDetails.class.getClassLoader());
        this.isFromUs = parcel.readByte() == 1;
        this.isRead = parcel.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel destination, int flags)
    {
        destination.writeInt(id);
        destination.writeLong(createdAt.getTimeInMillis());
        destination.writeString(shortMessage);
        destination.writeString(longMessage);
        destination.writeString(imageUrl);
        destination.writeParcelable(otherUser, 0);
        destination.writeByte((byte) (isFromUs ? 1 : 0));
        destination.writeByte((byte) (isRead ? 1 : 0));
        destination.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Creator<Message> CREATOR = new Creator<Message>()
    {
        @Override
        public Message createFromParcel(Parcel source)
        {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size)
        {
            return new Message[size];
        }
    };

    public void setIsSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }

    public int getId()
    {
        return id;
    }

    public Calendar getCreatedAt()
    {
        return createdAt;
    }

    public String getShortMessage()
    {
        return shortMessage;
    }

    public String getLongMessage()
    {
        return longMessage;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public UserDetails getOtherUser()
    {
        return otherUser;
    }

    public boolean isFromUs()
    {
        return isFromUs;
    }

    public boolean isRead()
    {
        return isRead;
    }

    public void setIsRead(boolean isRead)
    {
        this.isRead = isRead;
    }

    public boolean isSelected()
    {
        return isSelected;
    }
}
