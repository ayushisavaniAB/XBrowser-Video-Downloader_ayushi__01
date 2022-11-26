package com.privatebrowser.safebrowser.download.video.download.browser;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Patterns;
import android.webkit.ValueCallback;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.privatebrowser.safebrowser.download.video.databinding.ActivityBrowsingBinding;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;
import think.outside.the.box.ui.BaseActivity;

public class BrowsingActivity extends BaseActivity implements BrowserWebChromeClient.FileChooseListener {

    ActivityBrowsingBinding browsingBinding;
    public static BrowserManager browserManager;
    private Uri appLinkData;
    private ValueCallback<Uri[]> fileChooseValueCallbackMultiUri;
    private ValueCallback<Uri> fileChooseValueCallbackSingleUri;
    public OnBackPressedListener onBackPressedListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((browserManager = (BrowserManager) getSupportFragmentManager().findFragmentByTag("BM")) == null) {
            getSupportFragmentManager().beginTransaction().add(browserManager = new BrowserManager(), "BM").commit();
        }

        browsingBinding = ActivityBrowsingBinding.inflate(getLayoutInflater());
        setContentView(browsingBinding.getRoot());

        Intent appLinkIntent = getIntent();
        //String appLinkAction = appLinkIntent.getAction();
        appLinkData = appLinkIntent.getData();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (appLinkData != null) {
                    connect(appLinkData.toString());
                }
            }
        }, 100);
    }

    public static BrowserManager getBrowserManager() {
        return browserManager;
    }

    public void connect(String data) {
        if (Patterns.WEB_URL.matcher(data).matches()) {
            if (!data.startsWith("http")) {
                data = "http://" + data;
            }
            getBrowserManager().newWindow(data);
        } else {
            data = "https://google.com/search?q=" + data;
            getBrowserManager().newWindow(data);
        }
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    public OnBackPressedListener getOnBackPressedListener() {
        return onBackPressedListener;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestPermissionsResultCallback.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
    }

    private ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback;

    public void setOnRequestPermissionsResultListener(ActivityCompat
                                                              .OnRequestPermissionsResultCallback
                                                              onRequestPermissionsResultCallback) {
        this.onRequestPermissionsResultCallback = onRequestPermissionsResultCallback;
    }

    @Override
    public ValueCallback<Uri[]> getValueCallbackMultiUri() {
        return fileChooseValueCallbackMultiUri;
    }

    @Override
    public ValueCallback<Uri> getValueCallbackSingleUri() {
        return fileChooseValueCallbackSingleUri;
    }

    @Override
    public void setValueCallbackMultiUri(ValueCallback<Uri[]> valueCallback) {
        fileChooseValueCallbackMultiUri = valueCallback;
    }

    @Override
    public void setValueCallbackSingleUri(ValueCallback<Uri> valueCallback) {
        fileChooseValueCallbackSingleUri = valueCallback;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 123) {
            finish();
        }
        if (resultCode == Activity.RESULT_OK) {
//            url = String.valueOf(data.getData());
            Log.e("url", "onActivityResult: " + String.valueOf(data.getData()));
        }
        if (requestCode == 3 && resultCode == -1) {
            setResult(-1);
            finish();
        }

        if (requestCode == BrowserWebChromeClient.FILE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && fileChooseValueCallbackSingleUri != null) {
                    fileChooseValueCallbackSingleUri.onReceiveValue(data.getData());
                    fileChooseValueCallbackSingleUri = null;
                } else if (data != null && fileChooseValueCallbackMultiUri != null) {
                    Uri[] dataUris;
                    try {
                        dataUris = new Uri[]{Uri.parse(data.getDataString())};
                    } catch (Exception e) {
                        dataUris = null;
                    }
                    fileChooseValueCallbackMultiUri.onReceiveValue(dataUris);
                    fileChooseValueCallbackMultiUri = null;
                }
            } else if (fileChooseValueCallbackSingleUri != null) {
                fileChooseValueCallbackSingleUri.onReceiveValue(null);
                fileChooseValueCallbackSingleUri = null;
            } else if (fileChooseValueCallbackMultiUri != null) {
                fileChooseValueCallbackMultiUri.onReceiveValue(null);
                fileChooseValueCallbackMultiUri = null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getOnBackPressedListener() != null) {
            getFragmentManager().popBackStack();
            getOnBackPressedListener().onBackPressed();

        } else {
            browserManager = null;
            APIManager.showInter(BrowsingActivity.this, true, (AdsCallback) b -> {
                finish();
            });
        }
    }

}