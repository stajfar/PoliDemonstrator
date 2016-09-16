package it.polimi.polidemonstrator.businesslogic.businessrules;

import android.app.Application;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import it.polimi.polidemonstrator.businesslogic.ListenerServiceFromWear;

/**
 * Created by saeed on 9/15/2016.
 */
public class AlarmManagerService extends Service {
    Context context;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            context = getApplicationContext();

            Bundle basket = intent.getExtras();
            int roomID = basket.getInt("roomID");
            List<JSON_Ruler> intervalCheckUserRules_Parsed = (List<JSON_Ruler>) basket.getSerializable("list_Json_Ruler");


            //receive the bundle parameters from intent and start to evaluate the  rules
            new ListenerServiceFromWear.BackgroudTaskRuleFactGenerator(context, roomID, intervalCheckUserRules_Parsed).execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }
}