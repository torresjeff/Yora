package com.example.torre.yora.views;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.BaseActivity;
import com.example.torre.yora.services.entities.UserDetails;
import com.squareup.picasso.Picasso;


public class UserDetailsAdapter extends ArrayAdapter<UserDetails>
{
    private LayoutInflater inflater;

    public UserDetailsAdapter(BaseActivity activity)
    {
        super(activity, 0);
        inflater = activity.getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        UserDetails details = getItem(position);
        ViewHolder view;

        //We're not recycling a view, we have to instantiate a new item
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_user_details, parent, false);
            view = new ViewHolder(convertView);
            convertView.setTag(view);
        }
        else //recycle the view
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.displayName.setText(details.getUserName());
        Picasso.with(getContext()).load(details.getAvatarUrl()).into(view.avatar);


        return convertView;
    }

    private class ViewHolder
    {
        public TextView displayName;
        public ImageView avatar;

        public ViewHolder(View view)
        {
            displayName = (TextView) view.findViewById(R.id.list_item_user_details_displayName);
            avatar = (ImageView) view.findViewById(R.id.list_item_user_details_avatar);
        }
    }
}
