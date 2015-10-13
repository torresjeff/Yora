package com.example.torre.yora.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Account;
import com.squareup.otto.Subscribe;

public class RegisterActivity extends BaseActivity
{
    public static final String EXTRA_EXTERNAL_PROVIDER = "EXTRA_EXTERNAL_PROVIDER";
    public static final String EXTRA_EXTERNAL_USERNAME = "EXTRA_EXTERNAL_USERNAME";
    public static final String EXTRA_EXTERNAL_TOKEN = "EXTRA_EXTERNAL_TOKEN";

    private EditText userName;
    private EditText email;
    private EditText password;
    private Button registerButton;
    private View progressBar;
    private String defaultRegisterButtonText;

    private boolean isExternalLogin;
    private String externalToken;
    private String externalProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = (EditText)findViewById(R.id.activity_register_username);
        email = (EditText)findViewById(R.id.activity_register_email);
        password= (EditText)findViewById(R.id.activity_register_password);
        progressBar = findViewById(R.id.activity_register_progressBar);
        registerButton = (Button)findViewById(R.id.activity_register_registerButton);

        progressBar.setVisibility(View.GONE); //Invisible + doesn't get put into any view calculations -- practically doesn't exist

        defaultRegisterButtonText = registerButton.getText().toString();

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                registerButton.setText("");
                registerButton.setEnabled(false);
                userName.setEnabled(false);
                password.setEnabled(false);
                email.setEnabled(false);

                if (isExternalLogin)
                {
                    bus.post(new Account.RegisterWithExternalTokenRequest(userName.getText().toString(), email.getText().toString(), externalProvider, externalToken));
                }
                else
                {
                    bus.post(new Account.RegisterRequest(userName.getText().toString(), email.getText().toString(), password.getText().toString()));
                }
            }
        });

        Intent intent = getIntent();
        externalToken = intent.getStringExtra(EXTRA_EXTERNAL_TOKEN);
        externalProvider = intent.getStringExtra(EXTRA_EXTERNAL_PROVIDER);
        isExternalLogin = externalToken != null;

        if (isExternalLogin)
        {
            password.setVisibility(View.GONE);
            userName.setText(intent.getStringExtra(EXTRA_EXTERNAL_USERNAME));
        }
    }

    @Subscribe
    public void registerResponse(Account.RegisterResponse response)
    {
        onUserResponse(response);
    }

    @Subscribe
    public void externalRegisterResponse(Account.RegisterWithExternalTokenResponse response)
    {
        onUserResponse(response);
    }

    private void onUserResponse(Account.UserResponse response)
    {
        if (response.succeeded())
        {
            setResult(RESULT_OK);
            finish();
            return;
        }

        response.showErrorToast(this);
        userName.setError(response.getPropertyError("userName"));
        password.setError(response.getPropertyError("password"));
        email.setError(response.getPropertyError("email"));

        registerButton.setEnabled(true);
        userName.setEnabled(true);
        password.setEnabled(true);
        email.setEnabled(true);

        progressBar.setVisibility(View.GONE);

        registerButton.setText(defaultRegisterButtonText);

    }
}
