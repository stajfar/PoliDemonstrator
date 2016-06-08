package it.polimi.polidemonstrator.businessLogic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/7/2016.
 */
public class ListenerServiceFromHandheld extends WearableListenerService {

    private String myMessagePath;
    private String myMessage;
    Context context;







    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
          /*
         * Receive the message from wear
         */
        context=this;
        if (messageEvent.getPath().equals(getResources().getString(R.string.messagepath_beacon))) {
            //message is related to beacons
            String myMessage=new String(messageEvent.getData());
            if(myMessage!= null){
                //first send jason message to EstimoteBeacon to pars it
                final List<EstimoteBeacon> listBeacons=EstimoteBeacon.parsJSON_Beacons(myMessage);
                //start monitoring the beacons by starting a new service as this listener service will be killed



                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        BeaconMonitoring beaconMonitoring=new BeaconMonitoring(context);
                        beaconMonitoring.initializeBeaconManager(listBeacons);
                    } // This is your code
                };
                mainHandler.post(myRunnable);

            }

        }




/*
        if (messageEvent.getPath().equals(myMessagePath)) {
            //do something when you receive the message
            //fetch data from internet and push it back to wear
            String myMessage=new String(messageEvent.getData());

            context=this;
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
*/
    }

}