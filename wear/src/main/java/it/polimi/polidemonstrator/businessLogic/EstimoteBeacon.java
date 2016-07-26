package it.polimi.polidemonstrator.businesslogic;





import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saeed on 6/6/2016.
 */
public class EstimoteBeacon implements Serializable {

    private String identifier;
    private String UUID;
    private int minor;
    private int major;


    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    private EstimoteBeacon() {

    }


    public  static   List<EstimoteBeacon> parsJSON_Beacons(String json_results) {
        List<EstimoteBeacon> listEstimoteBeacon=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(json_results);
            int count=0;
            while (count< jsonArray.length())
            {
                EstimoteBeacon estimoteBeacon=new EstimoteBeacon();
                JSONObject jsonObject=jsonArray.getJSONObject(count);
                if (!jsonObject.getString("UUID").equals("null")) {
                    estimoteBeacon.setIdentifier(jsonObject.getString("identifier"));
                    estimoteBeacon.setUUID(jsonObject.getString("UUID"));
                    estimoteBeacon.setMajor(jsonObject.getInt("major"));
                    estimoteBeacon.setMinor(jsonObject.getInt("minor"));
                    listEstimoteBeacon.add(estimoteBeacon);
                }
                count++;
            }
            return listEstimoteBeacon;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
