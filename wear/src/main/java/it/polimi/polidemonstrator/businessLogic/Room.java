package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;

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
import java.util.List;

/**
 * Created by saeed on 5/20/2016.
 */
public class Room {
    private String roomid;

    private static String serverURL;

    public Room(Context context) {
        ServerURL serverURL=new ServerURL();
        this.serverURL=serverURL.getServerURL(context);
    }

    public Room() {

    }

    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        Room.serverURL = serverURL;
    }


    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

//list of functions
public String getRoomMeasurementlist(String roomid) {
    String json_url = serverURL + "/variables/room/" + roomid + "/list";
    String JSON_STRING;
    try {
        URL url = new URL(json_url);
        HttpURLConnection httpconnection = (HttpURLConnection) url.openConnection();
        int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
        httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
        InputStream inputStream = httpconnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        while ((JSON_STRING = bufferedReader.readLine()) != null) {
            stringBuilder.append(JSON_STRING + "\n");
        }
        bufferedReader.close();
        inputStream.close();
        httpconnection.disconnect();
        return stringBuilder.toString().trim();

    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
    public List<MeasurementClass> parsRoomSensorClassesJSON(String json_results, int[] unwantedMeasurementIdentifiers) {
        try {
            List<MeasurementClass> listMeasurementClasses=new ArrayList<>();
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            while (count< jsonArray.length())
            {
                MeasurementClass measurementItem=new MeasurementClass();
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                measurementItem.setSensorClasseId(jsonObject.getString("identifier"));
                measurementItem.setSensorClassLabel(jsonObject.getString("name"));

                if(MeasurementClass.iswantedMeasurementsIdentifier(jsonObject.getString("identifier"),unwantedMeasurementIdentifiers)){
                    listMeasurementClasses.add(measurementItem);
                }
                count++;
            }
            return listMeasurementClasses;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }



}
