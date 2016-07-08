package it.polimi.polidemonstrator.businesslogic;


import android.content.Context;

import android.os.AsyncTask;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import it.polimi.polidemonstrator.R;
import it.polimi.polidemonstrator.businesslogic.businessrules.JSON_Ruler;
import it.polimi.polidemonstrator.businesslogic.businessrules.TestClass;

/**
 * Created by saeed on 5/26/2016.
 */
public class ListenerServiceFromWear extends WearableListenerService {


    Context context;

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

           /*
         * Receive the datachange from wear
         */
        context=getApplicationContext();

        final ArrayList<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);

        for (DataEvent event : events) {
            PutDataMapRequest putDataMapRequest =
                    PutDataMapRequest.createFromDataMapItem(DataMapItem.fromDataItem(event.getDataItem()));

            String path = event.getDataItem().getUri().getPath();
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (path.equals(getResources().getString(R.string.messagepath_beacon_Change))) {
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    String myMessage = dataMap.getString(getResources().getString(R.string.messagepath_beacon_Change));
                    evaluateUserState(myMessage);
                }
            }


        }
    }

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
                //EstimoteBeacon estimoteBeacon=new EstimoteBeacon(context);
                //call async task to fetch beacon lists and send it to wear
                //// TODO: 6/22/2016  replace the rooom id
                //estimoteBeacon.new BackgroundTaskGetRoomCorrelatedBeacons(1,false,context).execute();

            }

        }else if (messageEvent.getPath().equals(getResources().getString(R.string.messagepath_latest_measurements))){
            //message is related to beacons
            String myMessage=new String(messageEvent.getData());
            if(myMessage.equals(getResources().getString(R.string.message_latestMeasurementValues))){
               //fetch the latest measurement values and then send it by Data API to watch
                //// TODO: 7/8/2016 modify the room id
                Room room=new Room();
                room.setRoomid("1");
                MeasurementClass measurementClass =new MeasurementClass(context);
                measurementClass.new BackgroundTaskGetLatestMeasurementClassValues(context,room,true);




            }

        }


        /*
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
        */

    }

    private void evaluateUserState(String myMessage) {
        //String messages
        final String FF=context.getResources().getString(R.string.message_beaconChange_FF);
        final String TF=context.getResources().getString(R.string.message_beaconChange_TF);
        final String TT=context.getResources().getString(R.string.message_beaconChange_TT);


        if (myMessage.equals(FF) || myMessage.equals(TF) || myMessage.equals(TT) ){
            //Watch says user is just Entered the elevator from floor(Leaving?)  AND

            //// TODO: 6/27/2016  correct the room number and isrefresh
           JSON_Ruler.fetchCorrelatedUserRulesFromCloud(context,1,false,myMessage);

        }


    }




    //get internal and External sensor data form API
    public static class BackgroudTaskRuleFactGenerator extends AsyncTask<Void, Void, Map<String, String>> {
        Context context;
        List<JSON_Ruler> json_rulers;
        int roomID;


        public BackgroudTaskRuleFactGenerator(Context context,int roomID, List<JSON_Ruler> json_rulers){
            this.context=context;
            this.json_rulers=json_rulers;
            this.roomID=roomID;
        }



        @Override
        protected Map<String, String> doInBackground(Void... params) {
            RuleFactGenerator ruleFactGenerator =new RuleFactGenerator(context);
            Map<String, String> bindings = ruleFactGenerator.factGenerator(roomID);
            return bindings;
        }

        @Override
        protected void onPostExecute(Map<String, String> bindings) {
            super.onPostExecute(bindings);
            TestClass testRules=new TestClass(context,json_rulers,bindings);

        }


    }

}