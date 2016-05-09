package it.polimi.polidemonstrator;

import android.app.Application;
import android.net.http.HttpResponseCache;

import java.io.File;
import java.io.IOException;

/**
 * Created by saeed on 5/9/2016.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //install the http response cache for the application
        try {
            File httpCacheDir = new File(MyApplication.this.getCacheDir(), "http");
            long httpCacheSize = 2 * 1024 * 1024; // 2 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);}
        catch(IOException e){
            //// TODO: 5/9/2016  show a toast message
        }
    }


}
