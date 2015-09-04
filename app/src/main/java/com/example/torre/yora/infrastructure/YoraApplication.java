package com.example.torre.yora.infrastructure;


import android.app.Application;

public class YoraApplication extends Application
{
    private Auth auth;

    @Override
    public void onCreate()
    {
        super.onCreate();
        auth = new Auth(this);
    }


    public Auth getAuth()
    {
        return auth;
    }
}
