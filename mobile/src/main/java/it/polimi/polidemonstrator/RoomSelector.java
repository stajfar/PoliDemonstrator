package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import it.polimi.polidemonstrator.businesslogic.Building;
import it.polimi.polidemonstrator.businesslogic.EstimoteBeacon;
import it.polimi.polidemonstrator.businesslogic.InternetConnection;
import it.polimi.polidemonstrator.businesslogic.MeasurementClass;
import it.polimi.polidemonstrator.businesslogic.MyPreferences;
import it.polimi.polidemonstrator.businesslogic.Room;
import it.polimi.polidemonstrator.businesslogic.SendMessageServiceToWearble2;


/**
 * Created by saeed on 4/11/2016.
 */
public class RoomSelector extends Activity {
    Context context;
    private Spinner spinnerBuilding, spinnerRoom;

    ListView listVieMeasurements;
    SwipeRefreshLayout swipeRefreshLayout;

    List<MeasurementClass> listMeasurementClassesParesed;

    MeasurementClass measurementClass;
    Building building;
    Room room;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.roomselector);
        setAcitivityElements();
        measurementClass=new MeasurementClass(context);
        building=new Building(context);
        room=new Room(context);


        new BackgroundTaskGetBuildings().execute();


        addListenerOnSpinnerItemSelection();
    }

    private void setAcitivityElements() {
        spinnerBuilding = (Spinner) findViewById(R.id.spinnerBuilding);
        spinnerRoom = (Spinner) findViewById(R.id.spinnerRoom);
        listVieMeasurements=(ListView)findViewById(R.id.listViewMeasurementClass);
        swipeRefreshLayout=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh_measurements);
    }








    public void addListenerOnSpinnerItemSelection() {
        spinnerBuilding.setOnItemSelectedListener(new SpinnerBuildingOnItemSelectedListener());
        spinnerRoom.setOnItemSelectedListener(new SpinnerRoomOnItemSelectedListener());
        listVieMeasurements.setOnItemClickListener(new listViewMeasurementsOnItemSelectedListener());
        swipeRefreshLayout.setOnRefreshListener(new swipeRefreshLayoutOnRefreshListener());
        swipeRefreshLayout.post(new swipRefreshLayoutAnimation());
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
    public void addItemsOnListViewMeasurementClasses(List<MeasurementClass> listMeasurementClasses) {

        ArrayList<MeasurementClass> arrayList = new ArrayList<>();
        for(MeasurementClass item : listMeasurementClasses){
            MeasurementClass msurementClass=new MeasurementClass();
            String[] listViewItem= MeasurementClass.getMeasurementListViewItem(item.getSensorClasseId());
            if (listViewItem != null) {
                msurementClass.setSensorClasseId(item.getSensorClasseId());
                msurementClass.setSensorClassLabel(listViewItem[0]);
                msurementClass.setSensorClassImage(Integer.valueOf(listViewItem[1]));
                msurementClass.setSensorClassSensorLatestValue(item.getSensorClassSensorLatestValue()+" "+listViewItem[2]);//entry.getValue()[1] is the lates value of measurement Class
            }
            arrayList.add(msurementClass);
        }
        MeasurementClass.AdapterSensorClasses sensorClassesAdapter=new MeasurementClass.AdapterSensorClasses(RoomSelector.this,R.layout.list_singlerow,arrayList);

        listVieMeasurements.setAdapter(sensorClassesAdapter);

    }




    //Async Task to fetch Measurement Class list of a given room ID
    public class BackgroundTaskGetMeasurementList extends AsyncTask<String, Void, List<MeasurementClass>> {
        Room room;
        boolean isRefresh;


        public BackgroundTaskGetMeasurementList(Room room, boolean isRefresh) {
            this.room=room;
            this.isRefresh=isRefresh;

        }


        @Override
        protected List<MeasurementClass> doInBackground(String... params) {

            String roomMeasurementClasslistJSON = room.getRoomMeasurementlist(room.getRoomid());
            if (roomMeasurementClasslistJSON != null){
                int[] UnwantedMeasurementIdentifiers = getResources().getIntArray(R.array.UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed = room.parsRoomSensorClassesJSON(roomMeasurementClasslistJSON,UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed=measurementClass.getMeasurementlatestValues(listMeasurementClassesParesed,room.getRoomid(),isRefresh);
            }
            return listMeasurementClassesParesed;
        }
        @Override
        protected void onPostExecute(List<MeasurementClass> listMeasurementClassesParesed) {
            if (listMeasurementClassesParesed != null) {
                //Fill sensor spinner with given sensors list data
                addItemsOnListViewMeasurementClasses(listMeasurementClassesParesed);

                //put latest values to DATA API Message path
                String myMessagePath=context.getResources().getString(R.string.messagepath_latest_measurements);
                //String myMessage=context.getResources().getString(R.string.message_fetchBeaconList);

                new SendMessageServiceToWearble2(context,new Intent(context,
                        SendMessageServiceToWearble2.class)
                        .putExtra("myMessagePath",myMessagePath)
                        .putExtra("myMeasurementClassesLatestValueMessage", (Serializable) listMeasurementClassesParesed)
                        .putExtra("myMessageType",SendMessageServiceToWearble2.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));


               /* context.startService(new Intent(context,
                        SendMessageServiceToWearble.class)
                        .putExtra("myMessagePath",myMessagePath)
                        .putExtra("myMeasurementClassesLatestValueMessage", (Serializable) listMeasurementClassesParesed)
                        .putExtra("myMessageType",SendMessageServiceToWearble.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));

*/

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
                //Fill room spinner with given room list data
                if(hashMapRooms.size() != 0){
                    addItemsOnSpinnerRooms(hashMapRooms);
                }else {
                    addItemsOnSpinnerRooms(hashMapRooms);
                    //clean Listview of measurements from old data
                    cleanListViewMeasurementClasses();
                }

                   //select the favorite room from saved preferences
                   spinnerRoom.setSelection(MyPreferences.getPreferenceRoom(context));
                   //clean room preferences
                   MyPreferences.clearPreferenceRoom(context);
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

            String buildingListJSON = building.getBuildings();
            return buildingListJSON;
        }

        @Override
        protected void onPostExecute(String results) {
            if (results != null) {
                HashMap<String, String> hashMapBuildings = building.parsBuildingJSON(results);
                //Fill building spinner with given sensors list data
                addItemsOnSpinnerBuildings(hashMapBuildings);

                //select the favorite building from preferences
                spinnerBuilding.setSelection(MyPreferences.getPreferenceBuilding(context));


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
                    "Selected: " + building.getBuildingLable()+" "+building.getBuildingid(),
                    Toast.LENGTH_SHORT).show();


            //execute another Async Task to fetch the related rooms of the chosen building
            new BackgroundTaskGetBuildingRoomsList().execute(building.getBuildingid());

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {


        }
    }

    private void cleanListViewMeasurementClasses() {
        ArrayList<MeasurementClass> arrayList = new ArrayList<>();
        MeasurementClass.AdapterSensorClasses sensorClassesAdapter=new MeasurementClass.AdapterSensorClasses(RoomSelector.this,R.layout.list_singlerow,arrayList);
        listVieMeasurements.setAdapter(sensorClassesAdapter);
    }


    private class SpinnerRoomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Room room=(Room) parent.getItemAtPosition(position);
            boolean isRefresh=false;

            Toast.makeText(parent.getContext(),
                    "Selected : " + room.getRoomLabel()+" "+room.getRoomid(),
                    Toast.LENGTH_SHORT).show();

            //execute another Async Task to fetch the related rooms of the chosen building
            new BackgroundTaskGetMeasurementList(room,isRefresh).execute();


            //put selected roomid on DataApi
            String myMessagePath=context.getResources().getString(R.string.messagepath_roomId);
            new SendMessageServiceToWearble2(context,new Intent(context,
                    SendMessageServiceToWearble2.class)
                    .putExtra("myMessagePath",myMessagePath)
                    .putExtra("myRoomID", room.getRoomid())
                    .putExtra("myMessageType",SendMessageServiceToWearble2.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));
            /*
            context.startService(new Intent(context,
                    SendMessageServiceToWearble.class)
                    .putExtra("myMessagePath",myMessagePath)
                    .putExtra("myRoomID", room.getRoomid())
                    .putExtra("myMessageType",SendMessageServiceToWearble.MyWear_HandheldMessageAPIType.SendThroughDataAPI.ordinal()));
            */

            //fetch the list of beacons from internet and send it back to wearble
            EstimoteBeacon estimoteBeacon=new EstimoteBeacon(context);
            //call async task to fetch beacon lists and send it to wear
            estimoteBeacon.new BackgroundTaskGetRoomCorrelatedBeacons(room.getRoomid(),false,context).execute();

            //save the current Building and Room Selection into preferences


            MyPreferences.savePreferencesRoomSelector(context,spinnerBuilding.getSelectedItemPosition(),
                    spinnerRoom.getSelectedItemPosition(),room.getRoomid());

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
            MeasurementClass selectedSensorClass=(MeasurementClass)listVieMeasurements.getItemAtPosition(position);
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


    @Override
    protected void onPause() {
        super.onPause();

    }
}
