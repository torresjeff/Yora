package com.example.torre.yora.infrastructure;


import android.content.Context;

public class Auth
{
    //We're going to keep a reference to the app's context
    //Think of the context as a service dependency
    private final Context context;
    private User user;


    public Auth(Context context)
    {
        this.context = context;
        user = new User();
    }

    public User getUser()
    {
        return user;
    }
}
