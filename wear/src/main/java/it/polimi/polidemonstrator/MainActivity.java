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

import it.polimi.polidemonstrator.businessLogic.SendMessageServiceToHandheld;

public class MainActivity extends Activity {

    private TextView mTextView;
    private Button btnEnterReg,btnExitReg;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                btnEnterReg = (Button) stub.findViewById(R.id.btnenter);
                btnExitReg = (Button) stub.findViewById(R.id.btnexit);

                btnEnterReg.setOnClickListener(new btnEnterRegClicked());
                btnExitReg.setOnClickListener(new btnExitRegClicked());
            }
        });

        context=this.getApplicationContext();


    }


    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }


    //testings purposes


    private void mytestFunction() {


        //consider it as entering to region
        MyNotification.showNotification(this, MainActivity.class,
                "Entring the region",
                "sending msg");


    }

    private class btnEnterRegClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,
                    "enter",
                    Toast.LENGTH_SHORT).show();
            MyNotification.showNotification(context, MainActivity.class,
                    "Entring the region",
                    "sending msg");



            //Room room=new Room(MyApplication.this);
            //room.setRoomid("1");
            //new BackgroundTaskGetMeasurementList(room,true).execute();


            startService(new Intent(context,SendMessageServiceToHandheld.class));
            


        }
    }

    private class btnExitRegClicked implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context,
                    "exit",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
