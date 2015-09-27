package com.example.torre.yora.activities;

import android.os.Bundle;

import com.example.torre.yora.R;
import com.example.torre.yora.views.MainNavDrawer;


public class SentMessagesActivity extends BaseAuthenticatedActivity
{

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_sent_messages);
        setNavDrawer(new MainNavDrawer(this));
    }
}
