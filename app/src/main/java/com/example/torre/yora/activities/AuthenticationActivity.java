package com.example.torre.yora.activities;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.fragments.RegisterGcmFragment;
import com.example.torre.yora.infrastructure.Auth;
import com.example.torre.yora.services.Account;
import com.squareup.otto.Subscribe;

public class AuthenticationActivity extends BaseActivity implements RegisterGcmFragment.GcmRegistrationCallback
{

    private Auth auth;
    public static final String EXTRA_RETURN_TO_ACTIVITY = "EXTRA_RETURN_TO_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        //If we don't have a token then we have to log in again
        if (!application.getAuth().hasAuthToken())
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        auth = application.getAuth();

        bus.post(new Account.LoginWithLocalTokenRequest(auth.getAuthToken()));
    }

    @Subscribe
    public void onLoginWithLocalToken(Account.LoginWithLocalTokenResponse response)
    {
        //We did not successfully authenticated with our local token. We must log in again
        if (!response.succeeded())
        {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            auth.setAuthToken(null);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        RegisterGcmFragment.get(this, false, getFragmentManager());
    }

    @Override
    public void gcmFinished()
    {
        Intent intent;
        String returnTo = getIntent().getStringExtra(EXTRA_RETURN_TO_ACTIVITY);

        if (returnTo != null)
        {
            try
            {
                intent = new Intent(this, Class.forName(returnTo));
            }
            catch (Exception ignored)
            {
                intent = new Intent(this, MainActivity.class);
            }
        }
        else
        {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
