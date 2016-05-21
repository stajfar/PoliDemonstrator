package it.polimi.polidemonstrator.businessLogic;

import android.content.Context;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saeed on 5/20/2016.
 */
public class MeasurementClass implements Serializable {
    private static String serverURL;
    //correlated to sensor classes
    private String SensorClasseId;
    private String sensorClassLabel;
    private int sensorClassImage;
    private String sensorClassSensorLatestValue;

    public MeasurementClass() {
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
        ServerURL serverURL=new ServerURL();
        this.serverURL=serverURL.getServerURL(context);
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


//list of functions
public List<MeasurementClass> getMeasurementlatestValues(List<MeasurementClass> resultsParsed, String roomid, boolean isRefresh) {


    for (MeasurementClass meaurementClassItem : resultsParsed) {
        //json Query for each measurement class
        String measurementLatestValue = getMeasurementLatestValue(roomid, meaurementClassItem.getSensorClasseId(), isRefresh);
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
            int maxStale = 60 * 60 ; // tolerate 60 minutes stale
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

    public static boolean iswantedMeasurementsIdentifier(String measurementIdentifier,int[] unwantedMeasurementIdentifiersList) {

        boolean isMeasurementAllowed=true;
        if(Arrays.binarySearch(unwantedMeasurementIdentifiersList, Integer.valueOf(measurementIdentifier)) >= 0)//sort the array first!
        {
            isMeasurementAllowed=false;
        }
        return isMeasurementAllowed;
    }


}
