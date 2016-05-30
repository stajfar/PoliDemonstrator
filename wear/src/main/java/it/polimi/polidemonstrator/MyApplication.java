package it.polimi.polidemonstrator;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.UUID;

import it.polimi.polidemonstrator.businessLogic.MeasurementClass;
import it.polimi.polidemonstrator.businessLogic.Room;


/**
 * Created by saeed on 5/18/2016.
 */
public class MyApplication extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private BeaconManager beaconmanager;
    Region region;
    List<MeasurementClass> listMeasurementClassesParesed;
    MeasurementClass measurementClass;

    @Override
    public void onCreate() {
        super.onCreate();

        setWearToHandheldSettings();




        // Add this before all other beacon code
        EstimoteSDK.enableDebugLogging(true);

        beaconmanager = new BeaconManager(getApplicationContext());

        region=new Region("monitored region",UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),null,null);// 43060, 27142);
        //set background monitoring interval
        beaconmanager.setBackgroundScanPeriod(10*1000,15*1000);

        // monitoring service to see if users enters or exits from a specific beacon region
        beaconmanager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                MyNotification.showNotification(MyApplication.this, MainActivity.class,
                        "Entring the region",
                        "sending msg");


                //Room room=new Room(MyApplication.this);
                //room.setRoomid("1");
                //new BackgroundTaskGetMeasurementList(room,true).execute();

                sendMessage();
            }

            @Override
            public void onExitedRegion(Region region) {
                MyNotification.showNotification(MyApplication.this, MainActivity.class,
                        "Exiting the region",
                        "sending msg");


                //Room room=new Room(MyApplication.this);
                //room.setRoomid("1");
                //new BackgroundTaskGetMeasurementList(room,true).execute();

                sendMessage();

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

    private void setWearToHandheldSettings() {
        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }


    //to send message from wearable to handheld
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
    private boolean mResolvingError=false;


    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {

        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), HELLO_WORLD_WEAR_PATH, null).setResultCallback(

                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {

                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e("TAG", "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }else{
            //Improve your code
        }

    }

    /*
    * Resolve the node = the connected device to send the message to
    */
    private void resolveNode() {

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        resolveNode();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
