package it.polimi.polidemonstrator;

import android.content.Context;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import it.polimi.polidemonstrator.businesslogic.RuleFactGenerator;
import it.polimi.polidemonstrator.businesslogic.businessrules.TestClass;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                   // Intent openmenu = new Intent("android.intent.action.CHART_LINECHART");
                   // startActivity(openmenu);
                    Intent openmenu = new Intent("android.intent.action.ROOM_SELECTOR");
                    startActivity(openmenu);
                }
            }

        };
        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }










}
