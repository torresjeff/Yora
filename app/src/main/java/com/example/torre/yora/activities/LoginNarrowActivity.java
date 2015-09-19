package com.example.torre.yora.activities;

import android.os.Bundle;
import com.example.torre.yora.R;

import com.example.torre.yora.fragments.LoginFragment;

public class LoginNarrowActivity extends BaseActivity implements LoginFragment.LoginCallback
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_narrow);
    }

    @Override
    public void onLoggedIn()
    {
        setResult(RESULT_OK);
        finish();
    }

}
