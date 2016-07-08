package it.polimi.polidemonstrator.businesslogic;

/**
 * Created by saeed on 5/20/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import it.polimi.polidemonstrator.R;




/**
 * Created by saeed on 4/27/2016.
 */
public class ServerURL {

    public String getServerURL(Context context){


        String fullAPIUrl=null;
        //run a background task to get the API server url form the json file which is uploaded into web server
        String webServerURL= context.getString(R.string.server_url);
        try {
            if(InternetConnection.isInternetConnected(context)) {
                String results = new BackgroundTaskGetAPIServer().execute(webServerURL).get();
                if (results != null) {
                    fullAPIUrl = ParsServerURL(results);
                }
                else{
                 Toast.makeText(context,
                         "Cannot retrieve Cloud URL! \n" +
                                 "Check web server!",
                         Toast.LENGTH_SHORT).show();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return fullAPIUrl;
    }



    //Async Task to fetch API server url form web server
    private class BackgroundTaskGetAPIServer extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String webserverURL=params[0];
            String aPIServerJson=getAPIjsonFromWebServer(webserverURL);
            return aPIServerJson;
        }

        private String getAPIjsonFromWebServer(String webserverURL) {
            String JSON_STRING;
            try {
                URL url = new URL(webserverURL);
                HttpURLConnection httpconnection = (HttpURLConnection) url.openConnection();
                httpconnection.setDoOutput(true);
                InputStream inputStream = httpconnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpconnection.disconnect();
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String results) {

        }

    }


    private  String ParsServerURL(String json_results) {
        try {

            JSONObject jsonObject=new JSONObject(json_results);

            String ip;
            String port;
            ip=jsonObject.getString("ip");
            port=jsonObject.getString("port");
            String parsedUrl="http://"+ip+":"+port;

            return parsedUrl;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
