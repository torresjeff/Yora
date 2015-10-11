package com.example.torre.yora.services.entities;


import android.os.Parcel;
import android.os.Parcelable;

//No personal information about the user
public class UserDetails implements Parcelable
{
    private final int id;
    private final boolean isContact;
    private final String displayName;
    private final String userName;
    private final String avatarUrl;

    public UserDetails(int id, boolean isContact, String displayName, String userName, String avatarUrl)
    {
        this.id = id;
        this.isContact = isContact;
        this.displayName = displayName;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }


    //EVERYTHING NEEDS TO BE WRITTEN/LOADED IN THE SAME ORDER
    private UserDetails(Parcel parcel)
    {
        this.id = parcel.readInt();
        this.isContact = parcel.readByte() == 1;
        this.displayName = parcel.readString();
        this.userName = parcel.readString();
        this.avatarUrl = parcel.readString();
    }

    @Override
    public void writeToParcel(Parcel destination, int flags)
    {
        //Responsible for saving data
        destination.writeInt(id);
        destination.writeByte((byte) (isContact ? 1 : 0)); //To write booleans, write Bytes instead. There is no native way of writing a boolean. Use this syntax
        destination.writeString(displayName);
        destination.writeString(userName);
        destination.writeString(avatarUrl);
    }



    @Override
    public int describeContents()
    {
        return 0;
    }

    //EVERYTHING NEEDS TO BE WRITTEN/LOADED IN THE SAME ORDER

    //this variable must be named CREATOR (all capital)
    public static final Creator<UserDetails> CREATOR = new Creator<UserDetails>()
    {
        @Override
        public UserDetails createFromParcel(Parcel source)
        {
            //Responsible for loading data
            //Overload our UserDetails constructor to accept a Parcel
            return new UserDetails(source);
        }

        @Override
        public UserDetails[] newArray(int size)
        {
            return new UserDetails[size];
        }
    };

    public int getId()
    {
        return id;
    }

    public boolean isContact()
    {
        return isContact;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }
}
