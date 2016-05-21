package it.polimi.polidemonstrator.businessLogic;

import android.content.Context;

/**
 * Created by saeed on 5/20/2016.
 */
public class Building {

    public Building(Context context) {
        ServerURL serverURL=new ServerURL();
        this.serverURL=serverURL.getServerURL(context);
    }

    private static String serverURL;
    private String buildingid;


    public String getBuildingid() {
        return buildingid;
    }

    public void setBuildingid(String buildingid) {
        this.buildingid = buildingid;
    }


    public static String getServerURL() {
        return serverURL;
    }

    public static void setServerURL(String serverURL) {
        Building.serverURL = serverURL;
    }






}
