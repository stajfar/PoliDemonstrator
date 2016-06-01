package it.polimi.polidemonstrator.businessLogic;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.MyNotification;

/**
 * Created by saeed on 5/31/2016.
 */
public class ConnectionServiceToHandheld extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private BeaconManager beaconmanager;
    Region regionRoom;
    Region regionElevator;
    Context context;


    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        Toast.makeText(this, "My Service started", Toast.LENGTH_LONG).show();
        setWearToHandheldSettings();




        // Add this before all other beacon code
        EstimoteSDK.enableDebugLogging(true);

        beaconmanager = new BeaconManager(getApplicationContext());

        regionRoom=new Region("monitored region room", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),43060, 27142);

        regionElevator=new Region("monitored region elevator", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"),43060, 27142);
        //set background monitoring interval
        beaconmanager.setBackgroundScanPeriod(10*1000,15*1000);

        // monitoring service to see if users enters or exits from a specific beacon region
        beaconmanager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                MyNotification.showNotification(context, MainActivity.class,
                        "Entring the region",
                        "sending msg");



                //Room room=new Room(MyApplication.this);
                //room.setRoomid("1");
                //new BackgroundTaskGetMeasurementList(room,true).execute();


                StateMachine stateMachine=new StateMachine();
               StateMachine.State newState= stateMachine.transition[StateMachine.State.FF.ordinal()][StateMachine.Symbols.Rm_out.ordinal()];

                sendMessage();
            }

            @Override
            public void onExitedRegion(Region region) {
                MyNotification.showNotification(context, MainActivity.class,
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

                beaconmanager.startMonitoring(regionElevator);
                beaconmanager.startMonitoring(regionRoom);



            }
        });
    }

    //to send message from wearable to handheld
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String HELLO_WORLD_WEAR_PATH = "/hello-world-wear";
    private boolean mResolvingError=false;


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



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }




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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        resolveNode();
        Toast.makeText(this, "My connection connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Activity mainActivity=new MainActivity();
        try {
            connectionResult.startResolutionForResult(mainActivity, ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
