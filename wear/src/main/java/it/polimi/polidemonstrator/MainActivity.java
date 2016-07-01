package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;

import java.util.List;

import it.polimi.polidemonstrator.businessLogic.BeaconMonitoring;
import it.polimi.polidemonstrator.businessLogic.MeasurementClass;
import it.polimi.polidemonstrator.businessLogic.Room;
import it.polimi.polidemonstrator.businessLogic.SendMessageServiceToHandheld;
import it.polimi.polidemonstrator.businessLogic.StateMachine;

public class MainActivity extends Activity {


    private Button btnEnterFloor;
    //,btnExitFloor,btnEnterRoom,btnExitRoom;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;





        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
               // mTextView = (TextView) stub.findViewById(R.id.text);
               btnEnterFloor = (Button) stub.findViewById(R.id.btnenterFloor);
               // btnExitFloor = (Button) stub.findViewById(R.id.btnexitFloor);
               // btnEnterRoom = (Button) stub.findViewById(R.id.btnenterRoom);
                //btnExitRoom = (Button) stub.findViewById(R.id.btnexitRoom);

               btnEnterFloor.setOnClickListener(new btnEnterFloorClicked());
              //  btnExitFloor.setOnClickListener(new btnExitFloorClicked());
              //  btnEnterRoom.setOnClickListener(new btnEnterRoomClicked());
              //  btnExitRoom.setOnClickListener(new btnExitRoomClicked());
            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();

       // SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }



   // StateMachine.State oldState=StateMachine.State.FF;
    private class btnEnterFloorClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // new BeaconMonitoring(this);
            new BackgroundTaskGetBeaconList().execute();

            /*Toast.makeText(context,
                    "Enter elv",
                    Toast.LENGTH_SHORT).show();
            //happened event
            StateMachine.Symbols newInputEvent=StateMachine.Symbols.Elv_in;

            StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInputEvent.ordinal()];

            if(oldState== StateMachine.State.TF && newState == StateMachine.State.FF){
                //this means that user is going to leave the building monitor if everything is fine
               startService(new Intent(context,SendMessageServiceToHandheld.class).putExtra("myMessage","userLeaving"));






                //room.setRoomid("1");
                //new BackgroundTaskGetMeasurementList(room,true).execute();
            }

            //ok everything is down and we have to update old state by new state
            oldState=newState;   */
        }
    }


/*

    private class btnExitFloorClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,
                    "Exit floor",
                    Toast.LENGTH_SHORT).show();

            StateMachine.Symbols newInput=StateMachine.Symbols.Elv_out;
            StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInput.ordinal()];

            oldState=newState;
        }
    }

    private class btnEnterRoomClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,
                    "Enter room",
                    Toast.LENGTH_SHORT).show();

            StateMachine.Symbols newInput=StateMachine.Symbols.Rm_in;
            StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInput.ordinal()];
            oldState=newState;


        }
    }

    private class btnExitRoomClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,
                    "Exit room",
                    Toast.LENGTH_SHORT).show();

            StateMachine.Symbols newInput=StateMachine.Symbols.Rm_out;
            StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInput.ordinal()];
            oldState=newState;

        }
    }

*/

    //Async Task to fetch Sensors Class list of a given room ID
    public class BackgroundTaskGetBeaconList extends AsyncTask<Void, Void, Void> {


        public BackgroundTaskGetBeaconList() {

        }

        @Override
        protected Void doInBackground(Void... params) {
            //send a message by service to handheld, requesting beacons of the room
            String myMessagePath=getResources().getString(R.string.messagepath_beacon);
            String myMessage=getResources().getString(R.string.message_fetchBeaconList);
            startService(new Intent(context,
                    SendMessageServiceToHandheld.class)
                    .putExtra("myMessagePath",myMessagePath).putExtra("myMessage",myMessage)
                    .putExtra("myMessageType",SendMessageServiceToHandheld.MyWear_HandheldMessageAPIType.SendThroughMessageAPI.ordinal()));


            return null;
        }



    }
}
