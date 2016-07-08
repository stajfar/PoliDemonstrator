package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;
import android.content.SharedPreferences;



/**
 * Created by saeed on 6/29/2016.
 */
public class MyPreferences {
   public static void savePreferencesRoomSelector(Context context,int spinnerBuildingPosition, int spinnerRoomPosition){
       // Store values between instances here
       SharedPreferences preferences = context.getSharedPreferences("roomSelector", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = preferences.edit();
       // Put the values from the UI
       editor.putInt("spinnerBuildingPosition", spinnerBuildingPosition); // value to store
       editor.putInt("spinnerRoomPosition", spinnerRoomPosition); // value to store

       // Commit to storage
       editor.commit();
   }

    public static int getPreferenceBuilding(Context context){
        // Get the between instance stored values
        SharedPreferences preferences = context.getSharedPreferences("roomSelector", Context.MODE_PRIVATE);

        int spinnerBuildingPosition=preferences.getInt("spinnerBuildingPosition",0);
        return spinnerBuildingPosition;
    }

    public static int getPreferenceRoom(Context context){
        // Get the between instance stored values
        SharedPreferences preferences = context.getSharedPreferences("roomSelector", Context.MODE_PRIVATE);
        int spinnerRoomPosition=preferences.getInt("spinnerRoomPosition",0);
        return spinnerRoomPosition;
    }

    public static void clearPreferenceRoom(Context context){
        SharedPreferences preferences = context.getSharedPreferences("roomSelector", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.clear();
        editor.commit();


    }
}
