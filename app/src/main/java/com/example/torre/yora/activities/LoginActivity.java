package com.example.torre.yora.activities;

import com.example.torre.yora.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class LoginActivity extends BaseActivity
{
    private View loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.activity_login_login);

        //When we're in tablet mode, this button won't appear. We can know in which mode we're in by finding the buttons.
        if (loginButton != null) //if we're not in tablet mode
        {
            loginButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    application.getAuth().getUser().setIsLoggedIn(true);
                    startActivity(new Intent(getApplicationContext(), LoginNarrowActivity.class));
                }
            });
        }

    }
}
