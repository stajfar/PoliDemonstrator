package it.polimi.polidemonstrator.businessLogic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

import it.polimi.polidemonstrator.MainActivity;
import it.polimi.polidemonstrator.MyNotification;

/**
 * Created by saeed on 6/3/2016.
 */
public class BeaconMonitoring {
    private BeaconManager beaconmanager;
    Region regionRoom;
    Region regionElevator;
    private Context context;

    public BeaconMonitoring(Context context) {
        this.context = context;
        initializeBeaconManager();
    }

    private void initializeBeaconManager() {
        Toast.makeText(context, "Be monitorin Srv started", Toast.LENGTH_SHORT).show();

        // Add this before all other beacon code
        EstimoteSDK.enableDebugLogging(true);

        beaconmanager = new BeaconManager(context);

        regionRoom = new Region("monitored region room", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 43060, 27142);

        regionElevator = new Region("monitored region elevator", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 43060, 27142);
        //set background monitoring interval
        beaconmanager.setBackgroundScanPeriod(10 * 1000, 15 * 1000);
        // monitoring service to see if users enters or exits from a specific beacon region
        beaconmanager.setMonitoringListener(new beaconManagerMonotoringListener());
        beaconmanager.connect(new beaconManagerServiceReadyCallback());

    }

    private class beaconManagerMonotoringListener implements BeaconManager.MonitoringListener {
        @Override
        public void onEnteredRegion(Region region, List<Beacon> list) {
            MyNotification.showNotification(context, MainActivity.class,
                    "Entring the region",
                    "sending msg");


            //Room room=new Room(MyApplication.this);
            //room.setRoomid("1");
            //new BackgroundTaskGetMeasurementList(room,true).execute();


            StateMachine stateMachine = new StateMachine();
            StateMachine.State newState = stateMachine.transition[StateMachine.State.FF.ordinal()][StateMachine.Symbols.Rm_out.ordinal()];

            context.startService(new Intent(context, SendMessageServiceToHandheld.class));
        }

        @Override
        public void onExitedRegion(Region region) {
            MyNotification.showNotification(context, MainActivity.class,
                    "Exiting the region",
                    "sending msg");


            //Room room=new Room(MyApplication.this);
            //room.setRoomid("1");
            //new BackgroundTaskGetMeasurementList(room,true).execute();

            // sendMessage();

        }
    }

    private class beaconManagerServiceReadyCallback implements BeaconManager.ServiceReadyCallback {
        @Override
        public void onServiceReady() {
            beaconmanager.startMonitoring(regionElevator);
            beaconmanager.startMonitoring(regionRoom);
        }
    }
}
