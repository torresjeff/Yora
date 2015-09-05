package com.example.torre.yora.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.torre.yora.R;

public class LoginNarrowActivity extends BaseActivity
{
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_narrow);

        /*findViewById(R.id.activity_login_narrow_login).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });*/
    }

}
