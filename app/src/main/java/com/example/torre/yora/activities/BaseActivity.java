package com.example.torre.yora.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;

import com.example.torre.yora.R;
import com.example.torre.yora.infrastructure.ActionScheduler;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.example.torre.yora.views.NavDrawer;
import com.squareup.otto.Bus;


public abstract class BaseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener
{
    private boolean isRegisteredWithBus;
    protected YoraApplication application;
    protected Toolbar toolbar;
    protected NavDrawer navDrawer;
    protected boolean isTablet;
    protected Bus bus;
    protected SwipeRefreshLayout swipeRefresh;


    protected ActionScheduler scheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        application = (YoraApplication)getApplication();
        bus = application.getBus();
        scheduler = new ActionScheduler(application);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        isTablet = (metrics.widthPixels/metrics.density) >= 600; //if this division is greater than 600, then we're in a tablet

        bus.register(this);
        isRegisteredWithBus = true;
    }

    @Override
    public void finish()
    {
        super.finish();

        if (isRegisteredWithBus)
        {
            bus.unregister(this);
            isRegisteredWithBus = false;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        scheduler.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        scheduler.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if (isRegisteredWithBus)
        {
            bus.unregister(this);
            isRegisteredWithBus = false;
        }


        if (navDrawer != null)
        {
            navDrawer.destroy();
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResId)
    {
        super.setContentView(layoutResId);

        toolbar = (Toolbar)findViewById(R.id.include_toolbar);

        //If we defined a toolbar, then set it as our action bar
        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
        }

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        if (swipeRefresh != null)
        {
            swipeRefresh.setOnRefreshListener(this);
            swipeRefresh.setColorSchemeColors(
                    Color.parseColor("#ff00ddff"),
                    Color.parseColor("#ff99cc00"),
                    Color.parseColor("#ffffbb33"),
                    Color.parseColor("#ffff4444"));
        }
    }

    @Override
    public void onRefresh()
    {

    }

    protected void setNavDrawer(NavDrawer navDrawer)
    {
        this.navDrawer = navDrawer;
        this.navDrawer.create();
    }

    public Toolbar getToolbar()
    {
        return toolbar;
    }

    public YoraApplication getYoraApplication()
    {
        return application;
    }

    public ActionScheduler getScheduler()
    {
        return scheduler;
    }
}
