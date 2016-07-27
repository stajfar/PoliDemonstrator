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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.polidemonstrator.MyApplication;
import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 4/11/2016.
 */
public class Room {
    private String roomid;
    private String roomLabel;
    private static String serverURL;

    public Room(Context context) {
        final MyApplication myApplication=(MyApplication)context.getApplicationContext();
        this.serverURL= myApplication.getJsonServerURL();
    }

    public Room() {

    }


    public String getRoomLabel() {
        return roomLabel;
    }

    public void setRoomLabel(String roomLabel) {
        this.roomLabel = roomLabel;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

   ///get room variables
    public String getRoomMeasurementVariables_JSON(String roomid){
        String json_url=serverURL+"/variables/room/"+roomid;
        String JSON_STRING;
        try {
            URL url=new URL(json_url);
            HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
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
            return stringBuilder.toString().trim();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public Map<String,List<Integer>> parsRoomSensorVariablesJSON(String json_results, List<String> instantMeasurementVariables) {
        Map<String,List<Integer>> mapParsedResults=new HashMap<>();
        try {

            JSONArray jsonArray=new JSONArray(json_results);

            MeasurementClass measurementItem;

            for(String mesurementVariable: instantMeasurementVariables) {
                int count=0;
                List<Integer> listJsonMeasurementVariables=new ArrayList<>();
                measurementItem = new MeasurementClass();
                while (count < jsonArray.length()) {
                    JSONObject jsonObject = jsonArray.getJSONObject(count);
                    if (mesurementVariable.equals(jsonObject.getString("label")) && jsonObject.getJSONObject("fksensor").getBoolean("indoor") == true) {

                        measurementItem.setSensorVariableLabel(jsonObject.getString("label"));
                        listJsonMeasurementVariables.add(jsonObject.getInt("variableid"));
                    }
                    count++;
                }
                if(listJsonMeasurementVariables.size() >0) {
                    mapParsedResults.put(measurementItem.getSensorVariableLabel(), listJsonMeasurementVariables);
                }
            }
            return mapParsedResults;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }




    public String getRoomMeasurementlist(String roomid){
        String json_url=serverURL+"/variables/room/"+roomid+"/list";
        String JSON_STRING;
        try {
            URL url=new URL(json_url);
            HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
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


    public HashMap<String, String> parsRoomListJSON(String json_results) {
        try {
            HashMap<String,String> hashMapSensors=new HashMap<>();
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            String roomid;
            String roomlabel;


            while (count< jsonArray.length())
            {
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                roomid=jsonObject.getString("roomid");
                roomlabel=jsonObject.getString("label");
                //fill a list to be returened
                hashMapSensors.put(roomid,roomlabel);
                count++;
            }
            return hashMapSensors;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    //A custom adapter for spinner which makes it more flexible to work with complex data types
    public static class SpinAdapterRoom extends ArrayAdapter<Room> {
        private Context context;
        private List<Room> rooms;
        private int resource;
        public SpinAdapterRoom(Context context, int resource, List<Room> rooms) {
            super(context, resource, rooms);
            this.context = context;
            this.rooms=rooms;
            this.resource=resource;
        }

        public int getCount(){
            return rooms.size();
        }

        public Room getItem(int position){
            return rooms.get(position);
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

            textViewRoom.setText(rooms.get(position).getRoomLabel());
            textViewRightSide.setText("");
            imageView.setImageResource(R.drawable.ic_room);
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

            textViewRoom.setText(rooms.get(position).getRoomLabel());
            textViewRightSide.setText("");
            imageView.setImageResource(R.drawable.ic_room);
            // And finally return your dynamic (or custom) view for each spinner item
            return listView_row;
        }

    }










}
