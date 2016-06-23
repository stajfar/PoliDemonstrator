package it.polimi.polidemonstrator.businessLogic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.MyNotification;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/3/2016.
 */
public class BeaconMonitoring  implements SensorEventListener {
    private BeaconManager beaconmanager;
    private Context context;
    List<EstimoteBeacon> listCorrelatedEstimoteBeacons;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    public  BeaconMonitoring(Context context) {
        this.context = context;


        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(mSensor != null){
            mSensorManager.registerListener(this,mSensor,mSensorManager.SENSOR_DELAY_NORMAL);
        }

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
            beaconmanager.setBackgroundScanPeriod(5 * 1000, 15 * 1000);
            // monitoring service to see if users enters or exits from a specific beacon region
            beaconmanager.setMonitoringListener(new beaconManagerMonotoringListener());
            beaconmanager.connect(new beaconManagerServiceReadyCallback());

        }

    }
    boolean isScanning=false;
    float oldStepsValue=0;
    @Override
    public void onSensorChanged(SensorEvent event) {
        float newStepsValue=event.values[0];

        if(newStepsValue > oldStepsValue +3){//ignore  steps to make sure if user is really walking
            //reduce sleep time of beacon manager
            //set background monitoring interval
           //this means that currently we are not scanning and we are discunnected from beacon manager
            if(listCorrelatedEstimoteBeacons != null && isScanning==false) {
                beaconmanager = new BeaconManager(context);
                //set background monitoring interval
                beaconmanager.setBackgroundScanPeriod(5 * 1000, 15 * 1000);
                // monitoring service to see if users enters or exits from a specific beacon region
                beaconmanager.setMonitoringListener(new beaconManagerMonotoringListener());
                beaconmanager.connect(new beaconManagerServiceReadyCallback());
            }
        }
        oldStepsValue=newStepsValue;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    StateMachine.State oldState=StateMachine.State.FF;
    private class beaconManagerMonotoringListener implements BeaconManager.MonitoringListener {

        @Override
        public void onEnteredRegion(final Region region, List<Beacon> list) {
           // MyNotification.showNotification(context, MainActivity.class,"Entring the region", "sending msg");
            //set background monitoring interval

            switch(region.getIdentifier()) {
                case "elevator":
                    //happened event
                    MyNotification.showNotification(context, MainActivity.class,"Enter  elevator", "");
                    StateMachine.Symbols newInputEvent=StateMachine.Symbols.Elv_in;
                    StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInputEvent.ordinal()];
                    if(oldState== StateMachine.State.TF && newState == StateMachine.State.FF ||
                            oldState== StateMachine.State.TT && newState == StateMachine.State.FF){
                        //this means that user is going to leave the building monitor if everything is fine
                        //context.startService(new Intent(context, SendMessageServiceToHandheld.class).putExtra("myMessage", "userLeaving"));
                        MyNotification.showNotification(context, MainActivity.class,
                                "Leaving?",
                                newState.toString());
                        //Send TT message to handheld
                        //send a message by service to handheld, requesting beacons of the room
                        sendMessageToHandheld_BeaconChange(newState);

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
                    //Send TT message to handheld
                    //send a message by service to handheld, requesting beacons of the room
                    sendMessageToHandheld_BeaconChange(newState2);
                    break;
            }
            //stop monitoring beacon manager to save battery
            //stop beacon monitoring to save battery
           ;
           // beaconmanager.stopMonitoring(region);

           // isScanning=false;//so you can connect to beacon manager after user start walking
            if(isTimerstarted ==false) {
                isTimerstarted=true;
                Timer timer = new Timer();
                if(timerTask != null){
                    timerTask.cancel();
                    isTimerstarted=false;
                }
                timerTask = new MyTimerTask();
                //Start the timer to do TimerTask after 2 minutes
                timer.schedule(timerTask, 15 * 60 * 1000);
            }
        }

        MyTimerTask timerTask=null;
        boolean isTimerstarted=false;
        //inner TimerClass
        class MyTimerTask extends TimerTask
        {
            @Override
            public void run() {
                beaconmanager.disconnect();
                isScanning = false;//so you can connect to beacon manager after user start walking
                // timer.cancel();
                isTimerstarted=false;
            }
        }



        @Override
        public void onExitedRegion(final Region region) {
            //MyNotification.showNotification(context, MainActivity.class,"Exiting the region", "sending msg");
            switch(region.getIdentifier()) {
                case "elevator":
                    if( oldState != StateMachine.State.TT ) {
                        StateMachine.Symbols newInput = StateMachine.Symbols.Elv_out;
                        StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInput.ordinal()];

                        oldState = newState;
                        MyNotification.showNotification(context, MainActivity.class, "Exiting  elevator", newState.toString());
                        sendMessageToHandheld_BeaconChange(newState);
                    }
                    break;
                case "room":
                    if(oldState != StateMachine.State.FF) {
                        StateMachine.Symbols newInput2 = StateMachine.Symbols.Rm_out;
                        StateMachine.State newState2 = StateMachine.transition[oldState.ordinal()][newInput2.ordinal()];
                        oldState = newState2;
                        MyNotification.showNotification(context, MainActivity.class, "Exiting  room", newState2.toString());
                        sendMessageToHandheld_BeaconChange(newState2);
                    }
                    break;
            }

            //stop beacon monitoring to save battery
            // isScanning=false;//so you can connect to beacon manager after user start walking
            if(isTimerstarted ==false) {
                isTimerstarted=true;
                Timer timer = new Timer();
                if(timerTask != null){
                    timerTask.cancel();
                    isTimerstarted=false;
                }
                timerTask = new MyTimerTask();
                //Start the timer to do TimerTask after 2 minutes
                timer.schedule(timerTask, 10 * 60 * 1000);
            }
            //Room room=new Room(MyApplication.this);
            //room.setRoomid("1");
            //new BackgroundTaskGetMeasurementList(room,true).execute();
            // sendMessage();
        }
    }

    private void sendMessageToHandheld_BeaconChange(StateMachine.State newState) {
        //send a message by service to handheld, requesting beacons of the room
        String myMessagePath=context.getResources().getString(R.string.messagepath_beacon_Change);
        String myMessage="";
        switch (newState){
            case FF:
                myMessage=context.getResources().getString(R.string.message_beaconChange_FF);
                break;
            case TF:
                myMessage=context.getResources().getString(R.string.message_beaconChange_TF);
                break;
            case TT:
                myMessage=context.getResources().getString(R.string.message_beaconChange_TT);
                break;
        }
        context.startService(new Intent(context,
                SendMessageServiceToHandheld.class).putExtra("myMessagePath",myMessagePath).putExtra("myMessage",myMessage));
    }

    List<Region> myRegionsList=null;
    private class beaconManagerServiceReadyCallback implements BeaconManager.ServiceReadyCallback {
        @Override
        public void onServiceReady() {
            myRegionsList=new ArrayList<>();
            for (EstimoteBeacon estimoteBeaconItem : listCorrelatedEstimoteBeacons) {
                Region region = new Region(estimoteBeaconItem.getIdentifier(), UUID.fromString(estimoteBeaconItem.getUUID()),
                        estimoteBeaconItem.getMajor(),estimoteBeaconItem.getMinor());
                myRegionsList.add(region);
                isScanning=true;// we are scanning so do not try to connect again until we disconnect
                beaconmanager.startMonitoring(region);
            }
        }
    }

}
