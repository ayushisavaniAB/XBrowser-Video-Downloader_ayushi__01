package com.privatebrowser.safebrowser.download.video.mainUi;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityBowserMainBinding;
import com.privatebrowser.safebrowser.download.video.fragment.HomeFragment;
import com.privatebrowser.safebrowser.download.video.fragment.SaveFragment;
import think.outside.the.box.handler.APIManager;

public class BrowserMainActivity extends BaseActivity {

    ActivityBowserMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBowserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        APIManager.showBanner(binding.adBanner);
        initListener();
    }

    private void initListener() {

        binding.previous.setOnClickListener(v -> {
            binding.previousImage.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.nextImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.homeImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.folderImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.settingImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.previousText.setTextColor(getResources().getColor(R.color.darkBlue));
            binding.homeText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.nextText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.folderText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.settingText.setTextColor(getResources().getColor(R.color.colorWhite));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new HomeFragment()).commit();
        });

        binding.next.setOnClickListener(v -> {
            binding.previousImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.nextImage.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.homeImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.folderImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.settingImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.previousText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.homeText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.nextText.setTextColor(getResources().getColor(R.color.darkBlue));
            binding.folderText.setTextColor(getResources().getColor(R.color.colorWhite));
            binding.settingText.setTextColor(getResources().getColor(R.color.colorWhite));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new SaveFragment()).commit();
        });

        binding.home.setOnClickListener(v -> {
            setHomeColor();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new HomeFragment()).commit();
        });

        binding.folder.setOnClickListener(v -> {
            setHomeColor();
            APIManager.showInter(this, false, b -> {
                Intent intent = new Intent(this, FolderActivity.class);
                intent.putExtra("type", "folder");
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            });
        });

        binding.settings.setOnClickListener(v -> {
            setHomeColor();
            APIManager.showInter(this, false, b -> {
                Intent intent = new Intent(this, FolderActivity.class);
                intent.putExtra("type", "setting");
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            });
        });

        binding.process.setOnClickListener(v -> {
            APIManager.showInter(this, false, b -> {
                startActivity(new Intent(this, ProcessActivity.class));
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            });
        });

        binding.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIManager.showInter(BrowserMainActivity.this, false, isfail -> {
                  onBackPressed();
                });
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_view, new HomeFragment()).commit();
    }

    private void setHomeColor() {

        binding.previousImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.nextImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.homeImage.setColorFilter(ContextCompat.getColor(this, R.color.darkBlue), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.folderImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.settingImage.setColorFilter(ContextCompat.getColor(this, R.color.colorWhite), android.graphics.PorterDuff.Mode.SRC_IN);
        binding.previousText.setTextColor(getResources().getColor(R.color.colorWhite));
        binding.homeText.setTextColor(getResources().getColor(R.color.darkBlue));
        binding.nextText.setTextColor(getResources().getColor(R.color.colorWhite));
        binding.folderText.setTextColor(getResources().getColor(R.color.colorWhite));
        binding.settingText.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 45);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission()) {
        } else {
            requestPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 45) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                requestPermission();
                Toast.makeText(this, getString(R.string.storage_permission_required_toast), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(BrowserMainActivity.this, true, isfail -> {
            finish();
        });
    }
}