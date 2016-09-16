package it.polimi.polidemonstrator.businesslogic.businessrules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by saeed on 9/16/2016.
 */
public class MyAlarmManagerReceiver extends BroadcastReceiver {


    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle basket=intent.getExtras();
        Intent i = new Intent(context, AlarmManagerService.class);
        i.putExtras(basket);
        context.startService(i);
    }
}