package com.example.torre.yora.activities;


import android.os.Bundle;

import com.example.torre.yora.R;

/**
 * This Activity shows the details of a specific contact
 */
public class ContactActivity extends BaseAuthenticatedActivity
{
    public static final String EXTRA_USER_DETAILS = "EXTRA_USER_DETAILS"; //Passed as an extra in the intent that starts this Activity, so it knows what information to display about the specific user.

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_contact);
    }
}
