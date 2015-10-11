package com.example.torre.yora.services;

import com.example.torre.yora.infrastructure.Auth;
import com.example.torre.yora.infrastructure.User;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.services.entities.UserDetails;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

public class InMemoryAccountService extends BaseInMemoryService
{

    public InMemoryAccountService(YoraApplication application)
    {
        super(application);
    }

    //Subscribe to Request events
    @Subscribe
    public void updateProfile(final Account.UpdateProfileRequest request)
    {
        final Account.UpdateProfileResponse response = new Account.UpdateProfileResponse();
        //We don't want to simulate errors so don't put anything else in the response. If we wanted to simulate errors then we would se values on the response error fields.
        //response.setOperationError("Simulated error");

        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                User user = application.getAuth().getUser();
                user.setDisplayName(request.displayName);
                user.setEmail(request.email);

                bus.post(response);
                bus.post(new Account.UserDetailsUpdatedEvent(user));
            }
        }, 2000, 3000);
    }

    @Subscribe
    public void updateAvatar(final Account.ChangeAvatarRequest request)
    {
        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                User user = application.getAuth().getUser();
                user.setAvatarUrl(request.newAvatarUri.toString());

                bus.post(new Account.ChangeAvatarResponse());
                bus.post(new Account.UserDetailsUpdatedEvent(user));
            }
        }, 4000, 5000);
    }

    @Subscribe
    public void changePassword(Account.ChangePasswordRequest request)
    {
        Account.ChangePasswordResponse response = new Account.ChangePasswordResponse();

        if (!request.newPassword.equals(request.confirmNewPassword))
        {
            response.setPropertyError("confirmNewPassword", "Passwords must match");
        }

        if (request.newPassword.length() < 3)
        {
            response.setPropertyError("newPassword", "Password must be longer than 3 characters");
        }

        postDelayed(response);
    }

    @Subscribe
    public void loginWithUsername(Account.LoginWithUsernameRequest request)
    {
        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Account.LoginWithUsernameResponse response = new Account.LoginWithUsernameResponse();
                loginUser(new Account.UserResponse());
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void updateGcmRegistration(Account.UpdateGcmRegistrationRequest request)
    {
        postDelayed(new Account.UpdateGcmRegistrationResponse());
    }

    @Subscribe
    public void loginWithExternalToken(Account.LoginWithExternalTokenRequest request)
    {
        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Account.LoginWithExternalTokenResponse response = new Account.LoginWithExternalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void register(Account.RegisterRequest request)
    {
        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Account.RegisterResponse response = new Account.RegisterResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void externalRegister(Account.RegisterWithExternalTokenRequest request)
    {
        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Account.RegisterResponse response = new Account.RegisterResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    @Subscribe
    public void loginWithLocalToken(Account.LoginWithLocalTokenRequest request)
    {
        invokeDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Account.LoginWithLocalTokenResponse response = new Account.LoginWithLocalTokenResponse();
                loginUser(response);
                bus.post(response);
            }
        }, 1000, 2000);
    }

    private void loginUser(Account.UserResponse response)
    {
        Auth auth = application.getAuth();
        User user = auth.getUser();

        user.setDisplayName("Jeffrey Torres");
        user.setUserName("torres.jeffrey");
        user.setEmail("torres.jeffrey@javeriana.edu.co");
        user.setAvatarUrl("http://www.gravatar.com/avatar/1?d=identicon");
        user.setId(123);
        user.setIsLoggedIn(true);

        bus.post(new Account.UserDetailsUpdatedEvent(user));

        auth.setAuthToken("fakeauthtoken");

        response.displayName = user.getDisplayName();
        response.userName = user.getUserName();
        response.email = user.getEmail();
        response.avatarUrl = user.getAvatarUrl();
        response.id = user.getId();
        response.authToken = auth.getAuthToken();
    }

    @Subscribe
    public void markMessageAsRead(Messages.MarkMessageAsReadRequest request)
    {
        postDelayed(new Messages.MarkMessageAsReadResponse());
    }

    @Subscribe
    public void getMessageDetails(Messages.GetMessageDetailsRequest request)
    {
        Messages.GetMessageDetailsResponse response = new Messages.GetMessageDetailsResponse();
        response.message = new Message(1, Calendar.getInstance(), "Short message", "Long message", null, new UserDetails(1, true, "Display name", "Username", ""), false, false);

        postDelayed(response);
    }
}
