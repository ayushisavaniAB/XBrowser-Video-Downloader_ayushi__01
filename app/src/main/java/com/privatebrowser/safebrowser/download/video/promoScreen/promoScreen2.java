package com.privatebrowser.safebrowser.download.video.promoScreen;

import android.content.Intent;
import android.os.Bundle;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.adapter.LanguageAdapter;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityPromoScreen2Binding;
import com.privatebrowser.safebrowser.download.video.mainUi.BaseActivity;
import com.privatebrowser.safebrowser.download.video.mainUi.BrowserMainActivity;
import com.privatebrowser.safebrowser.download.video.mainUi.WelComeActivity;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.util.TinyDB;

public class promoScreen2 extends BaseActivity {

    ActivityPromoScreen2Binding binding;
    LanguageAdapter languageAdapter;
    int LanguagePosition = 0;
    int[] CountryFlag = {
            R.drawable.us,
            R.drawable.cn,
            R.drawable.in,
            R.drawable.es,
            R.drawable.fr,
            R.drawable.eg,
            R.drawable.ru,
            R.drawable.br,
            R.drawable.id};
    String[] languageList = {"English", "Chinese", "Hindi", "Spanish", "French", "Arabic", "Russian", "Portuguese", "Indonesian"};
    String[] LanguageCode = {"en", "ch", "hi", "es", "fr", "ar", "ru", "pt", "id"};
    boolean setting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);
        binding = ActivityPromoScreen2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        APIManager.showNative(binding.adsNative200);
        APIManager.showBanner(binding.adsBanner65);

        initAdapter();

    }

    private void initAdapter() {

        languageAdapter = new LanguageAdapter(this, languageList, CountryFlag, position -> {
            LanguagePosition = position;
            languageAdapter.notifyDataSetChanged();
        });
        // binding.languageRecyclerview.setAdapter(languageAdapter);

        binding.btnNext.setOnClickListener(v -> {
            if (APIManager.getStartScreenCount() == 2) {
                APIManager.showInter(this, false, b -> {
                    startActivity(new Intent(this, WelComeActivity.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                });
            } else {
                APIManager.showInter(this, false, b -> {
                    startActivity(new Intent(this, promoScreen3.class));
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

                });
            }

        });
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(this, true, b -> {
            finish();
        });
    }
}