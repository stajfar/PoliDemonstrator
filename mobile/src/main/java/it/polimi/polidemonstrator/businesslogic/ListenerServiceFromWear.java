package it.polimi.polidemonstrator.businesslogic;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.R;
import it.polimi.polidemonstrator.businesslogic.businessrules.JSON_Ruler;
import it.polimi.polidemonstrator.businesslogic.businessrules.TestClass;

/**
 * Created by saeed on 5/26/2016.
 */
public class ListenerServiceFromWear extends WearableListenerService {

    private static final String POLI_DEMONSTRATOR_WEAR_PATH = "/poliDemonstrator-wear";
    Context context;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
          /*
         * Receive the message from wear
         */
        context=getApplicationContext();
        if (messageEvent.getPath().equals(getResources().getString(R.string.messagepath_beacon))) {
            //message is related to beacons
            String myMessage=new String(messageEvent.getData());
            if(myMessage.equals(getResources().getString(R.string.message_fetchBeaconList))){
                //fetch the list of beacons from internet and send it back to wearble
                EstimoteBeacon estimoteBeacon=new EstimoteBeacon(context);
                //call async task to fetch beacon lists
                estimoteBeacon.new BackgroundTaskGetRoomCorrelatedBeacons(1,false,context).execute();

            }

        }else if (messageEvent.getPath().equals(getResources().getString(R.string.messagepath_beacon_Change))) {
            //message is related to beacons
            String myMessage=new String(messageEvent.getData());
            //String messages
            final String FF=context.getResources().getString(R.string.message_beaconChange_FF);
            final String TF=context.getResources().getString(R.string.message_beaconChange_TF);
            final String TT=context.getResources().getString(R.string.message_beaconChange_TT);
            if (myMessage.equals(FF)){
                //Watch says user is just Entered the elevator from floor

            }else if(myMessage.equals(TF)){
                //Watch says user is just exited the room, yet is present inside the floor

            }else if(myMessage.equals(TT)){
                //Watch says user is just entered the room
                
                JSON_Ruler json_rule=new JSON_Ruler("NotificationDispatcher","Potenza_attiva_forza = 'O' AND Potenza_attiva_luci = 'C'"
                        ,"Consumption Warning!!","Lights are OFF!");
                List<JSON_Ruler> json_rulers=new ArrayList<>();
                //// TODO: 6/21/2016  fix this to be able to add more rules
                json_rulers.add(json_rule);

               



                //fetch rules from DB

                //fetch latest Sensor Data


                //Evaluate rules

                //Send a Notification to user
                new BackgroudTaskRuleFactGenerator(context,json_rulers).execute();

            }
        }



        if (messageEvent.getPath().equals(POLI_DEMONSTRATOR_WEAR_PATH)) {
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

    }


    //get internal and External sensor data form API
    public class BackgroudTaskRuleFactGenerator extends AsyncTask<Void, Void, Map<String, String>> {
        Context context;
        List<JSON_Ruler> json_rulers;

        BackgroudTaskRuleFactGenerator(Context context, List<JSON_Ruler> json_rulers){
            this.context=context;
            this.json_rulers=json_rulers;
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            RuleFactGenerator ruleFactGenerator=new RuleFactGenerator(context);
            Map<String, String> bindings = ruleFactGenerator.factGenerator("1");
            return bindings;
        }

        @Override
        protected void onPostExecute(Map<String, String> bindings) {
            super.onPostExecute(bindings);
            TestClass test=new TestClass(context,json_rulers,bindings);

        }


    }

}