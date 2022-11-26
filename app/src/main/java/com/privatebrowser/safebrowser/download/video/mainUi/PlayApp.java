package com.privatebrowser.safebrowser.download.video.mainUi;

import android.content.Intent;


import com.privatebrowser.safebrowser.download.video.BuildConfig;
import com.privatebrowser.safebrowser.download.video.download.DownloadManager;
import think.outside.the.box.AppClass;
import think.outside.the.box.handler.APIManager;

public class PlayApp extends AppClass {

    public static PlayApp instance;
    public Intent downloadService;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        downloadService = new Intent(getApplicationContext(), DownloadManager.class);
        if (BuildConfig.DEBUG) {
            APIManager.isLog = true;
        }
        setClass(SplashActivity.class);
    }

    public static PlayApp getInstance() {
        return instance;
    }

    public Intent getDownloadService() {
        return downloadService;
    }

}
