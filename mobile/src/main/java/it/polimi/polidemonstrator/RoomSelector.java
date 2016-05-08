package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import it.polimi.polidemonstrator.businesslogic.Building;
import it.polimi.polidemonstrator.businesslogic.DateTimeObj;
import it.polimi.polidemonstrator.businesslogic.MesurementClass;
import it.polimi.polidemonstrator.businesslogic.Room;


/**
 * Created by saeed on 4/11/2016.
 */
public class RoomSelector extends Activity {

    private Spinner spinnerBuilding, spinnerRoom;
    private Button btnRefreshBuilding;
    ListView listVieMeasurements;

    List<MesurementClass> hashMapMeasurementClassesParesed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomselector);
        setAcitivityElements();


        new BackgroundTaskGetBuildings().execute();

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();


    }

    private void setAcitivityElements() {
        spinnerBuilding = (Spinner) findViewById(R.id.spinnerBuilding);
        spinnerRoom = (Spinner) findViewById(R.id.spinnerRoom);


        listVieMeasurements=(ListView)findViewById(R.id.listViewMeasurementClass);
        btnRefreshBuilding=(Button) findViewById(R.id.btnRefreshBuilding);
    }





    // add items into spinner dynamically
    HashMap<String, String> mapList = new HashMap<String, String>();



    public void addListenerOnSpinnerItemSelection() {

        spinnerBuilding.setOnItemSelectedListener(new SpinnerBuildingOnItemSelectedListener());
        spinnerRoom.setOnItemSelectedListener(new SpinnerRoomOnItemSelectedListener());
        listVieMeasurements.setOnItemClickListener(new listViewMeasurementsOnItemSelectedListener());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {



        btnRefreshBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTaskGetBuildings().execute();
            }
        });
    }



    //add fetched data from API to SpinnerBuildings
    private void addItemsOnSpinnerBuildings(HashMap<String, String> hashMapBuildings) {
        ArrayList<Building> arrayList = new ArrayList<Building>();
        for (Map.Entry<String,String> entry : hashMapBuildings.entrySet()){

            Building building=new Building();
            building.setBuildingid(entry.getKey());
            building.setBuildingLable(entry.getValue());
            arrayList.add(building);
        }

        Building.SpinAdapterBuilding adapter=new Building.SpinAdapterBuilding(RoomSelector.this,android.R.layout.simple_spinner_item,
                arrayList);

        spinnerBuilding.setAdapter(adapter);



    }


    // add fetched data from API to SpinnerRooms
    public void addItemsOnSpinnerRooms(HashMap<String,String> hashMapRooms) {

        ArrayList<Room> arrayList = new ArrayList<Room>();
        for (Map.Entry<String,String> entry : hashMapRooms.entrySet()){

            Room room=new Room();
            room.setRoomid(entry.getKey());
            room.setRoomLabel(entry.getValue());
            arrayList.add(room);
        }

        Room.SpinAdapterRoom adapter=new Room.SpinAdapterRoom(RoomSelector.this,android.R.layout.simple_spinner_item,
                arrayList);
        spinnerRoom.setAdapter(adapter);
    }

    // add fetched data from API to SpinnerSensorClasses
    public void addItemsOnListViewMeasurementClasses(List<MesurementClass> listMesurementClasses) {

        ArrayList<MesurementClass> arrayList = new ArrayList<>();
        for(MesurementClass item : listMesurementClasses){
            MesurementClass msurementClass=new MesurementClass();
            String[] listViewItem=MesurementClass.getMeasurementListViewItem(item.getSensorClasseId());
            if (listViewItem != null) {
                msurementClass.setSensorClasseId(item.getSensorClasseId());
                msurementClass.setSensorClassLabel(listViewItem[0]);
                msurementClass.setSensorClassImage(Integer.valueOf(listViewItem[1]));
                msurementClass.setSensorClassSensorLatestValue(item.getSensorClassSensorLatestValue()+" "+listViewItem[2]);//entry.getValue()[1] is the lates value of measurement Class
            }
            arrayList.add(msurementClass);
        }
        MesurementClass.AdapterSensorClasses sensorClassesAdapter=new MesurementClass.AdapterSensorClasses(RoomSelector.this,R.layout.list_singlerow,arrayList);

        listVieMeasurements.setAdapter(sensorClassesAdapter);

    }




    //Async Task to fetch Sensors Class list of a given room ID
    public class BackgroundTaskGetMeasurementList extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //call a select latest value of a measurement class
            MesurementClass mesurementClass=new MesurementClass(RoomSelector.this);
            String  measurementClassVariablesURL=mesurementClass.jsonURL_GeneratorMeasurenetClassVariables("1", "1");

        }
        @Override
        protected String doInBackground(String... params) {
            Room room = new Room();
            String roomSensorlistJSON = room.getRoomSensorlist("https://api.myjson.com/bins/1lyj6");//("http://131.175.56.243:8080/variables/room/"+params[0]+"/list");//params[0] corresponds to roomID


            return roomSensorlistJSON;

        }
        @Override
        protected void onPostExecute(String results) {
            if (results != null) {
                Room room = new Room();
                int[] UnwantedMeasurementIdentifiers = getResources().getIntArray(R.array.UnwantedMeasurementIdentifiers);
                hashMapMeasurementClassesParesed = room.parsRoomSensorClassesJSON(results,UnwantedMeasurementIdentifiers);
                //Fill sensor spinner with given sensors list data
                addItemsOnListViewMeasurementClasses(hashMapMeasurementClassesParesed);
            }else{
                Toast.makeText(RoomSelector.this,
                        "Sorry, server is not Available,\n please try again!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    //Async Task to fetch ROOM list of a given building ID
    public class BackgroundTaskGetBuildingRoomsList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            Building building = new Building();
            String roomListJSON = building.getRoomList("https://api.myjson.com/bins/4ep2q");//("http://131.175.56.243:8080/rooms/building/"+params[0]);//param[0] corresponds to Building id
            return roomListJSON;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null) {
                Room room = new Room();
                HashMap<String, String> hashMapRooms = room.parsRoomListJSON(results);
                //Fill sensor spinner with given sensors list data
                addItemsOnSpinnerRooms(hashMapRooms);
            }else{
                Toast.makeText(RoomSelector.this,
                        "Sorry, server is not Available,\n please try again!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }



    //Async Task to fetch Buildings list
    public class BackgroundTaskGetBuildings extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Building building = new Building();
            String buildingListJSON = building.getBuildings("https://api.myjson.com/bins/4cjwy");//("http://131.175.56.243:8080/buildings/");
            return buildingListJSON;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null) {
                Building building = new Building();
                HashMap<String, String> hashMapBuildings = building.parsBuildingJSON(results);
                //Fill sensor spinner with given sensors list data
                addItemsOnSpinnerBuildings(hashMapBuildings);
            }else{
                Toast.makeText(RoomSelector.this,
                        "Sorry, server is not Available,\n please try again!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class SpinnerBuildingOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Building building=(Building) parent.getItemAtPosition(pos);

            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + building.getBuildingLable()+" "+building.getBuildingid(),
                    Toast.LENGTH_SHORT).show();
                    //execute another Async Task to fetch the related rooms of the chosen building
                    new BackgroundTaskGetBuildingRoomsList().execute(building.getBuildingid());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }


    private class SpinnerRoomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Room room=(Room) parent.getItemAtPosition(position);

            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + room.getRoomLabel()+" "+room.getRoomid(),
                    Toast.LENGTH_SHORT).show();
            //execute another Async Task to fetch the related rooms of the chosen building
            new BackgroundTaskGetMeasurementList().execute(room.getRoomid());

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private class listViewMeasurementsOnItemSelectedListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Here we have to create a bundle and send the Building ID, Room ID and VariableClass (i.e., temperature)
            //to next activity
            Building selectedBuilding=(Building)spinnerBuilding.getSelectedItem();
            Room selectedRoom=(Room)spinnerRoom.getSelectedItem();
            MesurementClass selectedSensorClass=(MesurementClass)listVieMeasurements.getItemAtPosition(position);
            String buildingID=selectedBuilding.getBuildingid();
            String roomID=selectedRoom.getRoomid();
            String measurementClassID=selectedSensorClass.getSensorClasseId();

            Bundle basket=new Bundle();
            basket.putString("buildingID", buildingID);
            basket.putString("roomID",roomID);
            basket.putString("measuermentClassID", measurementClassID);
            basket.putSerializable("listMeasuremetClasses", (Serializable) hashMapMeasurementClassesParesed);


            Intent openChartActivity = new Intent("android.intent.action.CHART_LINECHART");
            openChartActivity.putExtras(basket);
            startActivity(openChartActivity);
        }
    }
}
