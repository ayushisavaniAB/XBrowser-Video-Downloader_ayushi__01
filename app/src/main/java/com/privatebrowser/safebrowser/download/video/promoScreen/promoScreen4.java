package com.privatebrowser.safebrowser.download.video.promoScreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityPromoScreen4Binding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.mainUi.WelComeActivity;
import think.outside.the.box.handler.APIManager;

public class promoScreen4 extends BaseActivity {

    ActivityPromoScreen4Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);
        binding = ActivityPromoScreen4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        APIManager.showNative(binding.adsNative200);

        initListener();
    }

    private void initListener() {
        binding.btnNext.setOnClickListener(v -> {
            if (APIManager.getStartScreenCount() == 4) {
                APIManager.showInter(this, false, b -> {
                    Intent intent = new Intent(this,WelComeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                });
            }else
                APIManager.showInter(this, false, b -> {
                    Intent intent = new Intent(this,  promoScreen5.class);
                    startActivity(intent);
                    finish();
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