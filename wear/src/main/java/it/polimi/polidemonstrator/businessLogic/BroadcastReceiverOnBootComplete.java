package it.polimi.polidemonstrator.businessLogic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by saeed on 6/24/2016.
 */
public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, AndroidServiceStartOnBoot.class);
            context.startService(serviceIntent);
        }
    }
}