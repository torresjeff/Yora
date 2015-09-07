package fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.MainActivity;

public class LoginFragment extends BaseFragment
{
    private Button loginButton;

    private LoginCallback loginCallback;

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        loginCallback = (LoginCallback)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = (Button)v.findViewById(R.id.fragment_login_login);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                application.getAuth().getUser().setIsLoggedIn(true);
                loginCallback.onLoggedIn();
            }
        });

        return v;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        loginCallback = null;
    }

    public interface LoginCallback
    {
        void onLoggedIn();
    }
}
