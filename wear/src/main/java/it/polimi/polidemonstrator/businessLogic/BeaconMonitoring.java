package it.polimi.polidemonstrator.businessLogic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.MyNotification;

/**
 * Created by saeed on 6/3/2016.
 */
public class BeaconMonitoring {
    private BeaconManager beaconmanager;
    private Context context;
    List<EstimoteBeacon> listCorrelatedEstimoteBeacons;

    public  BeaconMonitoring(Context context) {
        this.context = context;
        //EstimoteBeacon estimoteBeacon=new EstimoteBeacon(context);
        //call async task to fetch beacon lists
        //estimoteBeacon.new BackgroundTaskGetRoomCorrelatedBeacons(1,false,context).execute();
    }
    public  BeaconMonitoring() {

    }



    //this method will be called by PostExecute of AsyncTask when the results of asyncTask is ready
    public  void initializeBeaconManager(List<EstimoteBeacon> listEstimoteBeacons_result) {



        if(listEstimoteBeacons_result != null){
            Toast.makeText(context, "Be list Recved", Toast.LENGTH_SHORT).show();
            this.listCorrelatedEstimoteBeacons=listEstimoteBeacons_result;
            // Add this before all other beacon code
            EstimoteSDK.enableDebugLogging(true);
            beaconmanager = new BeaconManager(context);
            //set background monitoring interval
            beaconmanager.setBackgroundScanPeriod(10 * 1000, 15 * 1000);
            // monitoring service to see if users enters or exits from a specific beacon region
            beaconmanager.setMonitoringListener(new beaconManagerMonotoringListener());
            beaconmanager.connect(new beaconManagerServiceReadyCallback());

        }

    }



    private class beaconManagerMonotoringListener implements BeaconManager.MonitoringListener {
        StateMachine.State oldState=StateMachine.State.FF;
        @Override
        public void onEnteredRegion(Region region, List<Beacon> list) {
           // MyNotification.showNotification(context, MainActivity.class,"Entring the region", "sending msg");
            switch(region.getIdentifier()) {
                case "elevator":
                    //happened event
                    MyNotification.showNotification(context, MainActivity.class,"Enter  elevator", "");
                    StateMachine.Symbols newInputEvent=StateMachine.Symbols.Elv_in;
                    StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInputEvent.ordinal()];
                    if(oldState== StateMachine.State.TF && newState == StateMachine.State.FF){
                        //this means that user is going to leave the building monitor if everything is fine
                        //context.startService(new Intent(context, SendMessageServiceToHandheld.class).putExtra("myMessage", "userLeaving"));
                        MyNotification.showNotification(context, MainActivity.class,
                                "Leaving?",
                                newState.toString());

                        //Room room=new Room(MyApplication.this);
                        //room.setRoomid("1");
                        //new BackgroundTaskGetMeasurementList(room,true).execute();
                    }
                    //ok everything is down and we have to update old state by new state

                    oldState=newState;
                    break;
                case "room":
                    StateMachine.Symbols newInput=StateMachine.Symbols.Rm_in;
                    StateMachine.State newState2 = StateMachine.transition[oldState.ordinal()][newInput.ordinal()];
                    oldState=newState2;
                    MyNotification.showNotification(context, MainActivity.class,"Enter  room", newState2.toString());
                    break;
            }
        }

        @Override
        public void onExitedRegion(Region region) {
            //MyNotification.showNotification(context, MainActivity.class,"Exiting the region", "sending msg");

            switch(region.getIdentifier()) {
                case "elevator":
                    StateMachine.Symbols newInput = StateMachine.Symbols.Elv_out;
                    StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInput.ordinal()];

                    oldState = newState;
                    MyNotification.showNotification(context, MainActivity.class,"Exiting  elevator", newState.toString());
                    break;
                case "room":
                    beaconmanager.setBackgroundScanPeriod(10 * 1000, 15 * 1000);
                    StateMachine.Symbols newInput2 = StateMachine.Symbols.Rm_out;
                    StateMachine.State newState2 = StateMachine.transition[oldState.ordinal()][newInput2.ordinal()];
                    oldState = newState2;
                    MyNotification.showNotification(context, MainActivity.class,"Exiting  room", newState2.toString());
                    break;
            }
            //Room room=new Room(MyApplication.this);
            //room.setRoomid("1");
            //new BackgroundTaskGetMeasurementList(room,true).execute();

            // sendMessage();

        }
    }

    private class beaconManagerServiceReadyCallback implements BeaconManager.ServiceReadyCallback {
        @Override
        public void onServiceReady() {
            for (EstimoteBeacon estimoteBeaconItem : listCorrelatedEstimoteBeacons) {
                Region region = new Region(estimoteBeaconItem.getIdentifier(), UUID.fromString(estimoteBeaconItem.getUUID()),
                        estimoteBeaconItem.getMajor(),estimoteBeaconItem.getMinor());
                beaconmanager.startMonitoring(region);
            }

        }
    }



}
