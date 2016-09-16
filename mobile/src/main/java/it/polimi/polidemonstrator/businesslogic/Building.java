package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;

import it.polimi.polidemonstrator.MyApplication;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 4/11/2016.
 */
public class Building {


    public Building(Context context) {
        final MyApplication myApplication=(MyApplication)context.getApplicationContext();
        this.serverURL= myApplication.getJsonServerURL();
    }

    private static String serverURL;
    private String buildingid;
    private String buildingLable;

    public String getBuildingid() {
        return buildingid;
    }

    public void setBuildingid(String buildingid) {
        this.buildingid = buildingid;
    }

    public String getBuildingLable() {
        return buildingLable;
    }

    public void setBuildingLable(String buildingLable) {
        this.buildingLable = buildingLable;
    }








    //List of functions
    public String getBuildings( ) {
        String JSON_STRING;
        String json_url=serverURL+"/buildings";
        try {
            URL url = new URL(json_url);
            HttpURLConnection httpconnection = (HttpURLConnection) url.openConnection();
           // httpconnection.addRequestProperty("Cache-Control", "only-if-cached");
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

    public HashMap<String, String> parsBuildingJSON(String json_results) {
        try {
            HashMap<String, String> hashMapSensors = new HashMap<>();
            JSONArray jsonArray = new JSONArray(json_results);
            int count = 0;
            String buildingid;
            String buildingLabel;


            while (count < jsonArray.length()) {
                JSONObject jsonObject = jsonArray.getJSONObject(count);
                buildingid = jsonObject.getString("buildingid");
                buildingLabel = jsonObject.getString("label");
                //fill a list to be returened
                hashMapSensors.put(buildingid, buildingLabel);
                count++;
            }
            return hashMapSensors;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRoomList(String buildingID) {
        String json_url=serverURL+"/rooms/building/"+buildingID;
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


    //Here is the list of inner classes

    //A custom adapter for spinner which makes it more flexible to work with complex data types
    public static class SpinAdapterBuilding extends ArrayAdapter<Building> {
        private Context context;
        private int resource;
        private List<Building> buildings;
        public SpinAdapterBuilding(Context context, int resource, List<Building> buildings) {
            super(context, resource, buildings);
            this.context = context;
            this.buildings=buildings;
            this.resource=resource;

        }

        public int getCount(){
            return buildings.size();
        }

        public Building getItem(int position){
            return buildings.get(position);
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
            TextView textViewRoom=(TextView)listView_row.findViewById(R.id.tvListSensorClass);
            TextView textViewRightSide=(TextView)listView_row.findViewById(R.id.tvListSensorLatestValue);

            textViewRoom.setText(buildings.get(position).getBuildingLable());
            textViewRightSide.setText("");
            imageView.setImageResource(R.drawable.ic_building);
            return listView_row;

        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View listView_row= inflater.inflate(resource,parent,false);
            ImageView imageView=(ImageView)listView_row.findViewById(R.id.imageViewlistItem);
            TextView textViewRoom=(TextView)listView_row.findViewById(R.id.tvListSensorClass);
            TextView textViewRightSide=(TextView)listView_row.findViewById(R.id.tvListSensorLatestValue);

            textViewRoom.setText(buildings.get(position).getBuildingLable());
            textViewRightSide.setText("");
            imageView.setImageResource(R.drawable.ic_building);
            return listView_row;
        }

    }
}
