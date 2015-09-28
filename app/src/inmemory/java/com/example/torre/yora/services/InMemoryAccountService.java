package com.example.torre.yora.services;

import com.example.torre.yora.infrastructure.YoraApplication;
import com.squareup.otto.Subscribe;

public class InMemoryAccountService
{
    private YoraApplication application;

    public InMemoryAccountService(YoraApplication application)
    {
        this.application = application;

        application.getBus().register(this);
    }

    //Subscribe to Request events

    @Subscribe
    public void updateProfile(Account.UpdateProfileRequest request)
    {
        Account.UpdateProfileResponse response = new Account.UpdateProfileResponse();
        //We don't want to simulate errors so don't put anything else in the response. If we wanted to simulate errors then we would se values on the response error fields.
        //response.setOperationError("Simulated error");

        application.getBus().post(response);
    }
}
