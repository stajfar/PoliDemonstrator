package it.polimi.polidemonstrator.businesslogic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by saeed on 5/31/2016.
 */
public class BootServiceStarter  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, ListenerServiceFromWear.class);
            context.startService(pushIntent);
        }
    }
}