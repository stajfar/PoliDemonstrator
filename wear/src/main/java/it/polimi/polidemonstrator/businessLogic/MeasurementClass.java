package it.polimi.polidemonstrator.businesslogic;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;


import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;


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

    //this function rounds the decimal portion of measurement values
    public static String roundDownMeasurementsValues(String rawMeasurementValue){
        String roundedDownMeasurementValue="";
        try{
            float rawmeasurementval=Float.valueOf(rawMeasurementValue);
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMinimumFractionDigits(1);
            formatter.setMaximumFractionDigits(1);
            roundedDownMeasurementValue = formatter.format(rawmeasurementval);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return  roundedDownMeasurementValue;
    }


}
