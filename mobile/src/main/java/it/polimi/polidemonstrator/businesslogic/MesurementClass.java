package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 4/26/2016.
 */
public class MesurementClass {
    //correlated to sensor classes
    private String SensorClasseId;
    private String sensorClassLabel;
    String serverURL;

    public MesurementClass(Context context) {
        ServerURL serverURLclass=new ServerURL();
        serverURL=serverURLclass.getServerURL(context);
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


    public static HashMap<String,List<String>> jsonURL_Generator(String sensorClassID,String buildingID,String roomID) {
        HashMap<String,List<String>> hashMapJson_Urls=new HashMap<>();
        List<String> UrlsColorsInternal=new ArrayList<String>();
        List<String> UrlsColorsExternal=new ArrayList<String>();
        switch (sensorClassID){
            case "1":
                // UrlsColorsInternal.add(serverURL+"/measurements/15min/room/"+roomID+"/variableclass/"+sensorClassID+"/"+ DateTimeObj.getCurrentDate());
                UrlsColorsInternal.add("https://api.myjson.com/bins/4hjry");
                UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                // UrlsColorsExternal.add("http://131.175.56.243:8080/weatherreports/60min/building/" + buildingID + "/" + DateTimeObj.getCurrentDate() + "?var=airtemperature");
                UrlsColorsExternal.add("https://api.myjson.com/bins/4sham");
                UrlsColorsExternal.add(String.valueOf(Color.RED));
                hashMapJson_Urls.put("Internal Temperature",UrlsColorsInternal);
                hashMapJson_Urls.put("External Temperature", UrlsColorsExternal);

                break;
            case "2":
                //UrlsColorsInternal.add("http://131.175.56.243:8080/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                UrlsColorsInternal.add("https://api.myjson.com/bins/5cpgi");
                UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));

               // UrlsColorsExternal.add("http://131.175.56.243:8080/weatherreports/60min/building/" + buildingID + "/" + DateTimeObj.getCurrentDate() + "?var=relativehumidity");
                UrlsColorsExternal.add("https://api.myjson.com/bins/4vsnu");
                UrlsColorsExternal.add(String.valueOf(Color.RED));

                hashMapJson_Urls.put("Internal Humidity",UrlsColorsInternal);
                hashMapJson_Urls.put("External Humidity", UrlsColorsExternal);


                break;
            case "4":
                //UrlsColorsInternal.add("http://131.175.56.243:8080/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                UrlsColorsInternal.add("https://api.myjson.com/bins/mjoq");
                UrlsColorsInternal.add(String.valueOf(ColorTemplate.getHoloBlue()));
                hashMapJson_Urls.put("Internal CO2", UrlsColorsInternal);

                break;
            default:
                //later change this default to something else
                // hashMapJson_Urls.put("Internal Temperature", "http://131.175.56.243:8080/measurements/15min/room/" + roomID + "/variableclass/" + sensorClassID + "/" + DateTimeObj.getCurrentDate());
                // hashMapJson_Urls.put("External Temperature", "http://131.175.56.243:8080/weatherreports/60min/building/" + buildingID + "/" + DateTimeObj.getCurrentDate() + "?var=airtemperature");
                 }
        return hashMapJson_Urls;

    }





    //SpinAddapter for room sensor classes
    //A custom adapter for spinner which makes it more flexible to work with complex data types
    public static class SpinAdapterSensorClasses extends ArrayAdapter<MesurementClass> {
        private Context context;


        private List<MesurementClass> mesurementClasses;

        public SpinAdapterSensorClasses(Context context, int resource, List<MesurementClass> mesurementClasses) {
            super(context, resource, mesurementClasses);
            this.context = context;
            this.mesurementClasses=mesurementClasses;

        }

        public int getCount(){
            return mesurementClasses.size();
        }

        public MesurementClass getItem(int position){
            return mesurementClasses.get(position);
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
            label.setText(mesurementClasses.get(position).getSensorClassLabel());

            // And finally return your dynamic (or custom) view for each spinner item
            return label;
        }

        // And here is when the "chooser" is popped up
        // Normally is the same view, but you can customize it if you want
        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setText(mesurementClasses.get(position).getSensorClassLabel());

            return label;
        }

    }



    public static class AdapterSensorClasses extends ArrayAdapter<MesurementClass> {
        private Context context;
        private int resource;

        private List<MesurementClass> mesurementClasses;

        public AdapterSensorClasses(Context context, int resource, List<MesurementClass> mesurementClasses) {
            super(context, resource, mesurementClasses);
            this.context = context;
            this.mesurementClasses=mesurementClasses;
            this.resource=resource;

        }

        public int getCount(){
            return mesurementClasses.size();
        }

        public MesurementClass getItem(int position){
            return mesurementClasses.get(position);
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

            textViewSensorClass.setText(mesurementClasses.get(position).getSensorClassLabel());
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

            textViewSensorClass.setText(mesurementClasses.get(position).getSensorClassLabel());
            // And finally return your dynamic (or custom) view for each spinner item
            return listView_row;

        }

    }






}
