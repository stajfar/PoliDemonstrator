package it.polimi.polidemonstrator;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

import it.polimi.polidemonstrator.businessLogic.MeasurementClass;
import it.polimi.polidemonstrator.businessLogic.Room;


/**
 * Created by saeed on 5/18/2016.
 */
public class MyApplication extends Application {
    private BeaconManager beaconmanager;
    Region region;
    List<MeasurementClass> listMeasurementClassesParesed;
    MeasurementClass measurementClass;

    @Override
    public void onCreate() {
        super.onCreate();

        // Add this before all other beacon code
        EstimoteSDK.enableDebugLogging(true);

        beaconmanager = new BeaconManager(getApplicationContext());

        region=new Region("monitored region",UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),null,null);// 43060, 27142);
        //set background monitoring interval
       // beaconmanager.setBackgroundScanPeriod(10*1000,15*1000);

        // monitoring service to see if users enters or exits from a specific beacon region
        beaconmanager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                MyNotification.showNotification(MyApplication.this, MainActivity.class,
                        "Entring the region",
                        "you are Entring the building");
                Room room=new Room(MyApplication.this);
                room.setRoomid("1");


                new BackgroundTaskGetMeasurementList(room,true).execute();
            }

            @Override
            public void onExitedRegion(Region region) {

            }
        });
        // add this below:
        beaconmanager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconmanager.startMonitoring(region);



            }
        });
    }



    //Async Task to fetch Sensors Class list of a given room ID
    public class BackgroundTaskGetMeasurementList extends AsyncTask<String, Void, List<MeasurementClass>> {
        Room room;
        boolean isRefresh;

        public BackgroundTaskGetMeasurementList(Room room, boolean isRefresh) {
            this.room=room;
            this.isRefresh=isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected List<MeasurementClass> doInBackground(String... params) {

            String roomMeasurementClasslistJSON = room.getRoomMeasurementlist(room.getRoomid());

            if (roomMeasurementClasslistJSON != null){
                int[] UnwantedMeasurementIdentifiers = getResources().getIntArray(R.array.UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed = room.parsRoomSensorClassesJSON(roomMeasurementClasslistJSON,UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed=measurementClass.getMeasurementlatestValues(listMeasurementClassesParesed, room.getRoomid(), isRefresh);
            }
            return listMeasurementClassesParesed;
        }
        @Override
        protected void onPostExecute(List<MeasurementClass> listMeasurementClassesParesed) {
            if (listMeasurementClassesParesed != null) {
                Toast.makeText(MyApplication.this,
                        "Yes Server!",
                        Toast.LENGTH_SHORT).show();

                //Fill sensor spinner with given sensors list data
                //addItemsOnListViewMeasurementClasses(listMeasurementClassesParesed);
            }else{
                Toast.makeText(MyApplication.this,
                        "No Server!",
                        Toast.LENGTH_SHORT).show();
            }


        }

    }



}
