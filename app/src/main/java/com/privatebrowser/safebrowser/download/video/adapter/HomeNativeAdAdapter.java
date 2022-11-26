package com.privatebrowser.safebrowser.download.video.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.Objects;

import com.privatebrowser.safebrowser.download.video.Ads.RecyclerViewAdapterWrapper;
import com.privatebrowser.safebrowser.download.video.R;
import think.outside.the.box.handler.APIManager;


public class HomeNativeAdAdapter extends RecyclerViewAdapterWrapper {

    private static final int TYPE_FB_NATIVE_ADS = 900;
    private static final int DEFAULT_AD_ITEM_INTERVAL = 2;

    private final Param mParam;
    private NativeAd nativeAd = null;

    private HomeNativeAdAdapter(Param param) {
        super(param.adapter);
        this.mParam = param;

        assertConfig();
        setSpanAds();
    }

    private void assertConfig() {
        if (mParam.gridLayoutManager != null) {
            //if user set span ads
            int nCol = mParam.gridLayoutManager.getSpanCount();
            if (mParam.adItemInterval % nCol != 0) {
                throw new IllegalArgumentException(String.format("The adItemInterval (%d) is not divisible by number of columns in GridLayoutManager (%d)", mParam.adItemInterval, nCol));
            }
        }
    }


    public void updateNative(NativeAd nativeAd) {
        this.nativeAd = nativeAd;
        notifyDataSetChanged();
    }

    private int convertAdPosition2OrgPosition(int position) {
        return position - (position + 1) / (mParam.adItemInterval + 1);
    }

    @Override
    public int getItemCount() {
        int realCount = super.getItemCount();
        return realCount + realCount / mParam.adItemInterval;
    }

    @Override
    public int getItemViewType(int position) {
        if (isAdPosition(position)) {
            return TYPE_FB_NATIVE_ADS;
        }
        return super.getItemViewType(convertAdPosition2OrgPosition(position));
    }

    private boolean isAdPosition(int position) {
        return (position + 1) % (mParam.adItemInterval + 1) == 0;
    }

    private void onBindAdViewHolder(final RecyclerView.ViewHolder holder) {
        final AdViewHolder adHolder = (AdViewHolder) holder;
        View view;
        if (nativeAd != null) {

            view = LayoutInflater.from(adHolder.getContext()).inflate(think.outside.the.box.R.layout.lib_admob_small, null);
            populateAppInstallAdViewMediaS(nativeAd, APIManager.getButtonColor(), APIManager.getButtonTextColor(), (NativeAdView) view.findViewById(think.outside.the.box.R.id.unified));

            adHolder.nativeAdContainer.removeAllViews();
            adHolder.nativeAdContainer.addView(view);
        }

    }

    public static void populateAppInstallAdViewMediaS(NativeAd unifiedNativeAd, String buttonColor, String textColor, NativeAdView unifiedNativeAdView) {
        unifiedNativeAdView.setHeadlineView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_headline));
        unifiedNativeAdView.setBodyView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_body));
        unifiedNativeAdView.setCallToActionView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_call_to_action));

        TextView viewById = unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_call_to_action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            viewById.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(buttonColor)));
        } else {
            viewById.setBackgroundColor(Color.parseColor(buttonColor));
        }
        viewById.setTextColor(Color.parseColor(textColor));

        unifiedNativeAdView.setIconView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_app_icon));
        unifiedNativeAdView.setPriceView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_price));
        unifiedNativeAdView.setStarRatingView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_stars));
        unifiedNativeAdView.setStoreView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_store));
        unifiedNativeAdView.setAdvertiserView(unifiedNativeAdView.findViewById(think.outside.the.box.R.id.ad_advertiser));
//
//        CardView cardView= (CardView) unifiedNativeAdView.getParent();
//        cardView.setCardBackgroundColor(Color.parseColor(InternalHandler.INSTANCE.getNativeBGColor()));
//        ((TextView) unifiedNativeAdView.getHeadlineView()).setTextColor(Color.parseColor(InternalHandler.INSTANCE.getNativeTextColor()));
//        ((TextView) unifiedNativeAdView.getBodyView()).setTextColor(Color.parseColor(InternalHandler.INSTANCE.getNativeTextColor()));
//        ((TextView) unifiedNativeAdView.getAdvertiserView()).setTextColor(Color.parseColor(InternalHandler.INSTANCE.getNativeTextColor()));
//        ((TextView) unifiedNativeAdView.getPriceView()).setTextColor(Color.parseColor(InternalHandler.INSTANCE.getNativeTextColor()));
//        ((TextView) unifiedNativeAdView.getStoreView()).setTextColor(Color.parseColor(InternalHandler.INSTANCE.getNativeTextColor()));


        ((TextView) unifiedNativeAdView.getHeadlineView()).setText(unifiedNativeAd.getHeadline());
        if (unifiedNativeAd.getBody() == null) {
            unifiedNativeAdView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getBodyView()).setText(unifiedNativeAd.getBody());
            ((TextView) Objects.requireNonNull(unifiedNativeAdView.getBodyView())).setMovementMethod(new ScrollingMovementMethod());
        }
        if (unifiedNativeAd.getCallToAction() == null) {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            unifiedNativeAdView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) unifiedNativeAdView.getCallToActionView()).setText(unifiedNativeAd.getCallToAction());
        }
        if (unifiedNativeAd.getIcon() == null) {
            unifiedNativeAdView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) unifiedNativeAdView.getIconView()).setImageDrawable(unifiedNativeAd.getIcon().getDrawable());
            unifiedNativeAdView.getIconView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getStarRating() == null) {
            unifiedNativeAdView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) unifiedNativeAdView.getStarRatingView()).setRating(unifiedNativeAd.getStarRating().floatValue());
            unifiedNativeAdView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (unifiedNativeAd.getAdvertiser() == null) {
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) unifiedNativeAdView.getAdvertiserView()).setText(unifiedNativeAd.getAdvertiser());
            unifiedNativeAdView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        try {
            unifiedNativeAdView.setNativeAd(unifiedNativeAd);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_FB_NATIVE_ADS) {
            onBindAdViewHolder(holder);
        } else {
            super.onBindViewHolder(holder, convertAdPosition2OrgPosition(position));
        }
    }

    private RecyclerView.ViewHolder onCreateAdViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View adLayoutOutline = inflater
                .inflate(mParam.itemContainerLayoutRes, parent, false);
        return new AdViewHolder(adLayoutOutline);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FB_NATIVE_ADS) {
            return onCreateAdViewHolder(parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    private void setSpanAds() {
        if (mParam.gridLayoutManager == null) {
            return;
        }
        final GridLayoutManager.SpanSizeLookup spl = mParam.gridLayoutManager.getSpanSizeLookup();
        mParam.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isAdPosition(position)) {
                    return spl.getSpanSize(position);
                }
                return 1;
            }
        });
    }

    private static class Param {
        RecyclerView.Adapter adapter;
        int adItemInterval;
        boolean forceReloadAdOnBind;
        Activity activity;

        @LayoutRes
        int itemContainerLayoutRes;

        @IdRes
        int itemContainerId;

        GridLayoutManager gridLayoutManager;
    }

    public static class Builder {
        private final Param mParam;

        private Builder(Param param) {
            mParam = param;
        }

        public static Builder with(RecyclerView.Adapter wrapped, Activity activity) {
            Param param = new Param();
            param.adapter = wrapped;
            param.activity = activity;
            param.adItemInterval = DEFAULT_AD_ITEM_INTERVAL;
            param.itemContainerLayoutRes = R.layout.bigads_nativelayout;
            param.itemContainerId = R.id.amNative;
            param.forceReloadAdOnBind = true;
            return new Builder(param);
        }

        public Builder adItemInterval(int interval) {
            mParam.adItemInterval = interval;
            return this;
        }


        public HomeNativeAdAdapter build() {
            return new HomeNativeAdAdapter(mParam);
        }

        public Builder enableSpanRow(GridLayoutManager layoutManager) {
            mParam.gridLayoutManager = layoutManager;
            return this;
        }

        public Builder forceReloadAdOnBind(boolean forced) {
            mParam.forceReloadAdOnBind = forced;
            return this;
        }
    }

    private static class AdViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout nativeAdContainer;
        boolean loaded;

        AdViewHolder(View view) {
            super(view);
            nativeAdContainer = view.findViewById(R.id.amNative);

            loaded = false;
        }

        Context getContext() {
            return nativeAdContainer.getContext();
        }
    }
}