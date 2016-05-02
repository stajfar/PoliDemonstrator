package it.polimi.polidemonstrator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

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
import it.polimi.polidemonstrator.businesslogic.MesurementClass;

public class Chart_LineChart extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnClickListener {

    String JSON_STRING;
    LineChart lineChart;
    HashMap<String,List<String> > hashMapJsonUrlsLineColors;
    HashMap<String, String> hashMapMeasurementClassesParsed;
    String buildingID,roomID,measurementClassID;

    ListView listViewMeasurements;
    AlertDialog dialog;
    Button btnRefresh;
    Spinner spinnerTimeSpan;
    DateTimeObj.MeasurementTimeWindow measurementTimeWindow;
    DateTimeObj.TimeIntervals timeIntervals;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart__line_chart);

        Bundle gotBasket=getIntent().getExtras();

        hashMapMeasurementClassesParsed=(HashMap)gotBasket.getSerializable("hashMapMeasuremetClasses");
        buildingID=gotBasket.getString("buildingID");
        roomID=gotBasket.getString("roomID");
        measurementClassID=gotBasket.getString("measuermentClassID");




        lineChart = (LineChart) findViewById(R.id.chart);
        lineChart.animateXY(2500, 2500);
        lineChart.setBackgroundColor(Color.LTGRAY);
        lineChart.setPinchZoom(true);
        lineChart.setDescription("");
        lineChart.setNoDataTextDescription("Refreshing Data form Server");

        btnRefresh=(Button)findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(new btnRefreshOnclick());

        spinnerTimeSpan=(Spinner)findViewById(R.id.spinnerTimeSpan);
        spinnerTimeSpan.setOnItemSelectedListener(new spinnerTimeSpanSelectedListener());

        measurementTimeWindow= DateTimeObj.MeasurementTimeWindow.Today;
        timeIntervals= DateTimeObj.TimeIntervals.FifteenMins;
        hashMapJsonUrlsLineColors=  MesurementClass.jsonURL_Generator(measurementClassID, buildingID, roomID, measurementTimeWindow);
        new BackgroudTask(hashMapJsonUrlsLineColors,timeIntervals).execute();







        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BackgroudTask(hashMapJsonUrlsLineColors,timeIntervals).execute();
                Snackbar.make(view, "Fetching data from server...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        buildAlterDialog(hashMapMeasurementClassesParsed);






    }

    private void buildAlterDialog(HashMap<String, String> hashMapMeasurementClassesParsed) {
        ArrayList<MesurementClass> arrayList = new ArrayList<>();
        for (Map.Entry<String,String> entry : hashMapMeasurementClassesParsed.entrySet()){
            MesurementClass measurementClass=new MesurementClass(Chart_LineChart.this);
            measurementClass.setSensorClasseId(entry.getKey());
            measurementClass.setSensorClassLabel(entry.getValue());
            String[] listViewItem=MesurementClass.getMeasurementListViewItem(entry.getKey());
            if (listViewItem != null) {
                measurementClass.setSensorClassLabel(listViewItem[0]);
                measurementClass.setSensorClassImage(Integer.valueOf(listViewItem[1]));
            }
            arrayList.add(measurementClass);
        }
        MesurementClass.AdapterSensorClasses sensorClassesAdapter=new MesurementClass.AdapterSensorClasses(Chart_LineChart.this,R.layout.list_singlerow,arrayList);

        listViewMeasurements=new ListView(this);
        listViewMeasurements.setAdapter(sensorClassesAdapter);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Select Measurement");

        builder.setSingleChoiceItems(sensorClassesAdapter, -1, this);

        dialog=builder.create();

    }


    ArrayList<String> arrayTimeStamp=new ArrayList<String>();

    private ArrayList<Entry> addRecordsToChartData(LinkedHashMap<Long, Float> hashMapParsedResults, ArrayList<Long> arrayListXAxisValues, DateTimeObj.MeasurementTimeWindow timeWindow) {
        ArrayList<Entry> arrayChartEntries=new ArrayList<Entry>();
        //clean array lists first of old data
        arrayTimeStamp.clear();

        for(int count=0;count<arrayListXAxisValues.size();count++) {

            if (hashMapParsedResults.containsKey(arrayListXAxisValues.get(count))) {
                Entry EntryInternal = new Entry(hashMapParsedResults.get(arrayListXAxisValues.get(count)), count);
                arrayChartEntries.add(EntryInternal);
            }
                switch (timeWindow){
                    case Today:
                        arrayTimeStamp.add(DateTimeObj.getTime(arrayListXAxisValues.get(count)));
                        break;
                    case Last7days:
                        arrayTimeStamp.add(DateTimeObj.getMonthDayTime(arrayListXAxisValues.get(count)));
                        break;

                }

        }

        return arrayChartEntries;

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



    @Override
    public void onClick(DialogInterface dialog, int which) {
        MesurementClass mesurementClass=(MesurementClass)listViewMeasurements.getItemAtPosition(which);

        measurementClassID=mesurementClass.getSensorClasseId();
        hashMapJsonUrlsLineColors=  MesurementClass.jsonURL_Generator(measurementClassID, buildingID, roomID, measurementTimeWindow);
        new BackgroudTask(hashMapJsonUrlsLineColors,timeIntervals).execute();

        Toast.makeText(Chart_LineChart.this,
                mesurementClass.getSensorClasseId()+" "+mesurementClass.getSensorClassLabel(),
                Toast.LENGTH_SHORT).show();
        dialog.cancel();



    }




    public  class BackgroundTaskGetMeasurementClassVariables extends AsyncTask<Void, Void, List<LinkedHashMap<Long, Float>>> {
        private String roomID;
        private  String measurementClassID;
        private  DateTimeObj.MeasurementTimeWindow measurementTimeWindow;
        private DateTimeObj.TimeIntervals intervals;
        public BackgroundTaskGetMeasurementClassVariables(String roomID, String measurementClassID, DateTimeObj.MeasurementTimeWindow measurementTimeWindow, DateTimeObj.TimeIntervals timeIntervals) {
            this.roomID=roomID;
            this.measurementClassID=measurementClassID;
            this.measurementTimeWindow=measurementTimeWindow;
            this.intervals=timeIntervals;
        }


        @Override
        protected List<LinkedHashMap<Long, Float>> doInBackground(Void... params) {
            String  measurementClassVariablesURL=MesurementClass.jsonURL_GeneratorMeasurenetClassVariables(roomID, measurementClassID);
            HashMap<Integer, String[]> parsed_MeasurementClassVariables=MesurementClass.getListofMeasurementVariables(measurementClassVariablesURL);

            List<String> measurementVariableURLs=MesurementClass.jsonURL_GeneratorMeasurementVariables(parsed_MeasurementClassVariables,measurementTimeWindow);
            // now the measurement variable urls are ready, fetch related json data
            List<LinkedHashMap<Long,Float>> parsed_MeasurementVariables=MesurementClass.getListofMeasurementVariableData(measurementVariableURLs);


            return parsed_MeasurementVariables;
        }

        @Override
        protected void onPostExecute(List<LinkedHashMap<Long, Float>> measurementClassVariablesParsed) {
            super.onPostExecute(measurementClassVariablesParsed);

            ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
            //prepare X values in miliseconds for the x-axis of the chart
            ArrayList<Long> arrayListXAxisValuesInMili = DateTimeObj.getDateTimeMiliRange(measurementTimeWindow, intervals);
            for(int i=0;i<measurementClassVariablesParsed.size();i++)
            {
                ArrayList<Entry> arrayListYvalues=addRecordsToChartData(measurementClassVariablesParsed.get(i), arrayListXAxisValuesInMili, measurementTimeWindow);
                LineDataSet lineInternalDataset=FillChartArrayListDataSets(arrayListYvalues, "lable1"+i,Color.BLUE);
                datasets.add(lineInternalDataset);
            }
            populateLineChart(datasets, arrayTimeStamp);
        }

    }



    //get internal and External sensor data form API
    public class BackgroudTask extends AsyncTask< Void  ,Void,  HashMap<String,List<String>>> {
        DateTimeObj.TimeIntervals intervals;
        HashMap<String,List<String>> hashMapUrlsColors2;
        ArrayList<Long> arrayListXAxisValuesInMili;


        BackgroudTask(HashMap<String, List<String>> hashMapUrlsColors, DateTimeObj.TimeIntervals intervals){
            this.intervals=intervals;
            this.hashMapUrlsColors2=hashMapUrlsColors;
        }

        @Override
        protected void onPreExecute() {
            //prepare X values in miliseconds for the x-axis of the chart
            arrayListXAxisValuesInMili=DateTimeObj.getDateTimeMiliRange(measurementTimeWindow, intervals);
        }

        @Override
        protected HashMap<String,List<String>> doInBackground(Void... params) {
            try {
                HashMap<String,List<String>> hashMapUrlsColors=hashMapUrlsColors2;
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
                manageAsyncTaskResults(hashMapJson_results,arrayListXAxisValuesInMili,measurementTimeWindow);

            }else {
                Toast.makeText(Chart_LineChart.this,
                        "Sorry, server is not Available,\n please try again!",
                        Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_about) {
            return true;

        } else if (id == R.id.action_MeasureemntSearch) {
            //show a list view dialog to user to select another measurement
            dialog.show();
        }else if (id == R.id.action_MeasureemntDetails){
            //display the activity that shows the details of a specific measurement
            displaySelectedMeasurementDetails();
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedMeasurementDetails() {

        new BackgroundTaskGetMeasurementClassVariables(roomID,measurementClassID,measurementTimeWindow,timeIntervals).execute();


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        /*Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //this event happens when user changes the Time period on the drawer's spinner
    private class spinnerTimeSpanSelectedListener implements android.widget.AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            RelativeLayout layout=(RelativeLayout)findViewById(R.id.relativeLayoutDatePickers);
            if (position== 4){
                //make the date picker section visible inside the Drawer
                layout.setVisibility(View.VISIBLE);
            }else{
                layout.setVisibility(View.GONE);
            }
            switch (position){
                case 0:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.Today;
                    timeIntervals= DateTimeObj.TimeIntervals.FifteenMins;
                    break;
                case 1:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.Last7days;
                    timeIntervals= DateTimeObj.TimeIntervals.HalfaDay;
                    break;
                case 2:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.ThisMonth;
                    timeIntervals= DateTimeObj.TimeIntervals.HalfaDay;
                    break;
                case 3:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.ThisYear;
                    timeIntervals= DateTimeObj.TimeIntervals.OneDay;
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class btnRefreshOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {

                    hashMapJsonUrlsLineColors= MesurementClass.jsonURL_Generator(measurementClassID, buildingID, roomID,
                            measurementTimeWindow);

                    new BackgroudTask(hashMapJsonUrlsLineColors, timeIntervals).execute();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }
    }

    private void manageAsyncTaskResults(HashMap<String, List<String>> hashMapJson_results, ArrayList<Long> arrayListXAxisValuesInMili, DateTimeObj.MeasurementTimeWindow timeWindow) {
        ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();
        for (Map.Entry<String,List<String>> entry : hashMapJson_results.entrySet()){
            LinkedHashMap<Long,Float> hashMapParsedResults=MesurementClass.parsJSON_Measurement(entry.getValue().get(0));

            ArrayList<Entry> arrayListYvalues=addRecordsToChartData(hashMapParsedResults, arrayListXAxisValuesInMili, timeWindow);
            LineDataSet lineInternalDataset=FillChartArrayListDataSets(arrayListYvalues, entry.getKey(), Integer.valueOf(entry.getValue().get(1)));
            datasets.add(lineInternalDataset);
        }

        populateLineChart(datasets, arrayTimeStamp);
    }
}
