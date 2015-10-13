package com.example.torre.yora.views;


import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.BaseActivity;

import java.util.ArrayList;

public class NavDrawer
{
    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;

    //Passed as a context in the constructor so we can then call activity.findViewById() and other things.
    protected BaseActivity activity;
    protected DrawerLayout drawerLayout;
    //Container of the NavDrawerItems (where we want to inflate our views)
    protected ViewGroup navDrawerView;

    public NavDrawer(BaseActivity activity)
    {
        this.activity = activity;

        items = new ArrayList<>();

        //R.id.drawer_layout is the ID of the <DrawerLayout> tag that every activity has. This ID is the same across all activity layout files.
        drawerLayout = (DrawerLayout)activity.findViewById(R.id.drawer_layout);

        //R.id.nav_drawer is the ID of the layout that specifies how the nav drawer will actually look (LinearLayout, ImageView at the top left, TextView, another LinearLayout for the items, etc).
        navDrawerView = (ViewGroup) activity.findViewById(R.id.nav_drawer);

        if (drawerLayout == null || navDrawerView == null)
        {
            throw new RuntimeException("Must have defined views witht he IDs: drawer_layout and nav_drawer");
        }

        Toolbar toolbar = activity.getToolbar();

        toolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setOpen(!isOpen());
            }
        });

        activity.getYoraApplication().getBus().register(this);

    }

    public void addItem(NavDrawerItem item)
    {
        items.add(item);
        //We're accessing NavDrawerItem's protected navDrawer, and assigning it to our NavDrawer class.
        item.navDrawer = this;
    }

    public boolean isOpen()
    {
        return drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    public void setOpen(boolean isOpen)
    {
        if (isOpen)
        {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void setSelectedItem(NavDrawerItem item)
    {
        //If we had a previously selected item, than de-select it first
        if (selectedItem != null)
        {
            selectedItem.setSelected(false);
        }

        selectedItem = item;
        selectedItem.setSelected(true);
    }

    //Responsible for inflating all the NavDrawerItems
    public void create()
    {
        LayoutInflater inflater = activity.getLayoutInflater();

        for (NavDrawerItem item : items)
        {
            item.inflate(inflater, navDrawerView);
        }
    }

    public void destroy()
    {
        activity.getYoraApplication().getBus().unregister(this);
    }

    public static abstract class NavDrawerItem
    {
        protected NavDrawer navDrawer;

        public abstract void inflate(LayoutInflater inflater, ViewGroup navDrawerView);
        public abstract void setSelected(boolean isSelected);
    }

    //BasicNavDrawerItem is a NavDrawerItem that shows text, potentially a badge, and an icon. Does not start a new Activity.
    public static class BasicNavDrawerItem extends NavDrawerItem implements View.OnClickListener
    {
        private String text;
        private String badge;
        private int iconDrawable;
        //The ViewGroup that we want to stuff this inflated item to
        private int containerId;

        private ImageView icon;
        private TextView textView;
        private TextView badgeTextview;
        //Holds a reference to the inflated View.
        private View view;
        private int defaultTextColor; //When this item is not selected it shows the default color, when it is selected it shows another color

        public BasicNavDrawerItem(String text, String badge, int iconDrawable, int containerId)
        {
            this.text = text;
            this.badge = badge;
            this.iconDrawable = iconDrawable;
            this.containerId = containerId;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView)
        {
            ViewGroup container = (ViewGroup)navDrawerView.findViewById(containerId);

            if (container == null)
            {
                throw new RuntimeException("Container not found. Couldn't inflate view.");
            }

            //list_item_nav_drawer is a layout that specifies how each list item will look (image, text, badge).
            view = inflater.inflate(R.layout.list_item_nav_drawer, container, false);

            container.addView(view);

            //Each element of the previously defined layout. Each part that makes up a list item.
            icon = (ImageView) view.findViewById(R.id.list_item_nav_drawer_icon);
            textView = (TextView) view.findViewById(R.id.list_item_nav_drawer_text);
            badgeTextview = (TextView) view.findViewById(R.id.list_item_nav_drawer_badge);
            defaultTextColor = textView.getCurrentTextColor();


            icon.setImageResource(iconDrawable);
            textView.setText(text);

            //The badge is optional, if we have it then set the text. If not, make it invisible.
            if (badge != null)
            {
                badgeTextview.setText(badge);
            }
            else
            {
                badgeTextview.setVisibility(View.GONE);
            }

            view.setOnClickListener(this);
        }

        @Override
        public void setSelected(boolean isSelected)
        {
            if (isSelected)
            {
                //Sets the background of the selected item, to the one specified by R.drawable.list_item_nav_drawer_selected_item_background.
                view.setBackgroundResource(R.drawable.list_item_nav_drawer_selected_item_background);

                textView.setTextColor(navDrawer.activity.getResources().getColor(R.color.list_item_nav_drawer_selected_item_textColor));
            }
            else
            {
                //Sets the background back to transparent when we de-select this item
                view.setBackground(null);
                textView.setTextColor(defaultTextColor);
            }
        }

        public void setText(String text)
        {
            this.text = text;

            //If this method is invoked after we have inflated our view, apart from updating the text, we also need to update the TextView.
            if (view != null)
            {
                textView.setText(text);
            }
        }

        public void setBadge(String badge)
        {
            this.badge = badge;

            if (view != null)
            {
                if (badge != null)
                {
                    badgeTextview.setVisibility(View.VISIBLE);
                }
                else
                {
                    badgeTextview.setVisibility(View.GONE);
                }
            }
        }

        public void setIcon(int iconDrawable)
        {
            this.iconDrawable = iconDrawable;

            if (view != null)
            {
                icon.setImageResource(iconDrawable);
            }
        }

        @Override
        public void onClick(View v)
        {
            navDrawer.setSelectedItem(this);


        }
    }

    /**
     * ActivityNavDrawerItems are BasicNavDrawerItems that start new Activities.
     */
    public static class ActivityNavDrawerItem extends BasicNavDrawerItem
    {
        //Activity to launch when an ActivityNavDrawer is clicked
        private final Class targetActivity;


        public ActivityNavDrawerItem(Class targetActivity, String text, String badge, int iconDrawable, int containerId)
        {
            super(text, badge, iconDrawable, containerId);
            this.targetActivity = targetActivity;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup navDrawerView)
        {
            super.inflate(inflater, navDrawerView);

            //If we're already in the Activity that this item represents, then mark it as selected.
            //For example, when we launch the app, the MainActivity is set to selected since it is the one that is launched first.
            if (this.navDrawer.activity.getClass() == targetActivity)
            {
                this.navDrawer.setSelectedItem(this);
            }
        }

        @Override
        public void onClick(View view)
        {
            //Close the NavDrawer every time we click one of its items
            navDrawer.setOpen(false);

            //If we clicked the Activity that we're already in, then don't launch it again.
            if (navDrawer.activity.getClass() == targetActivity)
            {
                return;
            }

            super.onClick(view);

            //TODO: animations
            navDrawer.activity.startActivity(new Intent(navDrawer.activity, targetActivity));
            navDrawer.activity.finish();
        }
    }
}
