package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import it.polimi.polidemonstrator.MyApplication;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 4/26/2016.
 */
public class MeasurementClass implements Serializable {

    private static String serverURL;
    //correlated to sensor classes
    private String SensorClasseId;
    private String sensorClassLabel;
    private int sensorClassImage;
    private String sensorClassSensorLatestValue;
    private Context context;

    private String sensorVariableLabel;
    private List<Integer> listSensorVariable;

    public List<Integer> getListSensorVariable() {
        return listSensorVariable;
    }

    public void setListSensorVariable(List<Integer> listSensorVariable) {
        this.listSensorVariable = listSensorVariable;
    }

    public String getSensorVariableLabel() {
        return sensorVariableLabel;
    }

    public void setSensorVariableLabel(String sensorVariableLabel) {
        this.sensorVariableLabel = sensorVariableLabel;
    }



    public MeasurementClass() {
    }

    public int getSensorClassImage() {
        return sensorClassImage;
    }

    public void setSensorClassImage(int sensorClassImage) {
        this.sensorClassImage = sensorClassImage;
    }

    public String getSensorClassSensorLatestValue() {
        return sensorClassSensorLatestValue;
    }

    public void setSensorClassSensorLatestValue(String sensorClassSensorLatestValue) {
        this.sensorClassSensorLatestValue = sensorClassSensorLatestValue;
    }

    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        MeasurementClass.serverURL = serverURL;
    }




    public MeasurementClass(Context context) {
        this.context=context;
        final MyApplication myApplication=(MyApplication)context.getApplicationContext();
        this.serverURL= myApplication.getJsonServerURL();
    }



    public String getSensorClassLabel() {
        return sensorClassLabel;
    }

    public void setSensorClassLabel(String sensorClassLabel) {
        this.sensorClassLabel = sensorClassLabel;
    }

    public String getSensorClasseId() {
        return SensorClasseId;
    }

    public void setSensorClasseId(String sensorClasseId) {
        SensorClasseId = sensorClasseId;
    }


    public  HashMap<String,List<String>> jsonURL_Generator(String sensorClassID, String buildingID, String roomID, DateTimeObj.MeasurementTimeWindow timeWindow, String selectedDate) {

        HashMap<String,List<String>> hashMapJson_Urls=new HashMap<>();
        List<String> UrlsColorsInternal=new ArrayList<String>();
        List<String> UrlsColorsExternal=new ArrayList<String>();
        switch (timeWindow){
            case  Today:
                switch (sensorClassID) {
                    case "1":
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/x9ng");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + "/" + DateTimeObj.getCurrentDate() + "?var=airtemperature");
                        //UrlsColorsExternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Temperature (C)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Temperature (C)", UrlsColorsExternal);
                        break;
                    case "2":
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/x9ng");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                       // UrlsColorsExternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + "/" + DateTimeObj.getCurrentDate() + "?var=relativehumidity");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Humidity (%)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Humidity (%)", UrlsColorsExternal);

                        break;
                    case "3":
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                        // UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Luminosity", UrlsColorsInternal);
                        break;

                    case "4":
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                       // UrlsColorsInternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Internal CO2 (V)", UrlsColorsInternal);
                        break;
                    case "5":
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                        // UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Smart Plug", UrlsColorsInternal);
                        break;
                    case "9"://power consumption
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/29y74");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Power Consumption (W)", UrlsColorsInternal);
                        break;

                }
                break;
            case Last7days:
                switch (sensorClassID) {
                    case "1":
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/x9ng");
                        UrlsColorsInternal.add(serverURL+"/measurements/60min/room/"+roomID+"/variableclass/"+sensorClassID+"/"+ DateTimeObj.getCurrentDate()+"?weekly=true");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        //UrlsColorsExternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsExternal.add(serverURL+"/weatherreports/60min/building/"+buildingID+"/" + DateTimeObj.getCurrentDate() + "?var=airtemperature&weekly=true");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Temperature (C)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Temperature (C)", UrlsColorsExternal);
                        break;
                    case "2":
                        // UrlsColorsInternal.add("");
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate() + "?weekly=true");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        // UrlsColorsExternal.add("");
                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + "/" + DateTimeObj.getCurrentDate() + "?var=relativehumidity&weekly=true");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Humidity (%)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Humidity (%)", UrlsColorsExternal);

                        break;
                    case "3":
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate() + "?weekly=true");
                        // UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Luminosity", UrlsColorsInternal);
                        break;
                    case "4":
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate() + "?weekly=true");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Internal CO2 (V)", UrlsColorsInternal);
                        break;
                    case "5"://office Smart plug
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate() + "?weekly=true");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Smart Plug", UrlsColorsInternal);
                        break;
                    case "9"://power consumption
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate() + "?weekly=true");
                        // UrlsColorsInternal.add("");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Power Consumption (W)", UrlsColorsInternal);
                        break;

                }
                break;
            case ThisMonth:
                String commonStringServerUrl=serverURL + "/measurements/60min/room/" + roomID + "/variableclass/";
                String commonStringDates= "/" + DateTimeObj.getCurrentYear()+"/"+DateTimeObj.getCurrentMonth();
                switch (sensorClassID) {
                    case "1"://temperature
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + commonStringDates + "?var=airtemperature");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Temperature (C)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Temperature (C)", UrlsColorsExternal);
                        break;
                    case "2"://humidity
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + commonStringDates + "?var=relativehumidity");
                        //UrlsColorsExternal.add("https://api.myjson.com/bins/4vsnu");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Humidity (%)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Humidity (%)", UrlsColorsExternal);
                        break;
                    case "3"://limunisity
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Luminosity", UrlsColorsInternal);
                        break;
                    case "4"://co2
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Internal CO2 (V)", UrlsColorsInternal);
                        break;
                    case "9"://power consumption
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Power Consumption (W)", UrlsColorsInternal);
                        break;
                }
                break;
            case ThisYear:
                commonStringServerUrl=serverURL + "/measurements/60min/room/" + roomID + "/variableclass/";
                commonStringDates= "/" + DateTimeObj.getCurrentYear();
                switch (sensorClassID) {
                    case "1"://temperature
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + commonStringDates + "?var=airtemperature");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Temperature (C)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Temperature (C)", UrlsColorsExternal);
                        break;
                    case "2"://humidity
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + commonStringDates + "?var=relativehumidity");
                        //UrlsColorsExternal.add("https://api.myjson.com/bins/4vsnu");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Humidity (%)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Humidity (%)", UrlsColorsExternal);
                        break;
                    case "3"://limunisity
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Luminosity", UrlsColorsInternal);
                        break;
                    case "4"://co2
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Internal CO2 (V)", UrlsColorsInternal);
                        break;
                    case "9"://power consumption
                        UrlsColorsInternal.add(commonStringServerUrl+ sensorClassID +commonStringDates);
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Power Consumption (W)", UrlsColorsInternal);
                        break;
                }
                break;
            case  Custom:
                switch (sensorClassID) {
                    case "1":
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + selectedDate);
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/x9ng");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + "/" + selectedDate + "?var=airtemperature");
                        //UrlsColorsExternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Temperature (C)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Temperature (C)", UrlsColorsExternal);
                        break;
                    case "2":
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + selectedDate);
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/x9ng");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

                        // UrlsColorsExternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsExternal.add(serverURL + "/weatherreports/60min/building/" + buildingID + "/" + selectedDate + "?var=relativehumidity");
                        UrlsColorsExternal.add(String.valueOf(Color.RED));

                        hashMapJson_Urls.put("Average Internal Humidity (%)", UrlsColorsInternal);
                        hashMapJson_Urls.put("External Humidity (%)", UrlsColorsExternal);

                        break;
                    case "3":
                        UrlsColorsInternal.add(serverURL + "/measurements/60min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + selectedDate);
                        // UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Luminosity", UrlsColorsInternal);
                        break;

                    case "4":
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + selectedDate);
                        // UrlsColorsInternal.add("https://api.myjson.com/bins/55g1o");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Internal CO2 (V)", UrlsColorsInternal);
                        break;

                    case "9"://power consumption
                        UrlsColorsInternal.add(serverURL + "/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + selectedDate);
                        //UrlsColorsInternal.add("https://api.myjson.com/bins/29y74");
                        UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                        hashMapJson_Urls.put("Average Power Consumption (W)", UrlsColorsInternal);
                        break;

                }
                break;
        }
        return hashMapJson_Urls;
    }


    private static final HashMap<String, String[]> measurementLookUpTable = new HashMap<String,String[]>() {{
        put("1", new String[]{"Temperature",String.valueOf(R.drawable.ic_temperature),"°C"});
        put("2", new String[]{"Humidity", String.valueOf(R.drawable.ic_humidity),"%"});
        put("3", new String[]{"Luminosity", String.valueOf(R.drawable.ic_luminosity)," "});
        put("4", new String[]{"CO2", String.valueOf(R.drawable.ic_co2),"V"});
        put("5", new String[]{"Current", String.valueOf(R.drawable.ic_information)," "});
        put("6", new String[]{"Active Power", String.valueOf(R.drawable.ic_active_power)," "});
        put("7", new String[]{"adb Sensors", String.valueOf(R.drawable.ic_information)," "});
        put("8", new String[]{"??", String.valueOf(R.drawable.ic_information)," "});
        put("9",new String[]{"Power Cons.",String.valueOf(R.drawable.ic_powercunsumption),"W"});
        put("10", new String[]{"Active Energy Meter", String.valueOf(R.drawable.ic_active_energy_meter)," "});
    }};
    public static String[] getMeasurementListViewItem(String measurementId) {
        String[] measurementResource;
        try {
            measurementResource=measurementLookUpTable.get(measurementId);
        }catch (Exception e)
        {
            measurementResource=null;
        }
        return measurementResource;
    }

    public  String jsonURL_GeneratorMeasurenetClassVariables(String roomID, String measurementClassID) {
        return serverURL+"/variables/room/"+roomID+"/variableclass/"+measurementClassID;
    }

    public static HashMap<Integer, String[]> getListofMeasurementVariables(String measurementClassVariablesURL) {
        String JSON_STRING;
        try {
            URL url = new URL(measurementClassVariablesURL);
            HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
            int maxStale = 60 * 60*24*28 ; // tolerate 4week stale
            httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
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


            String json_String=stringBuilder.toString().trim();
            //now the results are ready and we have to pars the Json results
            HashMap<Integer,String[]> parsed_MeasurementClassVariables=parsJSON_MeasurementClassVariables(json_String);//String[]==variableDescription,variableUnit,sensorID
            return parsed_MeasurementClassVariables;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HashMap<Integer, String[]> parsJSON_MeasurementClassVariables(String json_results) {

        try {
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            int variableID;

            String variableDescription;
            String variableUnit;
            String sensorID;

            String isVariableIndoor;
            HashMap<Integer, String[]> hashMapParsedResult=new HashMap<>();

            while (count< jsonArray.length())            {
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                JSONObject jsonSubObject=jsonObject.getJSONObject("fksensor");
                variableID = jsonObject.getInt("variableid");

                variableDescription=jsonObject.getString("description");
                variableUnit=jsonObject.getString("measure");
                sensorID=jsonSubObject.getString("sensorid");

                hashMapParsedResult.put(variableID,new String[]{variableDescription,variableUnit,sensorID});
                count++;
            }
            return hashMapParsedResult;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  List<String[]> jsonURL_GeneratorMeasurementVariables(HashMap<Integer, String[]> parsed_measurementClassVariables,
                                                                      DateTimeObj.MeasurementTimeWindow measurementTimeWindow) {
        List<String[]> jsonURL_MeasurementVariables=new ArrayList<>();
        switch(measurementTimeWindow){
            case Today:
                for (Map.Entry<Integer, String[]> entry : parsed_measurementClassVariables.entrySet()){
                    String url=serverURL+"/measurements/15min/sensor/variable/"+entry.getKey()+"/"+DateTimeObj.getCurrentDate();
                    jsonURL_MeasurementVariables.add(new String[]{url,entry.getValue()[0],entry.getValue()[1],entry.getValue()[2]});//entry.getValue()[0] --> indexes 0==description 1==measure Unit 2==sensorid
                }
                break;
            case Last7days:
                for (Map.Entry<Integer, String[]> entry : parsed_measurementClassVariables.entrySet()){
                    String url=serverURL+"/measurements/60min/sensor/variable/"+entry.getKey()+"/"+DateTimeObj.getCurrentDate()+"?weekly=true";
                    jsonURL_MeasurementVariables.add(new String[]{url,entry.getValue()[0],entry.getValue()[1],entry.getValue()[2]});
                }
                break;
            case ThisMonth:
                for (Map.Entry<Integer, String[]> entry : parsed_measurementClassVariables.entrySet()){
                    String url=serverURL+"/measurements/60min/sensor/variable/"+entry.getKey()+"/"+DateTimeObj.getCurrentYear()+"/"+DateTimeObj.getCurrentMonth();
                    jsonURL_MeasurementVariables.add(new String[]{url,entry.getValue()[0],entry.getValue()[1],entry.getValue()[2]});
                }
                break;
            case ThisYear:
                for (Map.Entry<Integer, String[]> entry : parsed_measurementClassVariables.entrySet()){
                    String url=serverURL+"/measurements/60min/sensor/variable/"+entry.getKey()+"/"+DateTimeObj.getCurrentYear();
                    jsonURL_MeasurementVariables.add(new String[]{url,entry.getValue()[0],entry.getValue()[1],entry.getValue()[2]});
                }
                break;
            case Custom:
                for (Map.Entry<Integer, String[]> entry : parsed_measurementClassVariables.entrySet()){
                    String url=serverURL+"/measurements/60min/sensor/variable/"+entry.getKey()+"/"+DateTimeObj.getCurrentYear();
                    jsonURL_MeasurementVariables.add(new String[]{url,entry.getValue()[0],entry.getValue()[1],entry.getValue()[2]});
                }
                break;
        }
       return jsonURL_MeasurementVariables;
    }

    public static List<ChartLine> getListofMeasurementVariableData(List<String[]> measurementVariableURLs, Context context, boolean isrefreshCacheData) {
        //fetch  data from JSON API
        List<ChartLine> listJsonData=new ArrayList<>();
        String JSON_STRING;
        for (int i=0;i<measurementVariableURLs.size();i++){

            try {
                 URL  url = new URL(measurementVariableURLs.get(i)[0]);//the first index of array is the url
                HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
                if (isrefreshCacheData==true) {
                    httpconnection.setUseCaches(false);
                }
                int maxStale = 60 * 45 ; // tolerate 45 minutes stale
                httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
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

                LinkedHashMap<Long,Float> hashMapParsedResult=  parsJSON_Measurement(stringBuilder.toString().trim());
                Map<String,String> chartLables=getLineChartLabels(context);
                String measurementDescription=get3FirstWordsofDescription(measurementVariableURLs.get(i)[1]);//[1] is the italian measurement description
                String measurementUnit=measurementVariableURLs.get(i)[2];
                String measurementSensorID=measurementVariableURLs.get(i)[3];

                String measurementCompleteLabel="("+measurementSensorID+")"+chartLables.get(measurementDescription)+"("+measurementUnit+")";

                ChartLine chartLine=new ChartLine(geMeasurementColor(context, i),measurementCompleteLabel,hashMapParsedResult);//color,line-label,json-output-parsed
                listJsonData.add(chartLine);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return listJsonData;
    }

    private static String get3FirstWordsofDescription(String description) {
        String harvestedDescription;
        String[] splittedItem = description.split(" ");
        harvestedDescription=splittedItem[0]+" "+splittedItem[1]+" "+splittedItem[2];
        return harvestedDescription;
    }

    public static HashMap<String, List<String>> getListofMeasurementClassData(HashMap<String, List<String>> hashMapUrlsColors, boolean isRefreshCachedData) {
        String JSON_STRING;
        try {
            HashMap<String,List<String>> hashMapJson_results=new HashMap<>();

            //fetch  data from JSON API
            for (Map.Entry<String,List<String>> entry : hashMapUrlsColors.entrySet()){
                List<String> listjsonColor=new ArrayList<>();
                URL url=new URL(entry.getValue().get(0));
                HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
                if (isRefreshCachedData==true) {
                    httpconnection.setUseCaches(false);
                }
                int maxStale = 60 * 45 ; // tolerate 45 minutes stale
                httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
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

                listjsonColor.add(stringBuilder.toString().trim());
                listjsonColor.add(entry.getValue().get(1));//this will sepecifies the color of the line

                hashMapJson_results.put(entry.getKey(), listjsonColor); //key is the line lable, listUrl color conatains Json outpt and the color of line
            }

            return hashMapJson_results;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int geMeasurementColor(Context context, int i) {
        int[] rainbow = context.getResources().getIntArray(R.array.ChartColors);
        if (i> rainbow.length){i=i%rainbow.length;}
           return rainbow[i];
    }

    public static Float computeMeanMeasurementValue(LinkedHashMap<Long, Float> hashMapParsedResults) {
        Float sum=0f;
        for (Map.Entry<Long, Float> entry : hashMapParsedResults.entrySet()){
            sum=sum+entry.getValue();
        }
        return sum/hashMapParsedResults.size();
    }

    public static List<MeasurementClass> getMeasurementlatestValues(List<MeasurementClass> resultsParsed, String roomid, boolean isRefresh) {

        for(MeasurementClass meaurementClassItem : resultsParsed){
            //json Query for each measurement class
            String measurementLatestValue=getMeasurementLatestValue(roomid,meaurementClassItem.getSensorClasseId(),isRefresh);
            meaurementClassItem.setSensorClassSensorLatestValue(measurementLatestValue);

        }
        return resultsParsed;
    }

    private static String getMeasurementLatestValue(String roomid, String measurementClassKey,boolean isRefresh) {
        String JSON_STRING;

        String[] startEndHours= DateTimeObj.getTimeRangeForTwoHours();
        String measurementClassVariablesURL=serverURL+"/measurements/60min/room/"+roomid+"/variableclass/"+measurementClassKey+"/"+
                DateTimeObj.getCurrentDate()+"?from="+startEndHours[0]+":00&to="+startEndHours[1]+":00";
        try {
            URL url = new URL(measurementClassVariablesURL);
            HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
            if (isRefresh==true) {
                httpconnection.setUseCaches(false);


            }
                int maxStale = 60 * 60; // tolerate 60 minutes stale
                httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);



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

            String json_String=stringBuilder.toString().trim();
            LinkedHashMap<Long,Float> measurementData=parsJSON_Measurement(json_String);

            List<Map.Entry<Long,Float>> entryList = new ArrayList<>(measurementData.entrySet());
            if (entryList.size() > 0) {
                Map.Entry<Long, Float> lastEntry = entryList.get(entryList.size() - 1);
                return lastEntry.getValue().toString();
            }else {
                return "";
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //SpinAddapter for room sensor classes
    //A custom adapter for spinner which makes it more flexible to work with complex data types
    public static class SpinAdapterSensorClasses extends ArrayAdapter<MeasurementClass> {
        private Context context;


        private List<MeasurementClass> measurementClasses;

        public SpinAdapterSensorClasses(Context context, int resource, List<MeasurementClass> measurementClasses) {
            super(context, resource, measurementClasses);
            this.context = context;
            this.measurementClasses = measurementClasses;

        }

        public int getCount(){
            return measurementClasses.size();
        }

        public MeasurementClass getItem(int position){
            return measurementClasses.get(position);
        }

        public long getItemId(int position){
            return position;
        }



        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
           // label.setText(measurementClasses.get(position).getSensorClassLabel());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(measurementClasses.get(position).getSensorClassLabel());

            return label;
        }

    }



    public static class AdapterSensorClasses extends ArrayAdapter<MeasurementClass> {
        private Context context;
        private int resource;

        private List<MeasurementClass> measurementClasses;

        public AdapterSensorClasses(Context context, int resource, List<MeasurementClass> measurementClasses) {
            super(context, resource, measurementClasses);
            this.context = context;
            this.measurementClasses = measurementClasses;
            this.resource=resource;

        }

        public int getCount(){
            return measurementClasses.size();
        }

        public MeasurementClass getItem(int position){
            return measurementClasses.get(position);
        }

        public long getItemId(int position){
            return position;
        }



        // This is for the "passive" state of the spinner
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listView_row= inflater.inflate(resource,parent,false);
            ImageView imageView=(ImageView)listView_row.findViewById(R.id.imageViewlistItem);
            TextView textViewSensorClass=(TextView)listView_row.findViewById(R.id.tvListSensorClass);
            TextView textViewSensorLatestValue=(TextView)listView_row.findViewById(R.id.tvListSensorLatestValue);

            textViewSensorClass.setText(measurementClasses.get(position).getSensorClassLabel());
            textViewSensorLatestValue.setText(measurementClasses.get(position).getSensorClassSensorLatestValue());
            imageView.setImageResource(measurementClasses.get(position).getSensorClassImage());
            // And finally return your dynamic (or custom) view for each spinner item
            return listView_row;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listView_row= inflater.inflate(resource,parent,false);
            ImageView imageView=(ImageView)listView_row.findViewById(R.id.imageViewlistItem);
            TextView textViewSensorClass=(TextView)listView_row.findViewById(R.id.tvListSensorClass);
            TextView textViewSensorLatestValue=(TextView)listView_row.findViewById(R.id.tvListSensorLatestValue);

            textViewSensorClass.setText(measurementClasses.get(position).getSensorClassLabel());
            // And finally return your dynamic (or custom) view for each spinner item
            return listView_row;

        }

    }




    public static LinkedHashMap<Long,Float> parsJSON_Measurement(String json_results) {
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

    public static Float parsJSON_Measurement_Object_WithOutTimeStamp(String json_results) {
        try {
            JSONObject jsonObject=new JSONObject(json_results);

            Float value=null;
            if (!jsonObject.getString("value").equals("null")) {
                value = Float.valueOf(jsonObject.getString("value"));
            }
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    static Map<String, String> getLineChartLabels(Context context) {
        String[] array = context.getResources().getStringArray(R.array.ChartLabels);
        Map<String, String> result = new HashMap<>();
        for (String str : array) {
            String[] splittedItem = str.split(":");
            result.put(splittedItem[0], splittedItem[1]);
        }
        return result;
    }

    public static boolean iswantedMeasurementsIdentifier(String measurementIdentifier,int[] unwantedMeasurementIdentifiersList) {

        boolean isMeasurementAllowed=true;
        if(Arrays.binarySearch(unwantedMeasurementIdentifiersList,Integer.valueOf(measurementIdentifier)) >= 0)//sort the array first!
        {
            isMeasurementAllowed=false;
        }
        return isMeasurementAllowed;
    }

 public static class ChartLine{
     int color;
     String label;
     LinkedHashMap<Long,Float> linexyvalues;

     public ChartLine(int color, String label, LinkedHashMap<Long, Float> linexyvalues) {
         this.color = color;
         this.label = label;
         this.linexyvalues = linexyvalues;
     }

     public int getColor() {
         return color;
     }

     public void setColor(int color) {
         this.color = color;
     }

     public String getLabel() {
         return label;
     }

     public void setLabel(String label) {
         this.label = label;
     }

     public LinkedHashMap<Long, Float> getLinexyvalues() {
         return linexyvalues;
     }

     public void setLinexyvalues(LinkedHashMap<Long, Float> linexyvalues) {
         this.linexyvalues = linexyvalues;
     }
 }



    //this part is related to user awareness
    public  Float getInstantMeasurementVariableValue(int measurementVariableID) {
        String json_Url = serverURL + "/measurements/variable/" + String.valueOf(measurementVariableID) + "/instant";
        String JSON_STRING;
        try {
            URL url = new URL(json_Url);
            HttpURLConnection httpconnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpconnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            while ((JSON_STRING = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(JSON_STRING+"\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpconnection.disconnect();
            String json_String=stringBuilder.toString().trim();
            //pars json_string
            Float parsedResult = parsJSON_Measurement_Object_WithOutTimeStamp(json_String);
            return parsedResult;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public class BackgroundTaskGetLatestMeasurementClassValues extends AsyncTask<Void,Void,List<MeasurementClass>>{

        Room room;
        boolean isRefresh;
        Context context;
        List<MeasurementClass> listMeasurementClassesParesed;
        MeasurementClass measurementClass;

        public BackgroundTaskGetLatestMeasurementClassValues(Context context,Room room, boolean isRefresh) {
            this.room=room;
            this.isRefresh=isRefresh;
            this.context=context;
        }

        @Override
        protected List<MeasurementClass> doInBackground(Void... params) {
            String roomMeasurementClasslistJSON = room.getRoomMeasurementlist(room.getRoomid());
            if (roomMeasurementClasslistJSON != null){
                int[] UnwantedMeasurementIdentifiers = context.getResources().getIntArray(R.array.UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed = room.parsRoomSensorClassesJSON(roomMeasurementClasslistJSON,UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed=measurementClass.getMeasurementlatestValues(listMeasurementClassesParesed,room.getRoomid(),isRefresh);
            }
            return listMeasurementClassesParesed;
        }


        @Override
        protected void onPostExecute(List<MeasurementClass> measurementClasses) {
            super.onPostExecute(measurementClasses);
            // put results on DataAPI
            if (listMeasurementClassesParesed != null) {

                //put latest values to DATA API Message path
                String myMessagePath=context.getResources().getString(R.string.messagepath_latest_measurements);
                //String myMessage=context.getResources().getString(R.string.message_fetchBeaconList);
                context.startService(new Intent(context,
                        SendMessageServiceToWearble.class)
                        .putExtra("myMessagePath",myMessagePath)
                        .putExtra("myMeasurementClassesLatestValueMessage", (Serializable) listMeasurementClassesParesed)
                        .putExtra("myMessageType",SendMessageServiceToWearble.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));

            }


        }
    }


    public class BackgroundTaskGetLast7DaysMeasurementClassValues extends AsyncTask<Void,Void,String>{
        String roomID;
        String measurementClassID;
        boolean isRefreshData;


        public BackgroundTaskGetLast7DaysMeasurementClassValues(String roomID,String measurementClassID, boolean isRefreshData) {
            this.roomID=roomID;
            this.measurementClassID = measurementClassID;
            this.isRefreshData=isRefreshData;
        }

        @Override
        protected String doInBackground(Void... params) {
            //use json url generator to generate the related url
            String jsonUrls= serverURL+"/measurements/60min/room/"+roomID+"/variableclass/"+measurementClassID+"/"+ DateTimeObj.getCurrentDate()+"?weekly=true";
            String json_Measurement7DaysValues=json_Downloader(jsonUrls,isRefreshData,60*40);
            //put results to DataApi
            //put latest values to DATA API Message path
            return json_Measurement7DaysValues;
        }

        @Override
        protected void onPostExecute(String json_Measurement7DaysValues) {
            super.onPostExecute(json_Measurement7DaysValues);


            String myMessagePath=context.getResources().getString(R.string.messagepath_last7days_measurements);
            //String myMessage=context.getResources().getString(R.string.message_fetchBeaconList);
            context.startService(new Intent(context,
                    SendMessageServiceToWearble.class)
                    .putExtra("myMessagePath",myMessagePath)
                    .putExtra("myMessage_json_Measurement7DaysValues", json_Measurement7DaysValues)
                    .putExtra("myMessage_MeasurementClassID",measurementClassID)
                    .putExtra("myMessageType",SendMessageServiceToWearble.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));



        }
    }


    private String json_Downloader(String stringUrl,boolean isRefreshCachedData,int maxStale){
        String JSON_STRING;
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
            if (isRefreshCachedData==true) {
                httpconnection.setUseCaches(false);
            }

            httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
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

            return  stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      return null;
    }




}
