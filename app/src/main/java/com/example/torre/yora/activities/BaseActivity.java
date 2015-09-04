package com.example.torre.yora.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.torre.yora.infrastructure.YoraApplication;


public class BaseActivity extends AppCompatActivity
{
    protected YoraApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        application = (YoraApplication)getApplication();
    }
}
