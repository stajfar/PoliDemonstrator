package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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
import java.util.LinkedHashMap;

import java.util.NavigableMap;

import java.util.TreeMap;

import it.polimi.polidemonstrator.businesslogic.DateTimeObj;
import it.polimi.polidemonstrator.businesslogic.MeasurementClass;
import it.polimi.polidemonstrator.businesslogic.SendMessageServiceToHandheld;

/**
 * Created by saeed on 7/5/2016.
 */
public class MeasurementDetailActivity extends Activity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    String measurementClassID;
    String measuementClassLabel;
    String measurmentClassMeasurementUnit;
    Context context;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError=false;
    LineChart lineChart;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement_details);
        context = this;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        Bundle gotBasket = getIntent().getExtras();
        measurementClassID = gotBasket.getString("measurementClassId");
        measuementClassLabel=gotBasket.getString("measurementClassLabel");
        measurmentClassMeasurementUnit=gotBasket.getString("measurementClassMeasurementUnit");

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new myWatcthOnLayoutInflatedListener());


        //send a message to handheld and ask it to update the DataAPI, (Handheld catches data for 60 mins)
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
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
                    if(path.equals(getResources().getString(R.string.messagepath_roomId))){
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataitem);
                        DataMap dataMap = dataMapItem.getDataMap();
                        String roomID= dataMap.getString("myRoomID");
                        //call the AsyncTask to get latest measurement values of last 7days
                        new BackgroundTaskRequestLast7DaysMeasurementsFromHandheld(roomID,measurementClassID).execute();
                    } else if (path.equals(getResources().getString(R.string.messagepath_last7days_measurements))) {
                        DataMapItem dataMapItem = DataMapItem.fromDataItem(dataitem);
                        DataMap dataMap = dataMapItem.getDataMap();
                        String measurementID=dataMap.getString("myMessage_MeasurementClassID");
                        if(measurementID.equals(measurementClassID)) {
                            String json_MeasurementLast7Days = dataMap.getString("myMessage_json_Measurement7DaysValues");
                            //here you have to pars the json results and feed the Charts
                            if (json_MeasurementLast7Days != null) {
                                analyzeChartByGivenJson(json_MeasurementLast7Days);
                            }
                        }

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
                if (path.equals(getResources().getString(R.string.messagepath_last7days_measurements))) {
                    DataMap dataMap = putDataMapRequest.getDataMap();
                    String json_MeasurementLast7Days= dataMap.getString("myMessage_json_Measurement7DaysValues");
                    //String measurementID=dataMap.getString("myMessage_MeasurementClassID");
                    //here you have to pars the json results and feed the Charts
                    if(json_MeasurementLast7Days != null) {
                       analyzeChartByGivenJson(json_MeasurementLast7Days);
                    }

                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mResolvingError = true;
    }

    private class myWatcthOnLayoutInflatedListener implements WatchViewStub.OnLayoutInflatedListener {
        @Override
        public void onLayoutInflated(WatchViewStub watchViewStub) {

            // in this example, a LineChart is initialized from xml
            lineChart = (LineChart) watchViewStub.findViewById(R.id.chart);
            barChart=(BarChart)watchViewStub.findViewById(R.id.barchart);
        }
    }

    public class BackgroundTaskRequestLast7DaysMeasurementsFromHandheld extends AsyncTask<Void, Void, Void> {
        String roomid,measurementid;
        public BackgroundTaskRequestLast7DaysMeasurementsFromHandheld(String roomID, String measurementClassID) {
            this.roomid=roomID;
            this.measurementid=measurementClassID;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(measurementClassID != null) {
                //send a message by service to handheld, requesting beacons of the room
                String myMessagePath = getResources().getString(R.string.messagepath_last7days_measurements);
                String myMessage =roomid+","+measurementClassID;
                startService(new Intent(context,
                        SendMessageServiceToHandheld.class)
                        .putExtra("myMessagePath", myMessagePath).putExtra("myMessage", myMessage)
                        .putExtra("myMessageType", SendMessageServiceToHandheld.MyWear_HandheldMessageAPIType.SendThroughMessageAPI.ordinal()));
            }
            return null;
        }
    }


    private void analyzeChartByGivenJson(String json_measurementLast7Days){
        LinkedHashMap<Long,Float> map= MeasurementClass.parsJSON_Measurement(json_measurementLast7Days);
        NavigableMap<Long,Float> navigableMap=new TreeMap<>();
        navigableMap.putAll(map);
        //then create 7 ranges of keys, one for each day of last 7 days (in descending order)
        Long currentDateInMilli=DateTimeObj.getDateTimeInMilli(DateTimeObj.getCurrentDate()+" 23:59");
        int _24Hours=86400000;
        LinkedHashMap<String,Float> barchartLinkedMap=new LinkedHashMap<>();
        for(int i=6; i>=1; i--){//i=6 7days ago... i=0 means means today
            long endRange= currentDateInMilli-i*_24Hours;
            long startRange= currentDateInMilli-(i+1)*_24Hours;
            float average=computeAverage(navigableMap.subMap(startRange,true,endRange,true));
            // put the  day of week and average into a map
            String dayOfWeek=DateTimeObj.getDayofWeek(endRange);
            barchartLinkedMap.put(dayOfWeek,average);
        }
        //send average values to barchart
        populateBarChart(barchartLinkedMap);
        // get today's values to be fed to line chart
        navigableMap=navigableMap.subMap(currentDateInMilli-_24Hours,true,currentDateInMilli,true);
        populateLineChart(navigableMap);
    }



    private float computeAverage(NavigableMap<Long, Float> longFloatNavigableMap) {
            float sum=0;
            for(Long key: longFloatNavigableMap.keySet()){
                sum=sum+longFloatNavigableMap.get(key);
            }
            //Actually this is the average
            sum=sum/longFloatNavigableMap.size();
            return sum;
        }




    private void populateLineChart(NavigableMap<Long, Float> navigableMap){
        lineChart.animateXY(1500, 1500);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setDescription("");

        ArrayList<Entry> vals = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        int count=0;
        for(Long timestamp: navigableMap.keySet()){
            Entry c1e1 = new Entry(navigableMap.get(timestamp),count);
            vals.add(c1e1);
            xVals.add("");
            count++;
        }
        LineDataSet setComp1 = new LineDataSet(vals, measuementClassLabel +" "+ measurmentClassMeasurementUnit);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setLineWidth(6.000f);
        setComp1.setDrawValues(false);
        // use the interface ILineDataSet
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);

        LineData lineData = new LineData(xVals, dataSets);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setEnabled(false);

        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        lineChart.setTouchEnabled(false);
        lineChart.invalidate();
    }

    private void populateBarChart(LinkedHashMap<String, Float> barchartLinkedMap) {
        barChart.animateXY(1500, 1500);
        barChart.setBackgroundColor(Color.WHITE);
        barChart.setDescription("");

        ArrayList<BarEntry> vals = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();
        int count=0;
        for(String dayofWeek: barchartLinkedMap.keySet()){
            BarEntry c1e1 = new BarEntry(barchartLinkedMap.get(dayofWeek),count);
            vals.add(c1e1);
            xVals.add(dayofWeek);
            count++;
        }
        BarDataSet setComp1 = new BarDataSet(vals, measuementClassLabel+" "+measurmentClassMeasurementUnit);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setBarSpacePercent(15);



        // use the interface ILineDataSet
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(setComp1);

        BarData barData = new BarData(xVals, dataSets);
        barChart.setData(barData);




        YAxis yAxisR = barChart.getAxisRight();
        yAxisR.setEnabled(false);

        YAxis yAxisL = barChart.getAxisLeft();
        yAxisL.setEnabled(false);

        barChart.setTouchEnabled(false);
        barChart.invalidate();

    }


}
