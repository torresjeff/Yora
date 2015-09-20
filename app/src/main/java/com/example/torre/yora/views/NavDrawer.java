package com.example.torre.yora.views;


import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.torre.yora.activities.BaseActivity;

import java.util.ArrayList;

public class NavDrawer
{
    private ArrayList<NavDrawerItem> items;
    private NavDrawerItem selectedItem;

    protected BaseActivity activity; //Activity that is holding the NavDrawer
    protected DrawerLayout drawerLayout;
    protected ViewGroup navDrawerView; //How the NavDrawer will look

    public NavDrawer(BaseActivity activity)
    {}

    public void addItem(NavDrawerItem item)
    {}

    public boolean isOpen()
    {
        //TODO: change later
        return false;
    }

    public void setOpen(boolean isOpen)
    {}

    public void setSelectedItem(NavDrawerItem item)
    {}

    //Responsible for inflating all the NavDrawerItems
    public void create()
    {}

    public static abstract class NavDrawerItem
    {
        protected NavDrawer navDrawer;

        public abstract void inflate(LayoutInflater inflater, ViewGroup container);
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
        private View view;
        private int defaultColor; //When this item is not selected it shows the default color, when it is selected it shows another color

        public BasicNavDrawerItem(String text, String badge, int iconDrawable, int containerId)
        {
            /*this.text = text;
            this.badge = badge;
            this.iconDrawable = iconDrawable;
            this.containerId = containerId;*/
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public void setBadge(String badge)
        {
            this.badge = badge;
        }

        public void setIcon(int iconDrawable)
        {
            this.iconDrawable = iconDrawable;
        }

        @Override
        public void inflate(LayoutInflater inflater, ViewGroup container)
        {

        }

        @Override
        public void setSelected(boolean isSelected)
        {

        }

        @Override
        public void onClick(View v)
        {

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
    }

}
