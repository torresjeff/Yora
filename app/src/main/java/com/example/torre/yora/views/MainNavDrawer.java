package com.example.torre.yora.views;

import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.activities.MainActivity;

/**
 * MainNavDrawer is a concrete implmentation of a NavDrawer. In its constructor we add all the items our NavDrawer is going to use.
 */
public class MainNavDrawer extends NavDrawer
{

    //Add all items (NavDrawerItems) of the NavDrawer here
    public MainNavDrawer(BaseActivity activity)
    {
        super(activity);

        //TODO: replace third parameter (null) with an actual badge text, replace android.R.drawable.sym_action_email with actual icon to be used, replace 0 with container to be used
        addItem(new ActivityNavDrawerItem(MainActivity.class, "Timeline", null, android.R.drawable.sym_action_email, 0));

        //TODO: replace null with actual badge text, replace android.R.drawable.sym_action_chat with actual icon to be used, replace 0 with container to be used.
        addItem(new BasicNavDrawerItem("Logout", null, android.R.drawable.sym_action_chat, 0));

    }
}
