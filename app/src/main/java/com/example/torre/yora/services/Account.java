package com.example.torre.yora.services;


import android.net.Uri;

import com.example.torre.yora.infrastructure.User;

public final class Account
{
    private Account()
    {
    }

    //All the information we need when the user logs in. The server responds with this info.
    public static class UserResponse extends ServiceResponse
    {
        public int id;
        public String avatarUrl;
        public String displayName;
        public String userName;
        public String email;
        public String authToken;
        public boolean hasPassword;
    }

    public static class LoginWithUsernameRequest
    {
        public String username;
        public String password;

        public LoginWithUsernameRequest(String username, String password)
        {
            this.username = username;
            this.password = password;
        }
    }

    public static class LoginWithUsernameResponse extends ServiceResponse
    {
    }

    //This is useful when the user turns off the phone. When he turns it back on and enters the app, the local token is sent to the server to see if it's still active.
    //If it is then respond with the information of the user. If it's not active then the user must log in again.
    public static class LoginWithLocalTokenRequest
    {
        public String authToken;
        public String clientId;

        public LoginWithLocalTokenRequest(String authToken)
        {
            this.authToken = authToken;
            clientId = "android";
        }
    }

    public static class LoginWithLocalTokenResponse extends UserResponse
    {
    }

    public static class RegisterRequest
    {
        public String username;
        public String email;
        public String password;
        public String clientId;

        public RegisterRequest(String username, String email, String password)
        {
            this.username = username;
            this.email = email;
            this.password = password;
            clientId = "android";
        }
    }

    public static class RegisterResponse extends UserResponse
    {
    }

    public static class RegisterWithExternalTokenRequest
    {
        public String username;
        public String email;
        public String provider;
        public String token;
        public String clientId;

        public RegisterWithExternalTokenRequest(String username, String email, String provider, String token)
        {
            this.username = username;
            this.email = email;
            this.provider = provider;
            this.token = token;
            clientId = "android";
        }
    }

    public static class RegisterWithExternalTokenResponse extends UserResponse
    {
    }

    public static class LoginWithExternalTokenRequest
    {
        public String provider;
        public String token;
        public String clientId;

        public LoginWithExternalTokenRequest(String provider, String token, String clientId)
        {
            this.provider = provider;
            this.token = token;
            this.clientId = clientId;
        }
    }

    public static class LoginWithExternalTokenResponse extends UserResponse
    {
    }

    public static class ChangeAvatarRequest
    {
        public Uri newAvatarUri;

        public ChangeAvatarRequest(Uri newAvatarUri)
        {
            this.newAvatarUri = newAvatarUri;
        }
    }

    public static class ChangeAvatarResponse extends ServiceResponse
    {
        public String avatarUrl;
    }

    public static class UpdateProfileRequest
    {
        public String displayName;
        public String email;

        public UpdateProfileRequest(String displayName, String email)
        {
            this.displayName = displayName;
            this.email = email;
        }
    }

    public static class UpdateProfileResponse extends  ServiceResponse
    {
        public String displayName;
        public String email;
    }

    public static class ChangePasswordRequest
    {
        public String currentPassword;
        public String newPassword;
        public String confirmNewPassword;

        public ChangePasswordRequest(String currentPassword, String newPassword, String confirmNewPassword)
        {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
            this.confirmNewPassword = confirmNewPassword;
        }
    }

    public static class ChangePasswordResponse extends ServiceResponse
    {
    }

    public static class UserDetailsUpdatedEvent
    {
        public User user;

        public UserDetailsUpdatedEvent(User user)
        {
            this.user = user;
        }
    }

    //GCM is Google's push notification API.
    //The device has to register with google play services, and then tell out server what the registration ID, so our server can send a push notification.
    public static class UpdateGcmRegistrationRequest
    {
        public String registrationId;

        public UpdateGcmRegistrationRequest(String registrationId)
        {
            this.registrationId = registrationId;
        }
    }

    public static class UpdateGcmRegistrationResponse extends ServiceResponse
    {
    }
}
