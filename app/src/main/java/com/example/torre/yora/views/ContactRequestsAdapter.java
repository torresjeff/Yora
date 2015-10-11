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
import com.example.torre.yora.services.entities.ContactRequest;
import com.squareup.picasso.Picasso;

public class ContactRequestsAdapter extends ArrayAdapter<ContactRequest>
{
    private LayoutInflater inflater;

    public ContactRequestsAdapter(BaseActivity activity)
    {
        super(activity, 0); //0 = we want to handle our layout inflation all by ourselves. So we pass an invalid layout resource.
        inflater = activity.getLayoutInflater();
    }

    //Returns an instantiated view that will be used for each individual row in the listView
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ContactRequest request = getItem(position);
        ViewHolder view;

        //We're not recycling a view, we have to instantiate a new item
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item_contact_request, parent, false);
            view = new ViewHolder(convertView);
            convertView.setTag(view);
        }
        else //recycle the view
        {
            view = (ViewHolder) convertView.getTag();
        }

        view.displayName.setText(request.getUser().getUserName());
        Picasso.with(getContext()).load(request.getUser().getAvatarUrl()).into(view.avatar);

        String createdAt = DateUtils.formatDateTime(getContext(), request.getCreatedAt().getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);

        if (request.isFromUs())
        {
            view.createdAt.setText("Sent at " + createdAt);
        }
        else
        {
            view.createdAt.setText("Received at " + createdAt);
        }

        return convertView;
    }

    private class ViewHolder
    {
        public TextView displayName;
        public TextView createdAt;
        public ImageView avatar;

        public ViewHolder(View view)
        {
            displayName = (TextView) view.findViewById(R.id.list_item_contact_request_displayName);
            createdAt = (TextView) view.findViewById(R.id.list_item_contact_request_createdAt);
            avatar = (ImageView) view.findViewById(R.id.list_item_contact_request_avatar);
        }
    }
}
