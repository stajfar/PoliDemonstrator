package it.polimi.polidemonstrator;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;



import com.estimote.sdk.SystemRequirementsChecker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import it.polimi.polidemonstrator.businesslogic.BeaconMonitoring;
import it.polimi.polidemonstrator.businesslogic.EstimoteBeacon;
import it.polimi.polidemonstrator.businesslogic.MeasurementClass;

import it.polimi.polidemonstrator.businesslogic.SendMessageServiceToHandheld;


public class MainActivity extends Activity  implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError=false;
    GridViewPager pager;
    DotsPageIndicator dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                pager = (GridViewPager) stub.findViewById(R.id.pager);
                dots = (DotsPageIndicator) stub.findViewById(R.id.indicator);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }


    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //read previously stored data
        readPreviousStoredDataAPI();
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    private void readPreviousStoredDataAPI() {
        PendingResult<DataItemBuffer> results = Wearable.DataApi.getDataItems(mGoogleApiClient);

        results.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {

                    for(DataItem dataitem: dataItems){
                        String path = dataitem.getUri().getPath();
                        if (path.equals(getResources().getString(R.string.messagepath_latest_measurements))) {
                            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataitem);
                            DataMap dataMap = dataMapItem.getDataMap();
                            Set<String> dataMapKeySet=dataMap.keySet();
                            List<MeasurementClass> listMeasurementClass=new ArrayList<MeasurementClass>();
                            for(String key : dataMapKeySet)
                            {
                                String[] gridViewViewItemLookupTable= MeasurementClass.getMeasurementGridViewPagerItem(key);
                                String[] dataMapValueStringArray=  dataMap.getStringArray(key);
                                MeasurementClass measurementClass=new MeasurementClass();
                                measurementClass.setSensorClasseId(key);
                                measurementClass.setSensorClassLabel(gridViewViewItemLookupTable[0]);
                                measurementClass.setSensorClassImage(Integer.valueOf(gridViewViewItemLookupTable[1]));
                                measurementClass.setSensorClassSensorLatestValue(dataMapValueStringArray[1]);
                                measurementClass.setSensorClassMeasurementUnit(gridViewViewItemLookupTable[2]);
                                listMeasurementClass.add(measurementClass);
                            }
                            pager.setAdapter(new MyGridPagerAdapter(context,R.layout.grid_view_pager_item,listMeasurementClass));

                            dots.setPager(pager);
                        } else if(path.equals(getResources().getString(R.string.messagepath_beacon))) {
                            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataitem);
                            DataMap dataMap = dataMapItem.getDataMap();
                            String myListEstimoteBeacons= dataMap.getString("myListEstimoteBeaconsJson");
                            if(myListEstimoteBeacons != null) {
                                final List<EstimoteBeacon> listBeacons = EstimoteBeacon.parsJSON_Beacons(myListEstimoteBeacons);
                                BeaconMonitoring beaconMonitoring = new BeaconMonitoring(context);
                                beaconMonitoring.initializeBeaconManager(listBeacons);
                            }

                        } else if(path.equals(getResources().getString(R.string.messagepath_roomId))){
                            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataitem);
                            DataMap dataMap = dataMapItem.getDataMap();
                            String roomID= dataMap.getString("myRoomID");
                            //send a message to handheld to update DataApi about latest measurements
                            new BackgroundTaskGetUpdatedLatestMeasurementValues(roomID).execute();

                        }
                    }
                dataItems.release();
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
               // Log.d(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                PutDataMapRequest putDataMapRequest =
                        PutDataMapRequest.createFromDataMapItem(DataMapItem.fromDataItem(event.getDataItem()));

                String path = event.getDataItem().getUri().getPath();
                if (path.equals(getResources().getString(R.string.messagepath_latest_measurements))) {
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    Set<String> dataMapKeySet=dataMap.keySet();
                    List<MeasurementClass> listMeasurementClass=new ArrayList<MeasurementClass>();
                    for(String key : dataMapKeySet)
                    {
                        String[] gridViewViewItem= MeasurementClass.getMeasurementGridViewPagerItem(key);
                        String[] dataMapValueStringArray=  dataMap.getStringArray(key);
                        MeasurementClass measurementClass=new MeasurementClass();
                        measurementClass.setSensorClassLabel(gridViewViewItem[0]);
                        measurementClass.setSensorClassImage(Integer.valueOf(gridViewViewItem[1]));
                        measurementClass.setSensorClassSensorLatestValue(dataMapValueStringArray[1]);
                        measurementClass.setSensorClassMeasurementUnit(gridViewViewItem[2]);
                        listMeasurementClass.add(measurementClass);
                    }
                    pager.setAdapter(new MyGridPagerAdapter(context,R.layout.grid_view_pager_item,listMeasurementClass));
                    dots.setPager(pager);
                }else if(path.equals(getResources().getString(R.string.messagepath_beacon))) {
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    String myListEstimoteBeacons = dataMap.getString("myListEstimoteBeaconsJson");
                    if (myListEstimoteBeacons != null) {
                        final List<EstimoteBeacon> listBeacons = EstimoteBeacon.parsJSON_Beacons(myListEstimoteBeacons);
                        BeaconMonitoring beaconMonitoring = new BeaconMonitoring(context);
                        beaconMonitoring.initializeBeaconManager(listBeacons);
                    }
                }
            }
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mResolvingError = true;

    }




    //Async Task to fetch Sensors Class list of a given room ID
    public class BackgroundTaskGetUpdatedLatestMeasurementValues extends AsyncTask<Void, Void, Void> {
        String roomId;

        public BackgroundTaskGetUpdatedLatestMeasurementValues(String roomID) {
            this.roomId=roomID;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(roomId != null) {
                //send a message by service to handheld, requesting beacons of the room
                String myMessagePath = getResources().getString(R.string.messagepath_latest_measurements);
                String myMessage = roomId;
                startService(new Intent(context,
                        SendMessageServiceToHandheld.class)
                        .putExtra("myMessagePath", myMessagePath).putExtra("myMessage", myMessage)
                        .putExtra("myMessageType", SendMessageServiceToHandheld.MyWear_HandheldMessageAPIType.SendThroughMessageAPI.ordinal()));

            }
            return null;
        }
    }



}
