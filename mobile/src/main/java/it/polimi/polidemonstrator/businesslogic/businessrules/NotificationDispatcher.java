package it.polimi.polidemonstrator.businesslogic.businessrules;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/13/2016.
 */
public class NotificationDispatcher implements ActionDispatcher {

    private String myMessageTitle;
    private String myMessageText;
    private Context context;

    public NotificationDispatcher(Context context, String myMessageTitle, String myMessageText) {
        this.context = context;
        this.myMessageTitle = myMessageTitle;
        this.myMessageText=myMessageText;

    }

    @Override
    public void fire() {
        //here you you implement what to do when Action is triggered (Send a notification to user)
        //other action classes are required

        
        Class<MainActivity> activityClass= MainActivity.class;
        Intent notifyIntent = new Intent(context,activityClass);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new android.app.Notification.Builder(context)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(myMessageTitle)
                .setContentText(myMessageText)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
