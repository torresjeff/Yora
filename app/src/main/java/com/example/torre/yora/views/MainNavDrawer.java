package com.example.torre.yora.views;

import android.view.View;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.activities.MainActivity;

/**
 * MainNavDrawer is a concrete implementation of a NavDrawer. In its constructor we add all the items our NavDrawer is going to use.
 */
public class MainNavDrawer extends NavDrawer
{

    //Add all items (NavDrawerItems) of the NavDrawer here
    public MainNavDrawer(final BaseActivity activity)
    {
        super(activity);

        //TODO: replace third parameter (null) with an actual badge text, replace R.drawable.abc_btn_check_to_on_mtrl_015 with actual icon to be used, replace 0 with container to be used
        addItem(new ActivityNavDrawerItem(MainActivity.class, "Timeline", "30", R.drawable.ic_mail_black_24dp, R.id.include_main_nav_drawer_topItemsContainer));

        //TODO: replace null with actual badge text, replace R.drawable.abc_btn_check_to_on_mtrl_015 with actual icon to be used, replace 0 with container to be used.
        //Items that don't start Activities should override onClick() so that they don't stay selected eternally.
        addItem(new BasicNavDrawerItem("Logout", null, R.drawable.ic_close_black_24dp, R.id.include_main_nav_drawer_bottomItemsContainer)
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(activity, "You logged out!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
