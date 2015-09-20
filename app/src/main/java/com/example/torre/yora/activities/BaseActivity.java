package com.example.torre.yora.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.torre.yora.R;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.example.torre.yora.views.NavDrawer;


public class BaseActivity extends AppCompatActivity
{
    protected YoraApplication application;
    protected Toolbar toolbar;
    protected NavDrawer navDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        application = (YoraApplication)getApplication();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId)
    {
        super.setContentView(layoutResId);

        toolbar = (Toolbar)findViewById(R.id.include_toolbar);

        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
        }
    }

    protected void setNavDrawer(NavDrawer navDrawer)
    {
        this.navDrawer = navDrawer;
        this.navDrawer.create();
    }
}
