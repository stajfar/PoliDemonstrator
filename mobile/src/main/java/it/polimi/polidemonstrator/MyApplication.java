package it.polimi.polidemonstrator;

import android.app.Application;
import android.content.Context;
import android.net.http.HttpResponseCache;

import android.support.multidex.MultiDex;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import it.polimi.polidemonstrator.businesslogic.ServerURL;

/**
 * Created by saeed on 5/9/2016.
 */
public class MyApplication extends Application {

    private String jsonServerURL;
    public String getJsonServerURL() {
        return jsonServerURL;
    }




    @Override
    public void onCreate() {
        super.onCreate();

        allocateCacheToApplication();
        setJsonServerAddress();


    }

    private void setJsonServerAddress() {
        ServerURL serverUrl=new ServerURL();
        jsonServerURL= serverUrl.getServerURL(this);
    }

    private void allocateCacheToApplication() {
        //install the http response cache for the application
        try {
            File httpCacheDir = new File(MyApplication.this.getCacheDir(), "http");
            long httpCacheSize = 8 * 1024 * 1024; // 5 MiB of cache space
            HttpResponseCache.install(httpCacheDir, httpCacheSize);}
        catch(IOException e){
            Toast.makeText(this,
                    "Failed to allocate cache space!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }





}
