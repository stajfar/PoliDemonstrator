package it.polimi.polidemonstrator.businesslogic;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
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

import java.util.List;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.R;


/**
 * Created by saeed on 6/7/2016.
 */
public class SendMessageServiceToWearble2  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    //to send message from wearable to handheld
    Node mNode; // the connected device to send the message to
    GoogleApiClient mGoogleApiClient;
    private String POLI_DEMONSTRATOR_MESSAGE_PATH ;
    private boolean mResolvingError=false;
    Context context;
    Bundle gotBasket;


    public SendMessageServiceToWearble2(Context context, Intent intent) {
       this.context=context;
        //Connect the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        gotBasket = intent.getExtras();
        connectToGoogleClientAPIandSendMessage();
    }





    private void connectToGoogleClientAPIandSendMessage() {
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // Toast.makeText(this, "connected", Toast.LENGTH_LONG).show();
        int myMessageType = gotBasket.getInt("myMessageType");
        if (myMessageType == MyWear_HandheldMessageAPIType.SendThroughMessageAPI.ordinal()) {
            resolveNodeAndRequestForMsgSend();
        } else if (myMessageType == MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()) {
            sendData();
        }
        //Release connection to GoogleAPI
        if (!mResolvingError) {
            mGoogleApiClient.disconnect();
        }
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

                }


            }
        });
    }


    /**
     * Send message to mobile handheld
     */
    private void sendMessage() {

        if (mNode != null && mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            this.POLI_DEMONSTRATOR_MESSAGE_PATH=gotBasket.getString("myMessagePath");
            String myTextMessage = gotBasket.getString("myMessage");


            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, mNode.getId(), POLI_DEMONSTRATOR_MESSAGE_PATH, myTextMessage.getBytes()).setResultCallback(

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



    private void sendData() {
        if (mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
            this.POLI_DEMONSTRATOR_MESSAGE_PATH = gotBasket.getString("myMessagePath");
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create(POLI_DEMONSTRATOR_MESSAGE_PATH);
            //check the message path to see which bundle keys should be read
            if (POLI_DEMONSTRATOR_MESSAGE_PATH.equals(context.getResources().getString(R.string.messagepath_beacon))) {
                //Means we have to put given beacon list into Shared Path
                String myListEstimoteBeaconsJson =  gotBasket.getString("myListEstimoteBeaconsJson");
                putDataMapReq.getDataMap().putString("myListEstimoteBeaconsJson",myListEstimoteBeaconsJson);

            } else if (POLI_DEMONSTRATOR_MESSAGE_PATH.equals(context.getResources().getString(R.string.messagepath_roomId))) {
                //means watch should be notified of room ID changes
                String roomID =  gotBasket.getString("myRoomID");
                putDataMapReq.getDataMap().putString("myRoomID",roomID);

            } else if (POLI_DEMONSTRATOR_MESSAGE_PATH.equals(context.getResources().getString(R.string.messagepath_latest_measurements))) {
                //means latest measurements average values should be sent to watch to be shown on its main Activity

                List<MeasurementClass> myMeasurementClassesLatestValueMessage = (List<MeasurementClass>) gotBasket
                        .getSerializable("myMeasurementClassesLatestValueMessage");
                for (MeasurementClass measurementClass : myMeasurementClassesLatestValueMessage) {
                    putDataMapReq.getDataMap().putStringArray(measurementClass.getSensorClasseId(), new String[]{
                            measurementClass.getSensorClassLabel(), measurementClass.getSensorClassSensorLatestValue()});

                }

            } else if (POLI_DEMONSTRATOR_MESSAGE_PATH.equals(context.getResources().getString(R.string.messagepath_last7days_measurements))){
                String json_MeasurementsLast7Days= gotBasket.getString("myMessage_json_Measurement7DaysValues");
                String measurementClassID= gotBasket.getString("myMessage_MeasurementClassID");

                putDataMapReq.getDataMap().putString("myMessage_json_Measurement7DaysValues",json_MeasurementsLast7Days);
                putDataMapReq.getDataMap().putString("myMessage_MeasurementClassID",measurementClassID);
            }


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
                    else {
                        Toast.makeText(context, "DataPath Updated.", Toast.LENGTH_LONG).show();
                    }
                }
            });
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
















    public enum MyWear_HandheldMessageAPIType{
        SendThroughMessageAPI (0),
        SendThroughDataAPI (1);

        private int myWear_HandheldMessageAPIType;

        MyWear_HandheldMessageAPIType(int i) {
            this.myWear_HandheldMessageAPIType=i;
        }

    }



}
