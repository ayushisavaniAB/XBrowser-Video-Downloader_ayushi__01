package com.privatebrowser.safebrowser.download.video.mainUi;

import android.os.Bundle;
import android.view.View;

import androidx.viewpager.widget.ViewPager;


import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.adapter.IntroAdapter;
import com.privatebrowser.safebrowser.download.video.databinding.ActivityHelpBinding;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.ui.BaseActivity;

public class HelpActivity extends BaseActivity implements View.OnClickListener {

    ActivityHelpBinding binding;
    String title[] = {"Go to Website", "Play the video", "Click the download button"};
    int image[] = {R.drawable.help_0,R.drawable.help_1,R.drawable.help_2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setLightTheme(false);
        super.onCreate(savedInstanceState);

        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        APIManager.showBanner(binding.adBanner);

        IntroAdapter introAdapter = new IntroAdapter(this, title,image, () -> {
            onBackPressed();
        });
        binding.idViewPager.setAdapter(introAdapter);
        binding.indicatorView.setViewPager(binding.idViewPager);
        binding.idViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 3) {
                    binding.idLinear1.setVisibility(View.GONE);
                    binding.idLinear2.setVisibility(View.VISIBLE);
                } else {
                    binding.idLinear2.setVisibility(View.GONE);
                    binding.idLinear1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        binding.idSkip.setOnClickListener(this);
        binding.idNext.setOnClickListener(this);
        binding.idContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_skip:
                binding.idViewPager.setCurrentItem(3);
                break;

            case R.id.id_next:
                if (binding.idViewPager.getCurrentItem() == 0) {
                    binding.idViewPager.setCurrentItem(1);
                } else if (binding.idViewPager.getCurrentItem() == 1) {
                    binding.idViewPager.setCurrentItem(2);
                } else if (binding.idViewPager.getCurrentItem() == 2) {
                    binding.idViewPager.setCurrentItem(3);
                } else if (binding.idViewPager.getCurrentItem() == 2) {
                    binding.idViewPager.setCurrentItem(0);
                }
                break;

            case R.id.id_continue:
                APIManager.showInter(this, false, b -> {
                    finish();
                });
                break;
        }
    }

    @Override
    public void onBackPressed() {
        APIManager.showInter(this, true, b -> {
            finish();
        });
    }
}