package com.privatebrowser.safebrowser.download.video.promoScreen;

import android.content.Intent;
import android.os.Bundle;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityPromoScreen3Binding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.mainUi.WelComeActivity;
import think.outside.the.box.handler.APIManager;

public class promoScreen3 extends BaseActivity {

    ActivityPromoScreen3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);
        binding = ActivityPromoScreen3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
         APIManager.showBanner(binding.adsBanner65);
        initListener();
    }

    private void initListener() {
        binding.btnNext.setOnClickListener(v -> {
            APIManager.showInter(this, false, b -> {
                if (APIManager.getStartScreenCount() == 3) {
                    startActivity(new Intent(this, WelComeActivity.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                } else {
                    startActivity(new Intent(this, promoScreen4.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            });
        });
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(this, true, b -> {
            finish();
        });
    }
}