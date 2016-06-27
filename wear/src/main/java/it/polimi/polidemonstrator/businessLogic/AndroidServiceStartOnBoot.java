package it.polimi.polidemonstrator.businessLogic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/24/2016.
 */
public class AndroidServiceStartOnBoot extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // here you can add whatever you want this service to do
        String myMessagePath=getResources().getString(R.string.messagepath_beacon);
        String myMessage=getResources().getString(R.string.message_fetchBeaconList);
        startService(new Intent(this,
                SendMessageServiceToHandheld.class).putExtra("myMessagePath",myMessagePath).putExtra("myMessage",myMessage));

    }
}
