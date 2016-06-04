package it.polimi.polidemonstrator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;

import javax.sql.StatementEvent;

import it.polimi.polidemonstrator.businessLogic.SendMessageServiceToHandheld;
import it.polimi.polidemonstrator.businessLogic.StateMachine;

public class MainActivity extends Activity {

    private TextView mTextView;
    private Button btnEnterFloor,btnExitFloor,btnEnterRoom,btnExitRoom;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
               // mTextView = (TextView) stub.findViewById(R.id.text);
                btnEnterFloor = (Button) stub.findViewById(R.id.btnenterFloor);
                btnExitFloor = (Button) stub.findViewById(R.id.btnexitFloor);
                btnEnterRoom = (Button) stub.findViewById(R.id.btnenterRoom);
                btnExitRoom = (Button) stub.findViewById(R.id.btnexitRoom);

                btnEnterFloor.setOnClickListener(new btnEnterFloorClicked());
                btnExitFloor.setOnClickListener(new btnExitFloorClicked());
                btnEnterRoom.setOnClickListener(new btnEnterRoomClicked());
                btnExitRoom.setOnClickListener(new btnExitRoomClicked());
            }
        });

        context=this.getApplicationContext();


    }


    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }



    StateMachine.State oldState=StateMachine.State.FF;
    private class btnEnterFloorClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,
                    "Enter elv",
                    Toast.LENGTH_SHORT).show();
            //happened event
            StateMachine.Symbols newInputEvent=StateMachine.Symbols.Elv_in;

            StateMachine.State newState = StateMachine.transition[oldState.ordinal()][newInputEvent.ordinal()];

            if(oldState== StateMachine.State.TF && newState == StateMachine.State.FF){
                //this means that user is going to leave the building monitor if everything is fine
                startService(new Intent(context,SendMessageServiceToHandheld.class));
                //Room room=new Room(MyApplication.this);
                //room.setRoomid("1");
                //new BackgroundTaskGetMeasurementList(room,true).execute();
            }

            //ok everything is down and we have to update old state by new state
            oldState=newState;
        }
    }

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
}
