package it.polimi.polidemonstrator.businessLogic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by saeed on 5/20/2016.
 */
public class InternetConnection {
    public static boolean isInternetConnected(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected==false){
            Toast.makeText(context,
                    "No Internet!",
                    Toast.LENGTH_SHORT).show();
        }

        return  isConnected;
    }
}
