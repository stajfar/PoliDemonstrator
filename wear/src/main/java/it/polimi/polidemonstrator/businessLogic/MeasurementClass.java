package it.polimi.polidemonstrator.businesslogic;



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

import it.polimi.polidemonstrator.R;

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



    private String sensorClassMeasurementUnit;

    public MeasurementClass() {
    }


    public int getSensorClassImage() {
        return sensorClassImage;
    }

    public void setSensorClassImage(int sensorClassImage) {
        this.sensorClassImage = sensorClassImage;
    }


    public String getSensorClassMeasurementUnit() {
        return sensorClassMeasurementUnit;
    }

    public void setSensorClassMeasurementUnit(String sensorClassMeasurementUnit) {
        this.sensorClassMeasurementUnit = sensorClassMeasurementUnit;
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
            if (isRefresh) {
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
            LinkedHashMap<Long,Float> hashMapParsedResult=new LinkedHashMap<>();

            while (count< jsonArray.length())
            {
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                if (!jsonObject.getString("value").equals("null")) {
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
        put("10", new String[]{"Active Energy Meter", String.valueOf(R.drawable.ic_information)," "});
    }};

    public static String[] getMeasurementGridViewPagerItem(String measurementId) {
        String[] measurementResource;
        try {
            measurementResource=measurementLookUpTable.get(measurementId);
        }catch (Exception e)
        {
            measurementResource=null;
        }
        return measurementResource;
    }


}
