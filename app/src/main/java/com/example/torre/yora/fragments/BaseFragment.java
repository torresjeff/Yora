package com.example.torre.yora.fragments;


import android.app.Fragment;
import android.os.Bundle;

import com.example.torre.yora.infrastructure.ActionScheduler;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.squareup.otto.Bus;

public abstract class BaseFragment extends Fragment
{
    protected YoraApplication application;
    protected Bus bus;
    protected ActionScheduler scheduler;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        application = (YoraApplication)getActivity().getApplication();
        bus = application.getBus();
        scheduler = new ActionScheduler(application);

        bus.register(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        scheduler.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        scheduler.onResume();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        bus.unregister(this);
    }
}
