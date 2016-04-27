package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;

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

import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 4/11/2016.
 */
public class Room {
    private String roomid;
    private String roomLabel;




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




    public String getRoomSensorlist(String json_url){
        String JSON_STRING;
        try {
            URL url=new URL(json_url);
            HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
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



    public HashMap<String,String> parsRoomSensorClassesJSON(String json_results) {
        try {
            HashMap<String,String> hashMapSensorClasses=new HashMap<>();
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            String identifierSensor;
            String nameSensor;


            while (count< jsonArray.length())
            {
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                identifierSensor=jsonObject.getString("identifier");
                nameSensor=jsonObject.getString("name");
                //fill a list to be returened
                hashMapSensorClasses.put(identifierSensor,nameSensor);
                count++;
            }
            return hashMapSensorClasses;

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



        public SpinAdapterRoom(Context context, int resource, List<Room> rooms) {
            super(context, resource, rooms);
            this.context = context;
            this.rooms=rooms;

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
            // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            // Then you can get the current item using the values array (Users array) and the current position
            // You can NOW reference each method you has created in your bean object (User class)
            label.setText(rooms.get(position).getRoomLabel());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(rooms.get(position).getRoomLabel());

            return label;
        }

    }










}
