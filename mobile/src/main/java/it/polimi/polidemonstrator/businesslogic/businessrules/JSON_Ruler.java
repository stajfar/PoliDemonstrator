package it.polimi.polidemonstrator.businesslogic.businessrules;


import android.content.Context;
import android.os.AsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
public class JSON_Ruler {
    private String userState;
    private boolean userStability;
    private int stableUserRuleIntervalCheck;
    private int rulePriority;

    private String ruleExpression;
    private String actionClass;
    private String actionMessageTitle;
    private String actionMessageText;


    public JSON_Ruler(String userState,boolean userStability,int stableUserRuleIntervalCheck,int rulePriority, String actionClass, String ruleExpression ,String actionMessageTitle,String actionMessageText) {
        this.userState=userState;
        this.userStability=userStability;
        this.stableUserRuleIntervalCheck=stableUserRuleIntervalCheck;
        this.rulePriority= rulePriority;
        this.actionClass = actionClass;
        this.actionMessageTitle = actionMessageTitle;
        this.actionMessageText = actionMessageText;
        this.ruleExpression = ruleExpression;
    }





    public String getUserState() {
        return userState;
    }

    public int getRulePriority() {
        return rulePriority;
    }

    public int getStableUserRuleIntervalCheck() {
        return stableUserRuleIntervalCheck;
    }

    public boolean isUserStability() {
        return userStability;
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

            //Send a Notification to user
            if(rules_parsed != null && rules_parsed.size() > 0)
            new ListenerServiceFromWear.BackgroudTaskRuleFactGenerator(context,roomID,rules_parsed).execute();


        }

        private List<JSON_Ruler> parsCorrelatedRoomRules(String roomCorrelatedRulesList_Json, String myMessage) {
            try {
                JSONArray jsonArray=new JSONArray(roomCorrelatedRulesList_Json);
                int count=0;

                List<JSON_Ruler> listJsonRules_parsed=new ArrayList<>();
                while (count< jsonArray.length())  {

                    JSONObject jsonObject=jsonArray.getJSONObject(count);
                    if(myMessage.equals(jsonObject.getString("userState"))) {

                        String userState = jsonObject.getString("userState");
                        boolean userStability = jsonObject.getBoolean("userStability");
                        int stableUserRuleIntervalCheck = jsonObject.getInt("stableUserRuleIntervalCheck");
                        int rulePriority = jsonObject.getInt("rulePriority");
                        String ruleExpression = jsonObject.getString("ruleExpression");
                        String actionClass = jsonObject.getString("actionClass");
                        String actionMessageTitle = jsonObject.getString("actionMessageTitle");
                        String actionMessageText = jsonObject.getString("actionMessageText");

                        JSON_Ruler json_ruler = new JSON_Ruler(userState, userStability, stableUserRuleIntervalCheck, rulePriority,
                                actionClass, ruleExpression, actionMessageTitle, actionMessageText);

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
            String rulesURL="https://api.myjson.com/bins/2vze5"; //serverURL+"/beacons/room/"+String.valueOf(roomID);
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
