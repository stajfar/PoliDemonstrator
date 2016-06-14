package it.polimi.polidemonstrator.businesslogic.businessrules;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import it.polimi.polidemonstrator.MainActivity;

/**
 * Created by saeed on 6/13/2016.
 */
public class NotificationDispatcher implements ActionDispatcher {

    @Override
    public void fire(Context context, String myMessage) {
        //here you you implement what to do when Action is triggered (Send a notification to user)
        //other action classes are required

        
        Class<MainActivity> activityClass= MainActivity.class;
        Intent notifyIntent = new Intent(context,activityClass);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new android.app.Notification.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(myMessage)
                .setContentText("message recieved!!!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
