package it.polimi.polidemonstrator.businesslogic.businessrules;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polimi.polidemonstrator.businesslogic.ListenerServiceFromWear;

/**
 * Created by saeed on 6/15/2016.
 */
public class JSON_Ruler  implements Serializable {

    private static AlarmManager alarmMgr;
    private static PendingIntent alarmIntent;

    private int userRuleIntervalCheck;
    private int ruleID;
    private String userState;
    private boolean ruleIntervalCheckEnabled;

    private int rulePriority;


    private String ruleExpression;
    private String actionClass;
    private String actionMessageTitle;
    private String actionMessageText;


    public JSON_Ruler(String userState,boolean ruleIntervalCheckEnabled,int userRuleIntervalCheck,int rulePriority, String actionClass, String ruleExpression ,String actionMessageTitle,String actionMessageText,int ruleID) {
        this.ruleID=ruleID;
        this.userState=userState;
        this.ruleIntervalCheckEnabled=ruleIntervalCheckEnabled;
        this.userRuleIntervalCheck=userRuleIntervalCheck;
        this.rulePriority= rulePriority;
        this.actionClass = actionClass;
        this.actionMessageTitle = actionMessageTitle;
        this.actionMessageText = actionMessageText;
        this.ruleExpression = ruleExpression;
    }



    public int getRuleID() {
        return ruleID;
    }

    public String getUserState() {
        return userState;
    }

    public int getRulePriority() {
        return rulePriority;
    }

    public int userRuleIntervalCheck() {
        return userRuleIntervalCheck;
    }

    public boolean isruleIntervalCheckEnabled() {
        return ruleIntervalCheckEnabled;
    }
    public String getActionClass() {
        return actionClass;
    }

    public String getActionMessageText() {
        return actionMessageText;
    }

    public String getActionMessageTitle() {
        return actionMessageTitle;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }



// set of functions

    public static void fetchCorrelatedUserRulesFromCloud(Context context, int roomID,
                                                         boolean isRefresh, String myMessage) {

        new BackgroundTaskGetRoomCorrelatedRulesFromCloud(roomID,isRefresh,context,myMessage).execute();




    }



    //Async Task to fetch API server url form web server
    public static class BackgroundTaskGetRoomCorrelatedRulesFromCloud extends AsyncTask<String, Void,   List<JSON_Ruler>> {
        int roomID;
        boolean isRefresh;
        Context context;
        String myMessage;

        public BackgroundTaskGetRoomCorrelatedRulesFromCloud(int roomID, boolean isReferesh, Context context, String myMessage) {
            this.roomID = roomID;
            this.isRefresh = isReferesh;
            this.context = context;
            this.myMessage=myMessage;
        }

        @Override
        protected   List<JSON_Ruler> doInBackground(String... params) {
            String roomCorrelatedRulesList_Json = getRoomCorrelatedRulesJson(roomID, isRefresh);
            List<JSON_Ruler> rules_parsed=null;
            if (roomCorrelatedRulesList_Json != null) {
               //Pars the correlated rules JSON file
               rules_parsed=  parsCorrelatedRoomRules(roomCorrelatedRulesList_Json,myMessage);

            }
            return rules_parsed;
        }



        @Override
        protected void onPostExecute(  List<JSON_Ruler> rules_parsed) {
            super.onPostExecute(rules_parsed);
            //now the parsed results are ready and we can call the

            //cancel previous AlarmManagers, if exists any
            cancelAlarmSchedule();

            //Send a Notification to user
            if(rules_parsed != null && rules_parsed.size() > 0){
                // let's filter the rules to see which rules should be evaluated immediately and which ones should
                // be scheduled by a service
                List<JSON_Ruler> intervalCheckUserRules_Parsed=new ArrayList<>();
                List<JSON_Ruler> immediateCheckUserRules_Parsed=new ArrayList<>();

                for (JSON_Ruler json_ruler: rules_parsed){
                    if (json_ruler.ruleIntervalCheckEnabled==true){
                        //these rules should be evaluated by an interval that is decided by the rule designer based on Minutes
                        intervalCheckUserRules_Parsed.add(json_ruler);
                    }else
                    {
                        //These rules should be evaluated immediately as soon as user's state changes
                        immediateCheckUserRules_Parsed.add(json_ruler);
                    }
                }
                //Evaluate the rules that should be executed immediately
                if(immediateCheckUserRules_Parsed.size() > 0) {
                    new ListenerServiceFromWear.BackgroudTaskRuleFactGenerator(context, roomID, immediateCheckUserRules_Parsed).execute();
                }
                if(intervalCheckUserRules_Parsed.size()> 0){
                    //Schedule a new alarm manager
                   scheduleAlarm( roomID, intervalCheckUserRules_Parsed,intervalCheckUserRules_Parsed.get(0).userRuleIntervalCheck());
                }

            }
        }

        private void cancelAlarmSchedule() {
                Intent intent = new Intent(context, MyAlarmManagerReceiver.class);
            // 0 argument is the id of the Alarm manager and is used to identify it
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                alarm.cancel(pendingIntent);

        }

        private void scheduleAlarm(int roomID, List<JSON_Ruler> intervalCheckUserRules_Parsed, int rulesIntervalCheck) {
            //set an alarm manager to check for those rules which should be evaluated when the user is stable in his state for a long time.
            // to do this an Alarm Manager is set when users enters a new state. Alarm manager runs an intent service( which runs in a background
            //Thread) by an interval. whenever, user changes his state, this alarm manager will be canceled and a new one will be generated based on
            // his new state.

            // Construct an intent that will execute the AlarmReceiver
            Intent intent = new Intent(this.context, MyAlarmManagerReceiver.class);
            Bundle basket=new Bundle();
            basket.putInt("roomID", roomID);
            basket.putSerializable("list_Json_Ruler",(Serializable)intervalCheckUserRules_Parsed);
            intent.putExtras(basket);

            // 0 parameter defines AlarmMAnager instance id and is useful to identify this instance later (i.e., to cancel it)
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager=(AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60*1000,rulesIntervalCheck*60*1000,
                    pendingIntent);

        }

        private List<JSON_Ruler> parsCorrelatedRoomRules(String roomCorrelatedRulesList_Json, String myMessage) {
            try {
                JSONObject jsonObject=new JSONObject(roomCorrelatedRulesList_Json);
                int userRuleIntervalCheck = jsonObject.getInt("userRuleIntervalCheck");
                int count=0;

                List<JSON_Ruler> listJsonRules_parsed=new ArrayList<>();
                JSONArray jsonArrayRules=jsonObject.getJSONArray("rules");
                while (count< jsonArrayRules.length())  {

                    JSONObject jsonObjectRule=jsonArrayRules.getJSONObject(count);
                    if(myMessage.equals(jsonObjectRule.getString("userState"))) {

                        int ruleID=jsonObjectRule.getInt("ruleID");
                        String userState = jsonObjectRule.getString("userState");
                        boolean ruleIntervalCheckEnabled = jsonObjectRule.getBoolean("ruleIntervalCheckEnabled");

                        int rulePriority = jsonObjectRule.getInt("rulePriority");
                        String ruleExpression = jsonObjectRule.getString("ruleExpression");
                        String actionClass = jsonObjectRule.getString("actionClass");
                        String actionMessageTitle = jsonObjectRule.getString("actionMessageTitle");
                        String actionMessageText = jsonObjectRule.getString("actionMessageText");

                        JSON_Ruler json_ruler = new JSON_Ruler(userState, ruleIntervalCheckEnabled, userRuleIntervalCheck, rulePriority,
                                actionClass, ruleExpression, actionMessageTitle, actionMessageText,ruleID);

                        listJsonRules_parsed.add(json_ruler);
                    }
                    count++;
                }
                return listJsonRules_parsed;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        private  String getRoomCorrelatedRulesJson(int roomID,boolean isRefresh) {
            String JSON_STRING;
            //// TODO: 6/6/2016 complete the rules url by roomID
            String rulesURL="https://api.myjson.com/bins/3g5rm";
            //"https://api.myjson.com/bins/2vze5"; //serverURL+"/beacons/room/"+String.valueOf(roomID);
            try {
                URL url = new URL(rulesURL);
                HttpURLConnection httpconnection=(HttpURLConnection)url.openConnection();
                if (isRefresh==true) {
                    httpconnection.setUseCaches(false);
                }
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                httpconnection.addRequestProperty("Cache-Control", "max-stale=" + maxStale);
                InputStream inputStream=httpconnection.getInputStream();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder=new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpconnection.disconnect();

                String json_String=stringBuilder.toString().trim();

                return json_String;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
