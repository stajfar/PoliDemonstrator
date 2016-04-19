package it.polimi.polidemonstrator;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import it.polimi.polidemonstrator.businesslogic.DateTimeObj;

public class SensorChart_LineChart extends AppCompatActivity {

    String JSON_STRING;
    //ruturned json output form API

    LineChart lineChart;
    HashMap<String,List<String> > hashMapJsonUrlsLineColors;
    String startDateTime;
    String endDateTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensorchart_linechart);

        Bundle gotBasket=getIntent().getExtras();
        hashMapJsonUrlsLineColors=(HashMap)gotBasket.getSerializable("hashMapJsonUrls");
        startDateTime=gotBasket.getString("startDateTime");
        endDateTime=gotBasket.getString("endDateTime");



        //json_urlInternal="http://131.175.56.243:8080/measurements/15min/room/"+roomID+"/variableclass/"+sensorClassID+"/2016/04/18";
        //json_urlExternal="http://131.175.56.243:8080/weatherreports/60min/building/"+buildingID+"/2016/04/18?var=airtemperature";



        Button btngetjson=(Button)findViewById(R.id.btngetjson);
        lineChart = (LineChart) findViewById(R.id.chart);

        btngetjson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new BackgroudTask().execute(hashMapJsonUrlsLineColors);

            }
        });
    }



    ArrayList<String> arrayTimeStamp=new ArrayList<String>();

    private ArrayList<Entry> addRecordsToChartData(LinkedHashMap<Long, Float> hashMapParsedResults, ArrayList<Long> arrayListXAxisValues) {
        ArrayList<Entry> arrayChartEntries=new ArrayList<Entry>();
        //clean array lists first of old data
        arrayTimeStamp.clear();



        for(int count=0;count<arrayListXAxisValues.size();count++) {

            if (hashMapParsedResults.containsKey(arrayListXAxisValues.get(count))) {
                Entry EntryInternal = new Entry(hashMapParsedResults.get(arrayListXAxisValues.get(count)), count);
                arrayChartEntries.add(EntryInternal);
            }

            arrayTimeStamp.add(DateTimeObj.getTime(arrayListXAxisValues.get(count)));
        }

        return arrayChartEntries;

    }

    private LinkedHashMap<Long,Float> parsJSON(String json_results) {
        try {
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            float value;
            long timestamp;
            LinkedHashMap<Long,Float> hashMapParsedResult=new LinkedHashMap<Long,Float>();

            while (count< jsonArray.length())
            {
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                if (jsonObject.getString("value") != "null") {
                    value = Float.valueOf(jsonObject.getString("value"));
                    timestamp = Long.valueOf(jsonObject.getString("timestamp"));
                    hashMapParsedResult.put(timestamp, value);
                }
                count++;
            }
            return hashMapParsedResult;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }




    private void populateLineChart(ArrayList<ILineDataSet> lineChartDatasets, ArrayList<String> arrayTimeStamp) {
        LineData lineData = new LineData(arrayTimeStamp, lineChartDatasets);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }



    private LineDataSet FillChartArrayListDataSets(ArrayList<Entry> arrayListYvalues, String charLineLabel, int color) {
        LineDataSet linedataSet = new LineDataSet(arrayListYvalues, charLineLabel);


        //config the appearance of the Internal chart line
        linedataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        linedataSet.setColor(color);
        linedataSet.setCircleColor(Color.WHITE);
        linedataSet.setLineWidth(2f);
        linedataSet.setCircleRadius(3f);
        linedataSet.setFillAlpha(65);
        linedataSet.setFillColor(ColorTemplate.getHoloBlue());
        linedataSet.setHighLightColor(Color.rgb(244, 117, 117));
        linedataSet.setDrawCircleHole(false);


        return linedataSet;
    }


    //get internal and External sensor data form API
    public class BackgroudTask extends AsyncTask<  HashMap<String,List<String>>  ,Void,  HashMap<String,List<String>>>{
        @Override
        protected void onPreExecute() {
            //json_url="http://131.175.56.243:8080/measurements/60min/sensor/variable/8/2016/04/01";
        }

        @Override
        protected HashMap<String,List<String>> doInBackground(HashMap<String,List<String>>... params) {
            try {
                HashMap<String,List<String>> hashMapUrlsColors=params[0];
                HashMap<String,List<String>> hashMapJson_results=new HashMap<>();

                //fetch  data from JSON API
                for (Map.Entry<String,List<String>> entry : hashMapUrlsColors.entrySet()){
                    List<String> listUrlColor=new ArrayList<>();
                    URL url=new URL(entry.getValue().get(0));
                    HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
                    InputStream inputStream=httpconnection.getInputStream();
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder=new StringBuilder();
                    while ((JSON_STRING = bufferedReader.readLine()) != null)
                    {
                        stringBuilder.append(JSON_STRING+"\n");
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpconnection.disconnect();

                    listUrlColor.add(stringBuilder.toString().trim());
                    listUrlColor.add(entry.getValue().get(1));//this will sepecifies the color of the line

                   hashMapJson_results.put(entry.getKey(), listUrlColor);
                }
                return hashMapJson_results;

            } catch (MalformedURLException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(HashMap<String,List<String>> hashMapJson_results) {

           if (hashMapJson_results != null){
               //fill the x-Axix array list by times
               ArrayList<Long> arrayListXAxisValues=DateTimeObj.getDateTimeMiliRange(startDateTime, endDateTime, DateTimeObj.TimeIntervals.FifteenMins);
               ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
               for (Map.Entry<String,List<String>> entry : hashMapJson_results.entrySet()){
                   LinkedHashMap<Long,Float> hashMapParsedResults=parsJSON(entry.getValue().get(0));
                   ArrayList<Entry> arrayChartEntries=addRecordsToChartData(hashMapParsedResults, arrayListXAxisValues);
                   LineDataSet lineInternalDataset=FillChartArrayListDataSets(arrayChartEntries, entry.getKey(), Integer.valueOf(entry.getValue().get(1)));
                   datasets.add(lineInternalDataset);

               }

               populateLineChart(datasets, arrayTimeStamp);
           }else {
               Toast.makeText(SensorChart_LineChart.this,
                       "Sorry, server is not Available,\n please try again!",
                       Toast.LENGTH_SHORT).show();
           }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }
    }



}
