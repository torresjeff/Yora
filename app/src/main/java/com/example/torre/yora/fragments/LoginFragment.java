package com.example.torre.yora.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Account;
import com.squareup.otto.Subscribe;

public class LoginFragment extends BaseFragment
{
    private Button loginButton;

    //Interface that calls onLoggedIn when the user is logged in.
    private LoginCallback loginCallback;
    private View progressBar;
    private EditText usernameText;
    private EditText passwordText;
    private String defaultLoginButtonText;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        //We cast the Activity that this Fragment is living in
        //so we can call the onLoggedIn method on it.
        loginCallback = (LoginCallback)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button)v.findViewById(R.id.fragment_login_login);
        progressBar = v.findViewById(R.id.fragment_login_progressBar);
        usernameText = (EditText) v.findViewById(R.id.fragment_login_username);
        passwordText = (EditText) v.findViewById(R.id.fragment_login_password);

        defaultLoginButtonText = loginButton.getText().toString();

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                progressBar.setVisibility(View.VISIBLE);
                loginButton.setText("");
                loginButton.setEnabled(false);
                usernameText.setEnabled(false);
                passwordText.setEnabled(false);
                bus.post(new Account.LoginWithUsernameRequest(usernameText.getText().toString(), passwordText.getText().toString()));
            }
        });

        return v;
    }

    @Subscribe
    public void onLoginWithUsername(Account.LoginWithUsernameResponse response)
    {
        response.showErrorToast(getActivity());

        if (response.succeeded())
        {
            loginCallback.onLoggedIn();
            return;
        }

        loginButton.setEnabled(true);

        usernameText.setError(response.getPropertyError("userName"));
        usernameText.setEnabled(true);

        passwordText.setError(response.getPropertyError("password"));
        passwordText.setEnabled(true);

        progressBar.setVisibility(View.GONE);
        loginButton.setText(defaultLoginButtonText);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        //If we don't do this it can result in a memory leak (we keep a reference to an Activity that no longer exists)
        loginCallback = null;
    }

    public interface LoginCallback
    {
        void onLoggedIn();
    }
}
