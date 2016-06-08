package it.polimi.polidemonstrator.businesslogic;

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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/7/2016.
 */
public class SendMessageServiceToWearble extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //to send message from wearable to handheld
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private String POLI_DEMONSTRATOR_MESSAGE_PATH ;
    private boolean mResolvingError=false;
    Context context;
    private String myMessage;



    @Override
    public void onCreate() {
        super.onCreate();
        context=this.getApplicationContext();
        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle gotBasket = intent.getExtras();

        this.POLI_DEMONSTRATOR_MESSAGE_PATH=gotBasket.getString("myMessagePath");
        this.myMessage = gotBasket.getString("myMessage");

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
                if (mNode != null) {
                    sendMessage();//send a message to the detected connected device
                    stopSelf();
                }
                else {
                    //wearble  is not connected to handheld and can connect to internet directly to fetch data


                }


            }
        });
    }


    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {

        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), POLI_DEMONSTRATOR_MESSAGE_PATH, myMessage.getBytes()).setResultCallback(

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
