package com.example.torre.yora.receivers;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.torre.yora.R;
import com.example.torre.yora.activities.MainActivity;
import com.example.torre.yora.activities.MessageActivity;
import com.example.torre.yora.infrastructure.Auth;
import com.example.torre.yora.infrastructure.YoraApplication;
import com.example.torre.yora.services.Events;
import com.squareup.otto.Bus;

public class NotificationReceiver extends BroadcastReceiver
{
    private static final String TAG = "NotificationReceiver";

    private Auth auth;
    private YoraApplication application;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        application = (YoraApplication) context.getApplicationContext();
        auth = application.getAuth();
        Bus bus = application.getBus();

        try
        {
            int operation = Integer.parseInt(intent.getStringExtra("operation")); //This is data that the server is sending. It is serialized in JSON format.
            int type = Integer.parseInt(intent.getStringExtra("type"));
            int entityId = Integer.parseInt(intent.getStringExtra("entityId"));
            int entityOwnerId = Integer.parseInt(intent.getStringExtra("entityOwnerId"));
            String entityOwnerName = intent.getStringExtra("entityOwnerName");

            Events.OnNotificationReceivedEvent event = new Events.OnNotificationReceivedEvent(operation, type, entityId, entityOwnerId, entityOwnerName);

            if (type == Events.ENTITIY_CONTACT)
            {
                sendContactNotification(event);
            }
            else if (type == Events.ENTITY_CONTACT_REQUEST)
            {
                sendContactRequestNotification(event);
            }
            else if (type == Events.ENTITY_MESSAGE)
            {
                sendMessageNotification(event);
            }

            bus.post(event);
        }
        catch (NumberFormatException e)
        {
            Log.e(TAG, "Error parsing message", e);
        }

        setResultCode(Activity.RESULT_OK); //Notify the OS that we successfully handled the push notification
    }

    private void sendMessageNotification(Events.OnNotificationReceivedEvent event)
    {
        if (event.operationType == Events.OPERATION_DELETED ||
                event.entityOwnerId == auth.getUser().getId()) //We don't want to show a notification if it is a contact request that we sent, or when we delete a contact request
        {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application)
                .setSmallIcon(R.mipmap.ic_launcher)//set the icon to our application icon
                .setContentTitle(event.entityOwnerName + " sent you a message on Yora!")
                .setContentText("Click here to view it");

        Intent intent = new Intent(application, MessageActivity.class);
        intent.putExtra(MessageActivity.EXTRA_MESSAGE_ID, event.entityId); //Causes the MessageActivity to launch when we click this notification (shows the message immediately)
        sendNotification(event.entityId, builder, intent);
    }

    private void sendContactRequestNotification(Events.OnNotificationReceivedEvent event)
    {
        if (event.operationType == Events.OPERATION_DELETED ||
                event.entityOwnerId == auth.getUser().getId()) //We don't want to show a notification if it is a contact request that we sent, or when we delete a contact request
        {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(application)
                .setSmallIcon(R.mipmap.ic_launcher)//set the icon to our application icon
                .setContentTitle(event.entityOwnerName + " sent you a contact request on Yora!")
                .setContentText("Click here to view it");

        sendNotification(event.entityId, builder, new Intent(application, MainActivity.class)); //Make it so that when we click on this notification it launches the main activity
    }

    private void sendContactNotification(Events.OnNotificationReceivedEvent event)
    {

    }

    private void sendNotification(int entityId, NotificationCompat.Builder builder, Intent intent)
    {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(application);
        stackBuilder.addParentStack(MessageActivity.class);
        stackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL; //Makes the notification go away when the user clicks on it

        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(entityId, notification);
    }


}
