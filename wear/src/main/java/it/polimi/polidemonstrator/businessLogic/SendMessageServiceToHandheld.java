package it.polimi.polidemonstrator.businesslogic;

import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


/**
 * Created by saeed on 5/31/2016.
 */
public class SendMessageServiceToHandheld extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //to send message from wearable to handheld
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private String POLI_DEMONSTRATOR_MESSAGE_PATH ;
    private boolean mResolvingError=false;
    Context context;
    private String myMessage;
    private int myMessageType;




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
        Bundle extras = intent.getExtras();
        this.myMessageType=extras.getInt("myMessageType");
        this.myMessage = extras.getString("myMessage");
        this.POLI_DEMONSTRATOR_MESSAGE_PATH=extras.getString("myMessagePath");



        connectToGoogleClientAPIandSendMessage();

        return START_STICKY;
    }

    private void connectToGoogleClientAPIandSendMessage() {
        if (!mResolvingError) {
            mGoogleApiClient.connect();
           // resolveNode();
          //  Toast.makeText(this, "connecting", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
        if(myMessageType== MyWear_HandheldMessageAPIType.SendThroughMessageAPI.ordinal()) {
            resolveNodeAndRequestForMsgSend();
        }else if(myMessageType ==  MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal() ){
            sendData();
        }
        //then Destroy the service
        stopSelf();
    }

    /*
 * Resolve the node = the connected device to send the message to
 */
    private void resolveNodeAndRequestForMsgSend() {
        //get the handheld device that is connected to wearble
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult nodes) {
                for (Node node : nodes.getNodes()) {
                    mNode = node;
                }
                if (mNode != null) {
                  sendMessage();//send a message to the detected connected device
                }
                else {
                    //wearble  is not connected to handheld and can connect to internet directly to fetch data
                    //don't forget about google cloud messeging which is embedded in Android Wear APP
                }
            }
        });
    }


    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {

        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient,mNode.getId(), POLI_DEMONSTRATOR_MESSAGE_PATH, myMessage.getBytes()).setResultCallback(

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



    private void sendData(){
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create(POLI_DEMONSTRATOR_MESSAGE_PATH);
            putDataMapReq.getDataMap().putString(POLI_DEMONSTRATOR_MESSAGE_PATH, myMessage);
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            putDataReq.setUrgent();

            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);


            pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                    if (!dataItemResult.getStatus().isSuccess()) {
                        Log.e("TAG", "Failed to put DataItem"
                                + dataItemResult.getStatus().getStatusCode());
                    }

                }
            });
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
        Toast.makeText(this, "ConFialed,\n"+connectionResult.getErrorMessage() , Toast.LENGTH_LONG).show();
        if (mResolvingError) {

            return;
        } else if (connectionResult.hasResolution()) {
            Toast.makeText(this, "Con. has Resol" , Toast.LENGTH_SHORT).show();
           /* try {
                Activity mainAcitvity=new MainActivity();
                mResolvingError = true;
                connectionResult.startResolutionForResult(mainAcitvity,REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
            */
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



    public enum MyWear_HandheldMessageAPIType{
        SendThroughMessageAPI (0),
        SendThroughDataAPI (1);

        private int myWear_HandheldMessageAPIType;

        MyWear_HandheldMessageAPIType(int i) {
            this.myWear_HandheldMessageAPIType=i;
        }

    }



}
