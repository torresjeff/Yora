package com.example.torre.yora.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.torre.yora.R;

public class RegisterActivity extends BaseActivity
{
    private EditText userName;
    private EditText email;
    private EditText password;
    private Button registerButton;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = (EditText)findViewById(R.id.activity_register_username);
        email = (EditText)findViewById(R.id.activity_register_email);
        password= (EditText)findViewById(R.id.activity_register_password);
        progressBar = findViewById(R.id.activity_register_progressBar);
        registerButton = (Button)findViewById(R.id.activity_register_registerButton);

        progressBar.setVisibility(View.GONE); //Invisible + doesn't get put into any view calculations -- practically doesn't exist

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //TODO: implement logic
                application.getAuth().getUser().setIsLoggedIn(true);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
