package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import it.polimi.polidemonstrator.MyApplication;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/7/2016.
 */
public class EstimoteBeacon {
    private String identifier;
    private String UUID;
    private int minor;
    private int major;
    private static String serverURL;
    private Context context;

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        EstimoteBeacon.serverURL = serverURL;
    }


    //list of functions
    public EstimoteBeacon(Context context) {
        this.context=context;
        final MyApplication myApplication=(MyApplication)context.getApplicationContext();
        this.serverURL= myApplication.getJsonServerURL();

    }


    private EstimoteBeacon() {

    }

    List<EstimoteBeacon> listestimoteBeacons;
    public List<EstimoteBeacon> getRoomCorrelatedBeacons(int roomID,boolean isrefresh){
        // new BackgroundTaskGetRoomCorrelatedBeacons(roomID,isrefresh).execute();
        return listestimoteBeacons;
    }


    //Async Task to fetch API server url form web server
    public class BackgroundTaskGetRoomCorrelatedBeacons extends AsyncTask<String, Void, String> {
        String roomID;
        boolean isRefresh;
        Context context;
        public BackgroundTaskGetRoomCorrelatedBeacons(String roomID, boolean isReferesh, Context context) {
            this.roomID=roomID;
            this.isRefresh=isReferesh;
            this.context=context;
        }

        @Override
        protected String doInBackground(String... params) {
            String roomCorrelatedBeaconListJason=getRoomCorrelatedBeaconsJson(roomID, isRefresh);
            if(roomCorrelatedBeaconListJason != null){
                return roomCorrelatedBeaconListJason;
            }
            return null;
        }

        private String getRoomCorrelatedBeaconsJson(String roomID,boolean isRefresh) {
            String JSON_STRING;
            //// TODO: 6/6/2016 complete the url
            String beaconClassVariablesURL="https://api.myjson.com/bins/2xxo5"; //serverURL+"/beacons/room/"+String.valueOf(roomID);
            try {
                URL url = new URL(beaconClassVariablesURL);
                HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
                if (isRefresh==true) {
                    httpconnection.setUseCaches(false);
                }
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
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

                return json_String;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String listestimotebeacons_result_Json) {

            if(listestimotebeacons_result_Json != null){
                String myMessagePath=context.getResources().getString(R.string.messagepath_beacon);
                String myMessage=listestimotebeacons_result_Json;
                context.startService(new Intent(context,
                        SendMessageServiceToWearble.class)
                        .putExtra("myMessagePath",myMessagePath)
                        .putExtra("myListEstimoteBeaconsJson", myMessage)
                        .putExtra("myMessageType",SendMessageServiceToWearble.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));

            }else {
                Toast.makeText(context,
                        "Beacon list \n" +
                                " fetch failed!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
