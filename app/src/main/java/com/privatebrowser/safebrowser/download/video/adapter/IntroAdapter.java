package com.privatebrowser.safebrowser.download.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.circularreveal.CircularRevealRelativeLayout;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import com.privatebrowser.safebrowser.download.video.mainUi.HelpActivity;
import com.privatebrowser.safebrowser.download.video.R;


public class IntroAdapter extends PagerAdapter {

    Callback callback;
    Context context;
    private String[] title;
    private ShapeableImageView icClose;
    private CircularRevealRelativeLayout topRelative, mainView, lastView;
    private MaterialTextView helpNumber;
    private MaterialTextView helpText;
    private ShapeableImageView image;
    int[] imageId;
    public interface Callback {
        void onClose();
    }

    public IntroAdapter(HelpActivity context, String[] title, int[] image, Callback callback) {

        this.context = context;
        this.title = title;
        this.imageId = image;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.intro_layout, container, false);
        icClose = layout.findViewById(R.id.ic_close);
        topRelative = layout.findViewById(R.id.topRelative);
        helpNumber = layout.findViewById(R.id.help_number);
        helpText = layout.findViewById(R.id.help_text);
        image = layout.findViewById(R.id.image);
        mainView = layout.findViewById(R.id.main_View);
        lastView = layout.findViewById(R.id.lastView);

        if (position < 3) {
            mainView.setVisibility(View.VISIBLE);
            lastView.setVisibility(View.GONE);
            helpText.setText(title[position]);
            int number = position + 1;
            helpNumber.setText(String.valueOf(number));
            image.setImageResource(imageId[position]);
        } else {
            lastView.setVisibility(View.VISIBLE);
            mainView.setVisibility(View.GONE);
        }

        icClose.setOnClickListener(v -> {
            callback.onClose();
        });

        container.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }


}
