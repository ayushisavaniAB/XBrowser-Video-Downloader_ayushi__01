package com.privatebrowser.safebrowser.download.video.promoScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityPromoScreen1Binding;
import com.privatebrowser.safebrowser.download.video.mainUi.WelComeActivity;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.ui.BaseActivity;

public class promoScreen1 extends BaseActivity {

    ActivityPromoScreen1Binding binding;
    private boolean doubleBackToExitPressedOnce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);

        binding = ActivityPromoScreen1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        APIManager.showNative(binding.adsNative200);
     //   APIManager.showBanner(binding.baneerAds);
        initListener();
    }

    private void initListener() {
        binding.btnNext.setOnClickListener(v -> {
            if (APIManager.getStartScreenCount() == 1) {
                APIManager.showInter(this, false, b -> {
                    Intent intent = new Intent(this, WelComeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                });
            }else
                APIManager.showInter(this, false, b -> {
                    Intent intent = new Intent(this, promoScreen2.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                });
        });
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce)
        {
            finishAffinity();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}