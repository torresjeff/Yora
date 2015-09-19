package com.example.torre.yora.activities;

import com.example.torre.yora.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.torre.yora.fragments.LoginFragment;


public class LoginActivity extends BaseActivity implements LoginFragment.LoginCallback
{
    private static final int REQUEST_NARROW_LOGIN = 1;
    private static final int REQUEST_REGISTER = 2;
    private static final int REQUEST_EXTERNAL_LOGIN = 3;

    private View loginButton;
    private View registerButton;
    private View facebookLoginButton;
    private View googleLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.activity_login_login);
        registerButton = findViewById(R.id.activity_login_register);
        facebookLoginButton = findViewById(R.id.activity_login_facebook);
        googleLoginButton = findViewById(R.id.activity_login_google);

        //When we're in tablet mode, this button won't appear. We can know in which mode we're in by finding the buttons.
        if (loginButton != null) //if we're not in tablet mode
        {
            loginButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    startActivityForResult(new Intent(getApplicationContext(), LoginNarrowActivity.class), REQUEST_NARROW_LOGIN);
                }
            });
        }

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivityForResult(new Intent(getApplicationContext(), RegisterActivity.class), REQUEST_REGISTER);
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doExternalLogin("Facebook");
            }
        });

        googleLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                doExternalLogin("Google");
            }
        });

    }

    /**
     * Starts the ExternalLoginActivity, to login with Facebook/Google.
     * @param externalService name of the external service (eg. Facebook, Google).
     */
    private void doExternalLogin(String externalService)
    {
        Intent intent = new Intent(this, ExternalLoginActivity.class);
        intent.putExtra(ExternalLoginActivity.EXTRA_EXTERNAL_SERVICE, externalService);
        startActivityForResult(intent, REQUEST_EXTERNAL_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK)
        {
            return;
        }

        if (requestCode == REQUEST_NARROW_LOGIN ||
                requestCode == REQUEST_REGISTER ||
                requestCode == REQUEST_EXTERNAL_LOGIN)
        {
            finishLogin();
        }
    }

    private void finishLogin()
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onLoggedIn()
    {
        finishLogin();
    }
}
