package com.privatebrowser.safebrowser.download.video.promoScreen;

import android.content.Intent;
import android.os.Bundle;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityPromoScreen5Binding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.mainUi.WelComeActivity;
import think.outside.the.box.handler.APIManager;

public class promoScreen5 extends BaseActivity {

    ActivityPromoScreen5Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);
        binding = ActivityPromoScreen5Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        APIManager.showBanner(binding.adsBanner65);

        initListener();
    }

    private void initListener() {
        binding.btnNext.setOnClickListener(v -> {
            APIManager.showInter(this, false, b -> {
                startActivity(new Intent(this, WelComeActivity.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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