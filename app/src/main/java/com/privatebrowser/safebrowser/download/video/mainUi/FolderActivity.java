package com.privatebrowser.safebrowser.download.video.mainUi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityFolderBinding;
import com.privatebrowser.safebrowser.download.video.fragment.SaveFragment;
import com.privatebrowser.safebrowser.download.video.fragment.SettingFragment;
import think.outside.the.box.handler.APIManager;

public class FolderActivity extends BaseActivity {

    ActivityFolderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentData();
        initFragment();
        initListener();
    }

    private void getIntentData() {
        String type = getIntent().getStringExtra("type");
        if (type.equals("folder")) {
            binding.title.setText(R.string.folder);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new SaveFragment()).commit();
        } else if (type.equals("setting")) {
            binding.title.setText(R.string.setting);
            setSettingColor();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new SettingFragment()).commit();
        } else {
            binding.title.setText(R.string.folder);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new SaveFragment()).commit();
        }
    }

    private void setSettingColor() {
        binding.homeImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.folderImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.settingImage.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.homeText.setTextColor(getResources().getColor(R.color.colorWhite));
        binding.folderText.setTextColor(getResources().getColor(R.color.colorWhite));
        binding.settingText.setTextColor(getResources().getColor(R.color.darkBlue));
    }

    private void initListener() {
        binding.icBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void initFragment() {

        binding.home.setOnClickListener(v -> {
            binding.homeImage.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.folderImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.settingImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.homeText.setTextColor(getResources().getColor(R.color.darkBlue));
            binding.folderText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.settingText.setTextColor(getResources().getColor(R.color.colorWhite));
            APIManager.showInter(this, false, b -> {
                finish();
            });
        });

        binding.folder.setOnClickListener(v -> {
            binding.title.setText(R.string.folder);
            binding.homeImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.folderImage.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.settingImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.homeText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.folderText.setTextColor(getResources().getColor(R.color.darkBlue));
            binding.settingText.setTextColor(getResources().getColor(R.color.colorWhite));
            APIManager.showInter(this, false, b -> {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new SaveFragment()).commit();
                    }
                }, 500);

            });
        });

        binding.settings.setOnClickListener(v -> {
            binding.title.setText(R.string.setting);
            setSettingColor();
            APIManager.showInter(this, false, b -> {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new SettingFragment()).commit();
                    }
                }, 500);
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