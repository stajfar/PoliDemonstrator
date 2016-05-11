package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import it.polimi.polidemonstrator.businesslogic.Building;
import it.polimi.polidemonstrator.businesslogic.InternetConnection;
import it.polimi.polidemonstrator.businesslogic.MesurementClass;
import it.polimi.polidemonstrator.businesslogic.Room;



/**
 * Created by saeed on 4/11/2016.
 */
public class RoomSelector extends Activity {

    private Spinner spinnerBuilding, spinnerRoom;

    ListView listVieMeasurements;
    SwipeRefreshLayout swipeRefreshLayout;

    List<MesurementClass> listMeasurementClassesParesed;

    MesurementClass measurementClass;
    Building building;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomselector);
        setAcitivityElements();
        measurementClass=new MesurementClass(RoomSelector.this);
        building=new Building(RoomSelector.this);
        room=new Room(RoomSelector.this);


        new BackgroundTaskGetBuildings().execute();

        addListenerOnButton();
        addListenerOnSpinnerItemSelection();


    }

    private void setAcitivityElements() {
        spinnerBuilding = (Spinner) findViewById(R.id.spinnerBuilding);
        spinnerRoom = (Spinner) findViewById(R.id.spinnerRoom);


        listVieMeasurements=(ListView)findViewById(R.id.listViewMeasurementClass);


        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh_measurements);
    }





    // add items into spinner dynamically
    HashMap<String, String> mapList = new HashMap<String, String>();



    public void addListenerOnSpinnerItemSelection() {

        spinnerBuilding.setOnItemSelectedListener(new SpinnerBuildingOnItemSelectedListener());
        spinnerRoom.setOnItemSelectedListener(new SpinnerRoomOnItemSelectedListener());
        listVieMeasurements.setOnItemClickListener(new listViewMeasurementsOnItemSelectedListener());
        swipeRefreshLayout.setOnRefreshListener(new swipeRefreshLayoutOnRefreshListener());
        swipeRefreshLayout.post(new swipRefreshLayoutAnimation());
    }

    // get the selected dropdown list value
    public void addListenerOnButton() {




    }



    //add fetched data from API to SpinnerBuildings
    private void addItemsOnSpinnerBuildings(HashMap<String, String> hashMapBuildings) {
        ArrayList<Building> arrayList = new ArrayList<Building>();
        for (Map.Entry<String,String> entry : hashMapBuildings.entrySet()){
            Building building=new Building(RoomSelector.this);
            building.setBuildingid(entry.getKey());
            building.setBuildingLable(entry.getValue());
            arrayList.add(building);
        }

        Building.SpinAdapterBuilding adapter=new Building.SpinAdapterBuilding(RoomSelector.this,R.layout.list_singlerow,
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

        Room.SpinAdapterRoom adapter=new Room.SpinAdapterRoom(RoomSelector.this,R.layout.list_singlerow,
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
    public class BackgroundTaskGetMeasurementList extends AsyncTask<String, Void, List<MesurementClass>> {
        Room room;
        boolean isRefresh;

        public BackgroundTaskGetMeasurementList(Room room, boolean isRefresh) {
            this.room=room;
            this.isRefresh=isRefresh;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected List<MesurementClass> doInBackground(String... params) {

            String roomMeasurementClasslistJSON = room.getRoomMeasurementlist(room.getRoomid());
            if (roomMeasurementClasslistJSON != null){
                int[] UnwantedMeasurementIdentifiers = getResources().getIntArray(R.array.UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed = room.parsRoomSensorClassesJSON(roomMeasurementClasslistJSON,UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed=measurementClass.getMeasurementlatestValues(listMeasurementClassesParesed,room.getRoomid(),isRefresh);
            }
            return listMeasurementClassesParesed;
        }
        @Override
        protected void onPostExecute(List<MesurementClass> listMeasurementClassesParesed) {
            if (listMeasurementClassesParesed != null) {



                //Fill sensor spinner with given sensors list data
                addItemsOnListViewMeasurementClasses(listMeasurementClassesParesed);
            }else{
                Toast.makeText(RoomSelector.this,
                        "Sorry, server is not Available,\n please try again!",
                        Toast.LENGTH_SHORT).show();
            }
            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    //Async Task to fetch ROOM list of a given building ID
    public class BackgroundTaskGetBuildingRoomsList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String buildingID=params[0];
            String roomListJSON = building.getRoomList(buildingID);
            return roomListJSON;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null) {

                HashMap<String, String> hashMapRooms = room.parsRoomListJSON(results);
                //Fill sensor spinner with given sensors list data
                if(hashMapRooms.size() != 0){
                    addItemsOnSpinnerRooms(hashMapRooms);
                }else {
                    addItemsOnSpinnerRooms(hashMapRooms);
                    //clean Listview of measurements from old data
                    cleanListViewMeasurementClasses();
                }

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

            String buildingListJSON = building.getBuildings();//("http://131.175.56.243:8080/buildings/");
            return buildingListJSON;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null) {
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

    private void cleanListViewMeasurementClasses() {
        ArrayList<MesurementClass> arrayList = new ArrayList<>();
        MesurementClass.AdapterSensorClasses sensorClassesAdapter=new MesurementClass.AdapterSensorClasses(RoomSelector.this,R.layout.list_singlerow,arrayList);
        listVieMeasurements.setAdapter(sensorClassesAdapter);
    }


    private class SpinnerRoomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Room room=(Room) parent.getItemAtPosition(position);
            boolean isRefresh=false;

            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + room.getRoomLabel()+" "+room.getRoomid(),
                    Toast.LENGTH_SHORT).show();

            //execute another Async Task to fetch the related rooms of the chosen building
            new BackgroundTaskGetMeasurementList(room,isRefresh).execute();

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
            basket.putSerializable("listMeasuremetClasses", (Serializable) listMeasurementClassesParesed);


            Intent openChartActivity = new Intent("android.intent.action.CHART_LINECHART");
            openChartActivity.putExtras(basket);
            startActivity(openChartActivity);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }

    private class swipeRefreshLayoutOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            boolean isInternetConnected=InternetConnection.isInternetConnected(RoomSelector.this);
            Room room=(Room) spinnerRoom.getSelectedItem();
            Toast.makeText(RoomSelector.this,
                    "Refreshing : " + room.getRoomLabel()+" data.",
                    Toast.LENGTH_SHORT).show();
            if(isInternetConnected == false){
                Toast.makeText(RoomSelector.this,
                        "There is no internet connection, cached data is displayed!",
                        Toast.LENGTH_SHORT).show();

                new BackgroundTaskGetMeasurementList(room, false).execute();//read data from cache
                swipeRefreshLayout.setRefreshing(false);
            }else {
                //execute another Async Task to fetch the related rooms of the chosen building
                new BackgroundTaskGetMeasurementList(room, true).execute();
            }

        }
    }

    private class swipRefreshLayoutAnimation implements Runnable {
        @Override
        public void run() {
            swipeRefreshLayout.setRefreshing(true);
        }
    }
}
