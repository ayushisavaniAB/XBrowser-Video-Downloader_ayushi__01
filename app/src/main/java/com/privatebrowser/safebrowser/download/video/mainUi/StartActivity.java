package com.privatebrowser.safebrowser.download.video.mainUi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityStartBinding;
import com.privatebrowser.safebrowser.download.video.promoScreen.promoScreen1;
import com.privatebrowser.safebrowser.download.video.promoScreen.promoScreen2;
import think.outside.the.box.handler.APIManager;

public class StartActivity extends BaseActivity {

    ActivityStartBinding binding;
    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        APIManager.showNative(binding.adsNative200);
     //   APIManager.showBanner(binding.baneerads);
        initListener();

    }

    private void initListener() {


        binding.start.setOnClickListener(v -> {
            APIManager.showInter(this, false, b -> {
                if (APIManager.getStartScreenCount() == 1) {
                    Intent intent = new Intent(this, WelComeActivity.class);
                    intent.putExtra("settingScreen", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    startActivity(new Intent(this, promoScreen1.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }

            });
        });

        binding.privacy.setOnClickListener(v -> {
            startActivity(new Intent(this, PrivacyActivity.class));
        });

        binding.share.setOnClickListener(v -> {
            String app = getString(R.string.app_name);
            Intent share = new Intent("android.intent.action.SEND");
            share.setType("text/plain");
            share.putExtra("android.intent.extra.TEXT", app + "\n\n" + "Open this Link on Play Store" + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(Intent.createChooser(share, "Share Application"));
        });
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}