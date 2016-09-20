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
                    System.out.println("new State: " + myMessage+". Evaluating...");
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


        if (messageEvent.getPath().equals(getResources().getString(R.string.messagepath_latest_measurements))){
            //message is related to beacons
            String myMessage_roomID=new String(messageEvent.getData());
            Room room=new Room();
            room.setRoomid(myMessage_roomID);

            MeasurementClass measurementClass =new MeasurementClass(context);
            measurementClass.new BackgroundTaskGetLatestMeasurementClassValues(context,room,true).execute();
        }else if(messageEvent.getPath().equals(getResources().getString(R.string.messagepath_last7days_measurements))){
            //message is related to MeasurementID
            String myMessage_RoomID_MeasurementID=new String(messageEvent.getData());
            String[] roomID_MeasurementID=myMessage_RoomID_MeasurementID.split(",");
            //Call a Background Task to select the json form cloud and to put it into DataAPI
            MeasurementClass measurementClass =new MeasurementClass(context);
            measurementClass. new BackgroundTaskGetLast7DaysMeasurementClassValues(roomID_MeasurementID[0],
                    roomID_MeasurementID[1],false).execute();
        }



    }

    private void evaluateUserState(String myMessage) {
        //String messages
        final String FF=context.getResources().getString(R.string.message_beaconChange_FF);
        final String TF=context.getResources().getString(R.string.message_beaconChange_TF);
        final String TT=context.getResources().getString(R.string.message_beaconChange_TT);


        if (myMessage.equals(FF) || myMessage.equals(TF) || myMessage.equals(TT) ){
            //Watch says user is just Entered the elevator from floor(Leaving?)  AND

            //  room id,take it from preferences?yes!!
            int roomID=MyPreferences.getPreferenceRoomID(context);
           JSON_Ruler.fetchCorrelatedUserRulesFromCloud(context,roomID,false,myMessage);

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
            new TestClass(context,json_rulers,bindings);

        }


    }

}