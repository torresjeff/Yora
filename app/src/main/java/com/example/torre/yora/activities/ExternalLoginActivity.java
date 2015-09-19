package com.example.torre.yora.activities;


import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import com.example.torre.yora.R;

public class ExternalLoginActivity extends BaseActivity
{
    public static final String EXTRA_EXTERNAL_SERVICE = "EXTRA_EXTERNAL_SERVICE";
    private Button testButton;
    private WebView webView;


    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_external_login);

        testButton = (Button) findViewById(R.id.activity_external_login_testButton);
        webView = (WebView)findViewById(R.id.activity_external_login_webView);


        testButton.setText("Login with " + getIntent().getStringExtra("EXTRA_EXTERNAL_SERVICE"));
        testButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                application.getAuth().getUser().setIsLoggedIn(true);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
