package com.example.torre.yora.services.entities;


//No personal information about the user
public class UserDetails
{
    private int id;
    private boolean isContact;
    private String displayName;
    private String userName;
    private String avatarUrl;

    public UserDetails(int id, boolean isContact, String displayName, String userName, String avatarUrl)
    {
        this.id = id;
        this.isContact = isContact;
        this.displayName = displayName;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }

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
