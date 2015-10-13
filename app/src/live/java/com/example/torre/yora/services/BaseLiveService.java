package com.example.torre.yora.services;

import com.example.torre.yora.infrastructure.YoraApplication;
import com.squareup.otto.Bus;

/**
 * Created by Jeffrey Torres on 12/10/2015.
 */
public abstract class BaseLiveService
{
    protected final Bus bus;
    protected final YoraWebService api;
    protected final YoraApplication application;

    protected BaseLiveService(YoraWebService api, YoraApplication application)
    {
        this.bus = application.getBus();
        this.api = api;
        this.application = application;
        bus.register(this);
    }
}
