package it.polimi.polidemonstrator.businessLogic;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
public class SendMessageServiceToHandheld extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //to send message from wearable to handheld
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private static final String POLI_DEMONSTRATOR_WEAR_PATH = "/poliDemonstrator-wear";
    private boolean mResolvingError=false;



    @Override
    public void onCreate() {
        super.onCreate();
        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        connectToGoogleClientAPIandSendMessage();

        return START_STICKY;
    }

    private void connectToGoogleClientAPIandSendMessage() {
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        resolveNode();
        Toast.makeText(this, "connected", Toast.LENGTH_SHORT).show();
    }

    /*
 * Resolve the node = the connected device to send the message to
 */
    private void resolveNode() {
        //get the handheld device that is connected to wearble
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
                sendMessage();//send a message to the detected connected device
                stopSelf();
            }
        });
    }


    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {

        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), POLI_DEMONSTRATOR_WEAR_PATH, null).setResultCallback(

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
    public void onConnectionSuspended(int i) {

    }

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                Activity mainAcitvity=new MainActivity();
                mResolvingError = true;
                connectionResult.startResolutionForResult(mainAcitvity,REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()

            mResolvingError = true;
        }


    }






    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (!mResolvingError) {
            mGoogleApiClient.disconnect();
        }

    }






}
