package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;
import android.os.Handler;

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


import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/7/2016.
 */
public class ListenerServiceFromHandheld extends WearableListenerService {

    private String myMessagePath;
    private String myMessage;
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
                if (path.equals(getResources().getString(R.string.messagepath_roomId))) {
                    //do sth here

                }else if (path.equals(getResources().getString(R.string.messagepath_beacon))){
                    //beacon list related to selected room is fetched and now we can start Beacon monitoring
                    //first send jason message to EstimoteBeacon to pars it
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    String myListEstimoteBeacons= dataMap.getString("myListEstimoteBeacons");
                    final List<EstimoteBeacon> listBeacons=EstimoteBeacon.parsJSON_Beacons(myListEstimoteBeacons);
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


        }
    }







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


            }

        }





    }

}