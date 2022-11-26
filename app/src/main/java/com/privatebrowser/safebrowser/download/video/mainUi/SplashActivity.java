package com.privatebrowser.safebrowser.download.video.mainUi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivitySplashBinding;
import com.privatebrowser.safebrowser.download.video.download.bookmark.BookmarksSQLite;
import com.privatebrowser.safebrowser.download.video.utils.Converters;
import com.privatebrowser.safebrowser.download.video.utils.PreferenceUtil;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.callback.SplashCallback;
import think.outside.the.box.handler.APIManager;

public class SplashActivity extends BaseActivity {

    ActivitySplashBinding binding;
    PreferenceUtil preferenceUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceUtil = new PreferenceUtil(SplashActivity.this);

        runOnlyOneTime();

        APIManager.initializeSplash(this, new SplashCallback() {
            @Override
            public void onSuccess() {
                APIManager.showSplashAD(SplashActivity.this, new AdsCallback() {
                    @Override
                    public void onClose(boolean b) {
                        if (APIManager.hasStartScreen()) {
                            startActivity(new Intent(SplashActivity.this, StartActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, WelComeActivity.class));
                        }
                    }
                });
            }
        });

    }

    public void runOnlyOneTime() {
        if (preferenceUtil.getBoolean("FirstRun", true)) {
            BookmarksSQLite bookmarksSQLite = new BookmarksSQLite(this);
            bookmarksSQLite.add(Converters.getImageFromResource(this, R.drawable.veoh_icon), "Veoh", "https://www.veoh.com/");
            bookmarksSQLite.add(Converters.getImageFromResource(this, R.drawable.imdb_icon), "imdb", "https://m.imdb.com/");
            bookmarksSQLite.add(Converters.getImageFromResource(this, R.drawable.ic_tik), getString(R.string.tiktok), "https://www.tiktok.com/");
            preferenceUtil.putBoolean("FirstRun", false);
        }
    }
}