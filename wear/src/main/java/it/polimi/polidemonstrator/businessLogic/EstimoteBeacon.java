package it.polimi.polidemonstrator.businessLogic;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.estimote.sdk.Region;

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
import java.util.List;

/**
 * Created by saeed on 6/6/2016.
 */
public class EstimoteBeacon implements Serializable {

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
        ServerURL serverURL=new ServerURL();
        this.serverURL=serverURL.getServerURL(context);

    }


    private EstimoteBeacon() {

    }

    List<EstimoteBeacon> listestimoteBeacons;
    public List<EstimoteBeacon> getRoomCorrelatedBeacons(int roomID,boolean isrefresh){
       // new BackgroundTaskGetRoomCorrelatedBeacons(roomID,isrefresh).execute();
        return listestimoteBeacons;
    }


    //Async Task to fetch API server url form web server
    public class BackgroundTaskGetRoomCorrelatedBeacons extends AsyncTask<String, Void, List<EstimoteBeacon>> {
        int roomID;
        boolean isRefresh;
        Context context;
        public BackgroundTaskGetRoomCorrelatedBeacons(int roomID, boolean isReferesh, Context context) {
            this.roomID=roomID;
            this.isRefresh=isReferesh;
            this.context=context;
        }

        @Override
        protected List<EstimoteBeacon> doInBackground(String... params) {
            String roomCorrelatedBeaconListJason=getRoomCorrelatedBeaconsJson(roomID, isRefresh);
            if(roomCorrelatedBeaconListJason != null){
                List<EstimoteBeacon> listEstimoteBeacon= parsJSON_Beacons(roomCorrelatedBeaconListJason);
                return listEstimoteBeacon;
            }
           return null;
        }

        private String getRoomCorrelatedBeaconsJson(int roomID,boolean isRefresh) {
            String JSON_STRING;
            //// TODO: 6/6/2016 complete the url
            String beaconClassVariablesURL="https://api.myjson.com/bins/4vpga"; //serverURL+"/beacons/room/"+String.valueOf(roomID);
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
        protected void onPostExecute(List<EstimoteBeacon> listestimotebeacons_result) {

            if(listestimotebeacons_result != null){
               // BeaconMonitoring beaconMonitoring=new BeaconMonitoring(context);
               // beaconMonitoring.initializeBeaconManager(listestimotebeacons_result);
            }else {
                Toast.makeText(context,
                        "Beacon list \n" +
                                " fetch failed!",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    public  static   List<EstimoteBeacon> parsJSON_Beacons(String json_results) {
        List<EstimoteBeacon> listEstimoteBeacon=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            while (count< jsonArray.length())
            {
                EstimoteBeacon estimoteBeacon=new EstimoteBeacon();
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                if (jsonObject.getString("UUID") != "null") {
                    estimoteBeacon.setIdentifier(jsonObject.getString("identifier"));
                    estimoteBeacon.setUUID(jsonObject.getString("UUID"));
                    estimoteBeacon.setMajor(jsonObject.getInt("major"));
                    estimoteBeacon.setMinor(jsonObject.getInt("minor"));
                    listEstimoteBeacon.add(estimoteBeacon);
                }
                count++;
            }
            return listEstimoteBeacon;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
