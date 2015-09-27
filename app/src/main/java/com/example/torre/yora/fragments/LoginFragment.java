package com.example.torre.yora.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.torre.yora.R;

public class LoginFragment extends BaseFragment
{
    private Button loginButton;

    //Interface that calls onLoggedIn when the user is logged in.
    private LoginCallback loginCallback;

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
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                application.getAuth().getUser().setIsLoggedIn(true);
                //Dummy display name
                application.getAuth().getUser().setDisplayName("torres.jeffrey");
                loginCallback.onLoggedIn();
            }
        });

        return v;
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
