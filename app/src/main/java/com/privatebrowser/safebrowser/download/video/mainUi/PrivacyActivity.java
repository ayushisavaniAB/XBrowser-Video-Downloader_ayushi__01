package com.privatebrowser.safebrowser.download.video.mainUi;

import android.os.Bundle;

import com.privatebrowser.safebrowser.download.video.databinding.ActivityPrivacyBinding;
import think.outside.the.box.handler.APIManager;

public class PrivacyActivity extends BaseActivity {

    ActivityPrivacyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backButton.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(this, true, b -> {
            finish();
        });
    }
}