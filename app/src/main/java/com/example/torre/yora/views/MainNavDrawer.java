package com.example.torre.yora.views;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.activities.ContactsActivity;
import com.example.torre.yora.activities.MainActivity;
import com.example.torre.yora.activities.ProfileActivity;
import com.example.torre.yora.activities.SentMessagesActivity;
import com.example.torre.yora.infrastructure.User;

/**
 * MainNavDrawer is a concrete implementation of a NavDrawer. In its constructor we add all the items our NavDrawer is going to use.
 */
public class MainNavDrawer extends NavDrawer
{
    //The name of the user inside the nav drawer
    private final TextView displayName;
    //Avatar of the user inside the nav drawer
    private final ImageView avatar;

    //Add all items (NavDrawerItems) of the NavDrawer here
    public MainNavDrawer(final BaseActivity activity)
    {
        super(activity);

        addItem(new ActivityNavDrawerItem(MainActivity.class, "Timeline", null, R.drawable.ic_chat_bubble_outline_black_36dp, R.id.include_main_nav_drawer_topItemsContainer));
        addItem(new ActivityNavDrawerItem(SentMessagesActivity.class, "Sent messages", null, R.drawable.ic_mail_black_24dp, R.id.include_main_nav_drawer_topItemsContainer));
        addItem(new ActivityNavDrawerItem(ContactsActivity.class, "Contacts", null, R.drawable.ic_group_black_36dp, R.id.include_main_nav_drawer_topItemsContainer));
        addItem(new ActivityNavDrawerItem(ProfileActivity.class, "Profile", null, R.drawable.ic_person_pin_black_36dp, R.id.include_main_nav_drawer_topItemsContainer));

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

        displayName = (TextView) activity.findViewById(R.id.include_main_nav_drawer_displayName);
        avatar = (ImageView) activity.findViewById(R.id.include_main_nav_drawer_avatar);

        User loggedInUser = activity.getYoraApplication().getAuth().getUser();

        displayName.setText(loggedInUser.getDisplayName());

        //TODO: change avatar image to avatar URL from loggedInUser
    }
}
