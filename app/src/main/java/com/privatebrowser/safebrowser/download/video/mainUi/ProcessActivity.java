package com.privatebrowser.safebrowser.download.video.mainUi;

import android.os.Bundle;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityProcessBinding;
import com.privatebrowser.safebrowser.download.video.fragment.ProgressFragment;
import think.outside.the.box.handler.APIManager;

public class ProcessActivity extends BaseActivity {

    ActivityProcessBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProcessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new ProgressFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(this, true, b -> {
            finish();
        });
    }
}