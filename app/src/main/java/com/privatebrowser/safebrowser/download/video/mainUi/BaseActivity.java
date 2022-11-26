package com.privatebrowser.safebrowser.download.video.mainUi;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.NonNull;

import java.util.Locale;

import com.privatebrowser.safebrowser.download.video.utils.Utility;
import com.privatebrowser.safebrowser.download.video.videos.GlobalVar;
import com.privatebrowser.safebrowser.download.video.videos.service.VideoService;
import think.outside.the.box.util.TinyDB;


public abstract class BaseActivity extends think.outside.the.box.ui.BaseActivity {

    GlobalVar mGlobalVar = GlobalVar.getInstance();
    boolean isNeedResumePlay = false;
    Bitmap bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utility.hideNavigation(this);
        super.onCreate(savedInstanceState);

        inItService();

        String langCode = new TinyDB(this).getString("LangCode");
        setLocale(this, langCode);
    }

    public static void setLocale(Context activity, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bmp != null) {
            bmp.recycle();
        }
        if (GlobalVar.getInstance().videoService != null)
            unbindService(videoServiceConnection);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected ServiceConnection videoServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VideoService.VideoBinder binder = (VideoService.VideoBinder) service;
            GlobalVar.getInstance().videoService = binder.getService();

            if (isNeedResumePlay) startPopupMode();
            if (mGlobalVar.isOpenFromIntent) {
                mGlobalVar.isOpenFromIntent = false;
                mGlobalVar.videoService.playVideo(0, false);
                showFloatingView(BaseActivity.this, true);
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isConnection = false;
        }
    };
    boolean isConnection = false;
    protected static Intent _playIntent;

    public void inItService() {
        _playIntent = new Intent(this, VideoService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(_playIntent);
            } else {
                startService(_playIntent);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return;
        }

        bindService(_playIntent, videoServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void startPopupMode() {
        if (_playIntent != null) {
            GlobalVar.getInstance().videoService.playVideo(mGlobalVar.seekPosition, true);
        }
    }

    public void stopPopupMode() {
        if (_playIntent != null) {
            GlobalVar.getInstance().videoService.removePopup();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    int requestCode = 1;

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == resultCode) {
            isNeedResumePlay = true;
            startPopupMode();

        } else {

        }
    }

    @SuppressLint("NewApi")
    public void showFloatingView(Context context, boolean isShowOverlayPermission) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startPopupMode();
            return;
        }

        if (Settings.canDrawOverlays(context)) {
            startPopupMode();
            return;
        }

        if (isShowOverlayPermission) {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
