package com.example.torre.yora.activities;

import android.content.Intent;
import android.os.Bundle;

/**
 * Forces activities that extend this class, that the user must be logged in
 */
public abstract class BaseAuthenticatedActivity extends BaseActivity
{
    @Override
    protected final void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!application.getAuth().getUser().isLoggedIn())
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        //If we are logged in then we implement this method. This makes sure this code doesn't run if we aren't logged in.
        onYoraCreate(savedInstanceState);
    }

    protected abstract void onYoraCreate(Bundle savedInstanceState);
}
