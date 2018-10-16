package it.polimi.polidemonstrator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.HttpResponseCache;
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
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import it.polimi.polidemonstrator.businesslogic.DateTimeObj;
import it.polimi.polidemonstrator.businesslogic.InternetConnection;
import it.polimi.polidemonstrator.businesslogic.MeasurementClass;

public class Chart_LineChart extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DialogInterface.OnClickListener {

    String JSON_STRING;
    LineChart lineChart;
    HashMap<String,List<String> > hashMapJsonUrlsLineColors;
    List<MeasurementClass> listMeasurementClassesParsed;
    String buildingID,roomID,measurementClassID;
    String SelectedDate;

    ListView listViewMeasurements;
    AlertDialog dialog;
    Button btnRefresh;
    Spinner spinnerTimeSpan;
    DateTimeObj.MeasurementTimeWindow measurementTimeWindow;
    DateTimeObj.TimeIntervals timeIntervals;
    DatePicker datePicker;
    MeasurementClass measurementClass;

    boolean isChartinDetailMode;//determines if watch shows average values of details of each specific sensor



//this file updated by editor2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart__line_chart);

        Bundle gotBasket=getIntent().getExtras();

        listMeasurementClassesParsed=(List<MeasurementClass>)gotBasket.getSerializable("listMeasuremetClasses");
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

        datePicker=(DatePicker)findViewById(R.id.datePickerSelectDate);

        measurementTimeWindow= DateTimeObj.MeasurementTimeWindow.Today;
        timeIntervals= DateTimeObj.TimeIntervals.FifteenMins;

        measurementClass=new MeasurementClass(Chart_LineChart.this);
        hashMapJsonUrlsLineColors=  measurementClass.jsonURL_Generator(measurementClassID, buildingID, roomID, measurementTimeWindow, SelectedDate);

        isChartinDetailMode=false;//at the beginning we want to display average values


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isInternetConnected= InternetConnection.isInternetConnected(Chart_LineChart.this);
                if(isInternetConnected == false){
                    Toast.makeText(Chart_LineChart.this,
                            "There is no internet connection, cached data is displayed!",
                            Toast.LENGTH_SHORT).show();
                }else {
                    boolean refreshCacheData=true;
                    if(isChartinDetailMode==false) {
                        new BackgroudTask(hashMapJsonUrlsLineColors, timeIntervals, measurementTimeWindow, SelectedDate, refreshCacheData).execute();
                    }else{

                        new BackgroundTaskGetMeasurementClassVariables(roomID,measurementClassID,measurementTimeWindow,timeIntervals,refreshCacheData).execute();
                    }
                    Snackbar.make(view, "Fetching data from server...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        buildAlterDialog(listMeasurementClassesParsed);

        new BackgroudTask(hashMapJsonUrlsLineColors,timeIntervals, measurementTimeWindow, SelectedDate, false).execute();
    }

    private void buildAlterDialog(List<MeasurementClass> listMeasurementClassesParsed) {
        ArrayList<MeasurementClass> arrayList = new ArrayList<>();
        for(MeasurementClass item : listMeasurementClassesParsed){
            MeasurementClass msurementClass=new MeasurementClass(Chart_LineChart.this);
            msurementClass.setSensorClasseId(item.getSensorClasseId());
            msurementClass.setSensorClassLabel(item.getSensorClassLabel());
            String[] listViewItem= MeasurementClass.getMeasurementListViewItem(item.getSensorClasseId());
            if (listViewItem != null) {
                msurementClass.setSensorClassLabel(listViewItem[0]);
                msurementClass.setSensorClassImage(Integer.valueOf(listViewItem[1]));
            }
            arrayList.add(msurementClass);
        }
        MeasurementClass.AdapterSensorClasses sensorClassesAdapter=new MeasurementClass.AdapterSensorClasses(Chart_LineChart.this,R.layout.list_singlerow,arrayList);

        listViewMeasurements=new ListView(this);
        listViewMeasurements.setAdapter(sensorClassesAdapter);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Select Measurement");
        builder.setCancelable(true);
        builder.setIcon(R.drawable.pie_chart);
        builder.setNegativeButton(android.R.string.cancel, null); // dismisses by default 
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
                    case ThisMonth:
                        arrayTimeStamp.add(DateTimeObj.getMonthDayTime(arrayListXAxisValues.get(count)));
                        break;
                    case ThisYear:
                        arrayTimeStamp.add(DateTimeObj.getMonthDayTime(arrayListXAxisValues.get(count)));
                        break;
                    case Custom:
                        arrayTimeStamp.add(DateTimeObj.getTime(arrayListXAxisValues.get(count)));
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
        MeasurementClass measurementClass =(MeasurementClass)listViewMeasurements.getItemAtPosition(which);
        measurementClassID= measurementClass.getSensorClasseId();
        hashMapJsonUrlsLineColors=  measurementClass.jsonURL_Generator(measurementClassID, buildingID, roomID, measurementTimeWindow, SelectedDate);
        new BackgroudTask(hashMapJsonUrlsLineColors,timeIntervals, measurementTimeWindow, SelectedDate, false).execute();

        Toast.makeText(Chart_LineChart.this,
                measurementClass.getSensorClasseId()+" "+ measurementClass.getSensorClassLabel(),
                Toast.LENGTH_SHORT).show();
        dialog.cancel();

    }




    public  class BackgroundTaskGetMeasurementClassVariables extends AsyncTask<Void, Void, List<MeasurementClass.ChartLine>> {
        private String roomID;
        private  String measurementClassID;
        private  DateTimeObj.MeasurementTimeWindow measurementTimeWindow;
        private DateTimeObj.TimeIntervals intervals;
        private boolean refreshCacheData;
        public BackgroundTaskGetMeasurementClassVariables(String roomID, String measurementClassID, DateTimeObj.MeasurementTimeWindow measurementTimeWindow, DateTimeObj.TimeIntervals timeIntervals, boolean refreshCacheData) {
            this.roomID=roomID;
            this.measurementClassID=measurementClassID;
            this.measurementTimeWindow=measurementTimeWindow;
            this.intervals=timeIntervals;
            this.refreshCacheData=refreshCacheData;

            isChartinDetailMode=true;
        }


        @Override
        protected List<MeasurementClass.ChartLine> doInBackground(Void... params) {

            String  measurementClassVariablesURL=measurementClass.jsonURL_GeneratorMeasurenetClassVariables(roomID, measurementClassID);
            HashMap<Integer, String[]> parsed_MeasurementClassVariables= MeasurementClass.getListofMeasurementVariables(measurementClassVariablesURL);//String[]==variableDescription,variableUnit,sensorID

            List<String[]> measurementVariableURLsLabelNames=measurementClass.jsonURL_GeneratorMeasurementVariables(parsed_MeasurementClassVariables,measurementTimeWindow);
            // now the measurement variable urls are ready, fetch related json data
            List<MeasurementClass.ChartLine> parsed_MeasurementVariables= MeasurementClass.getListofMeasurementVariableData(measurementVariableURLsLabelNames,Chart_LineChart.this,refreshCacheData);

            return parsed_MeasurementVariables;
        }

        @Override
        protected void onPostExecute(List<MeasurementClass.ChartLine> measurementClassVariablesParsed) {
            super.onPostExecute(measurementClassVariablesParsed);
            if (measurementClassVariablesParsed != null) {
                ArrayList<ILineDataSet> datasets = new ArrayList<>();
                //prepare X values in miliseconds for the x-axis of the chart
                ArrayList<Long> arrayListXAxisValuesInMili = DateTimeObj.getDateTimeMiliRange(measurementTimeWindow, intervals, SelectedDate);
                for (int i = 0; i < measurementClassVariablesParsed.size(); i++) {
                    ArrayList<Entry> arrayListYvalues = addRecordsToChartData(measurementClassVariablesParsed.get(i).getLinexyvalues(), arrayListXAxisValuesInMili, measurementTimeWindow);


                    LineDataSet lineInternalDataset = FillChartArrayListDataSets(arrayListYvalues, measurementClassVariablesParsed.get(i).getLabel(), measurementClassVariablesParsed.get(i).getColor());
                    datasets.add(lineInternalDataset);
                }
                populateLineChart(datasets, arrayTimeStamp);
            } else {
                Toast.makeText(Chart_LineChart.this,
                        "Server is not available, or there is not data to be displayed!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }



    //get internal and External sensor data form API
    public class BackgroudTask extends AsyncTask< Void  ,Void,  HashMap<String,List<String>>> {
        DateTimeObj.TimeIntervals intervals;
        HashMap<String,List<String>> hashMapUrlsColors;
        ArrayList<Long> arrayListXAxisValuesInMili;
        DateTimeObj.MeasurementTimeWindow timeWindow;
        String selectedDate;
        boolean isRefreshCachedData; //this is only related to cached data and is not correlated to changing the time window, etc.


        BackgroudTask(HashMap<String, List<String>> hashMapUrlsColors, DateTimeObj.TimeIntervals intervals, DateTimeObj.MeasurementTimeWindow measurementTimeWindow, String selectedDate, boolean isRefreshCachedData){
            this.intervals=intervals;
            this.hashMapUrlsColors=hashMapUrlsColors;
            this.timeWindow=measurementTimeWindow;
            this.selectedDate=selectedDate;
            this.isRefreshCachedData=isRefreshCachedData;
            isChartinDetailMode=false;//switch to average displaying of the chart
        }

        @Override
        protected void onPreExecute() {
            //prepare X values in miliseconds for the x-axis of the chart
            arrayListXAxisValuesInMili=DateTimeObj.getDateTimeMiliRange(measurementTimeWindow, intervals,selectedDate);
        }

        @Override
        protected HashMap<String,List<String>> doInBackground(Void... params) {

            HashMap<String,List<String>> hashMapJson_results= MeasurementClass.getListofMeasurementClassData(hashMapUrlsColors,isRefreshCachedData);
            //now pars the results that are given by the API
            return  hashMapJson_results;
        }


        @Override
        protected void onPostExecute(HashMap<String,List<String>> hashMapJson_results) {
            ArrayList<ILineDataSet> datasets = new ArrayList<ILineDataSet>();

            if(hashMapJson_results !=null){
                for (Map.Entry<String,List<String>> entry : hashMapJson_results.entrySet()) {//key is the label name of the line
                    LinkedHashMap<Long, Float> hashMapParsedResults = MeasurementClass.parsJSON_Measurement(entry.getValue().get(0));//get(0)is one record of un-parsed json
                    ArrayList<Entry> arrayListYvalues=addRecordsToChartData(hashMapParsedResults, arrayListXAxisValuesInMili, timeWindow);
                    LineDataSet lineInternalDataset=FillChartArrayListDataSets(arrayListYvalues, entry.getKey(), Integer.valueOf(entry.getValue().get(1)));
                    datasets.add(lineInternalDataset);

                    if (measurementClassID.equals("9"))
                    {
                        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

                        LimitLine ll1 = new LimitLine(MeasurementClass.computeMeanMeasurementValue(hashMapParsedResults), "Mean Over Selected Time Window");
                        ll1.setLineWidth(4f);
                        ll1.enableDashedLine(10f, 10f, 0f);
                        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                        ll1.setTextSize(10f);
                        ll1.setTypeface(tf);
                        YAxis leftAxis = lineChart.getAxisLeft();
                        leftAxis.removeAllLimitLines();
                        leftAxis.addLimitLine(ll1);
                        // limit lines are drawn behind data (and not on top)
                        leftAxis.setDrawLimitLinesBehindData(true);

                    }
                }
                populateLineChart(datasets, arrayTimeStamp);
            }
            else {
                Toast.makeText(Chart_LineChart.this,
                        "Server is not available, or there is not data to be displayed!",
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
        boolean refreshCacheData=false;
        new BackgroundTaskGetMeasurementClassVariables(roomID,measurementClassID,measurementTimeWindow,timeIntervals, refreshCacheData).execute();


    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

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
                    timeIntervals= DateTimeObj.TimeIntervals.OneEighthDay;
                    break;
                case 2:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.ThisMonth;
                    timeIntervals= DateTimeObj.TimeIntervals.OneEighthDay;
                    break;
                case 3:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.ThisYear;
                    timeIntervals= DateTimeObj.TimeIntervals.OneEighthDay;
                    break;
                case 4:
                    measurementTimeWindow=DateTimeObj.MeasurementTimeWindow.Custom;
                    timeIntervals= DateTimeObj.TimeIntervals.FifteenMins;
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
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth()+1;
            int selectedDay =  datePicker.getDayOfMonth();
            SelectedDate=selectedYear+"/"+selectedMonth+"/"+selectedDay;

                    hashMapJsonUrlsLineColors= measurementClass.jsonURL_Generator(measurementClassID, buildingID, roomID,
                            measurementTimeWindow,SelectedDate);

                    new BackgroudTask(hashMapJsonUrlsLineColors, timeIntervals,measurementTimeWindow,SelectedDate, false).execute();

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }


}
