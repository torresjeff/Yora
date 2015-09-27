package com.example.torre.yora.activities;


import android.os.Bundle;

import com.example.torre.yora.R;
import com.example.torre.yora.views.MainNavDrawer;

public class ContactsActivity extends BaseAuthenticatedActivity
{
    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_contacts);
        setNavDrawer(new MainNavDrawer(this));
    }
}
