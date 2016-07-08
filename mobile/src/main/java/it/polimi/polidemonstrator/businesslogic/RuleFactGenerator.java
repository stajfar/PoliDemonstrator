package it.polimi.polidemonstrator.businesslogic;

import android.content.Context;

import java.util.AbstractMap;

import java.util.Arrays;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import it.polimi.polidemonstrator.R;

/**
 * Created by saeed on 6/17/2016.
 */
public class RuleFactGenerator {

    Context context;


    public RuleFactGenerator(Context context) {
        this.context = context;

    }

    public  Map<String, String> factGenerator(int roomId){
        Room room=new Room(context);
        String json_roomVariables= room.getRoomMeasurementVariables_JSON(String.valueOf(roomId));
        //get list of desired variables from String.xml
       String[] instantMeasurementVariables= context.getResources().getStringArray(R.array.InstantMeasurementVariables);
        List<String> listDesiredStringVariables = Arrays.asList(instantMeasurementVariables);
       //fetch room's desired variables by http connection
        Map<String,List<Integer>> mapVariablesLabelsWithVariableIDs=room.parsRoomSensorVariablesJSON(json_roomVariables,listDesiredStringVariables);

        //now we have to decide values of each key which should be fetched by http instant
        //add latest Sensor Data as key Value pairs
        // for test purpose define a variable binding ...
        Map<String, String> mapBindings = new HashMap<>();
        for(String mapkeyVariableLabel: mapVariablesLabelsWithVariableIDs.keySet()){
            List<Integer> variableIDs=mapVariablesLabelsWithVariableIDs.get(mapkeyVariableLabel);
            MeasurementClass measurementClass=new MeasurementClass(context);

            Float measurementInstantValue=0f;
            int count=0;
            for(int variableID: variableIDs){
                if(measurementClass.getInstantMeasurementVariableValue(variableID) != null) {
                    measurementInstantValue += measurementClass.getInstantMeasurementVariableValue(variableID);
                    count++;
                }else if(count == 0){
                    //this means all the sensors are dead (no battery)
                    measurementInstantValue=null;
                }
            }
            if(count >0) {
                //make the average of different values of different sensors (i.e., there are multiple indoor temperature sensors)
                measurementInstantValue = measurementInstantValue / count;
            }



            //Generate the correlated rule
            Map.Entry<String,String> entry=ruleBindignGenerator(mapkeyVariableLabel,measurementInstantValue);
            if(entry != null) {
                mapBindings.put(entry.getKey(), entry.getValue());
            }


        }

        return mapBindings;


        //if user leaves the office FF happens, in this case check lights consumtion

        //if user Enters the office TT happens, Start Alarm Manager in this case check windows open and Air conditioner values

        //Read the rules that should be checked and if necessary executed at FF for instance

        // from the given rules, match the rule-keys with variable-lables and make http request to fetch those variable instant
        //values

        //make the appropriate strings from given values

        //evalute the rules
    }



    private Map.Entry<String,String> ruleBindignGenerator(String mapkeyVariableLabel, Float measurementInstantValue) {
        Map.Entry<String, String> entry=null;

        if (measurementInstantValue != null && measurementInstantValue > 0) {
            entry = new AbstractMap.SimpleEntry<String, String>(mapkeyVariableLabel.replaceAll(" ","_"), "'O'");
        } else if (measurementInstantValue != null && measurementInstantValue == 0) {
            entry = new AbstractMap.SimpleEntry<String, String>(mapkeyVariableLabel.replaceAll(" ","_"), "'C'");
        }
        return entry;
    }

}
