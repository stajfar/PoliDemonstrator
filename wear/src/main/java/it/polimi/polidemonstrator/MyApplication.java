package it.polimi.polidemonstrator;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import it.polimi.polidemonstrator.businessLogic.BeaconMonitoring;
import it.polimi.polidemonstrator.businessLogic.SendMessageServiceToHandheld;
import it.polimi.polidemonstrator.businessLogic.MeasurementClass;
import it.polimi.polidemonstrator.businessLogic.Room;


/**
 * Created by saeed on 5/18/2016.
 */
public class MyApplication extends Application  {
    List<MeasurementClass> listMeasurementClassesParesed;
    MeasurementClass measurementClass;

    @Override
    public void onCreate() {
        super.onCreate();
        //start monitoring sorounding beacons
       new BeaconMonitoring(this);

    }


    //Async Task to fetch Sensors Class list of a given room ID
    public class BackgroundTaskGetMeasurementList extends AsyncTask<String, Void, List<MeasurementClass>> {
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
        protected List<MeasurementClass> doInBackground(String... params) {

            String roomMeasurementClasslistJSON = room.getRoomMeasurementlist(room.getRoomid());

            if (roomMeasurementClasslistJSON != null){
                int[] UnwantedMeasurementIdentifiers = getResources().getIntArray(R.array.UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed = room.parsRoomSensorClassesJSON(roomMeasurementClasslistJSON,UnwantedMeasurementIdentifiers);
                listMeasurementClassesParesed=measurementClass.getMeasurementlatestValues(listMeasurementClassesParesed, room.getRoomid(), isRefresh);
            }
            return listMeasurementClassesParesed;
        }
        @Override
        protected void onPostExecute(List<MeasurementClass> listMeasurementClassesParesed) {
            if (listMeasurementClassesParesed != null) {
                Toast.makeText(MyApplication.this,
                        "Yes Server!",
                        Toast.LENGTH_SHORT).show();

                //Fill sensor spinner with given sensors list data
                //addItemsOnListViewMeasurementClasses(listMeasurementClassesParesed);
            }else{
                Toast.makeText(MyApplication.this,
                        "No Server!",
                        Toast.LENGTH_SHORT).show();
            }


        }

    }



}
