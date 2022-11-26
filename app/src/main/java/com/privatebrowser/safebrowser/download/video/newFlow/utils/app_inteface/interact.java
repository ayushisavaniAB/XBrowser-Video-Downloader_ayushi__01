package com.privatebrowser.safebrowser.download.video.newFlow.utils.app_inteface;

import android.webkit.WebView;

import java.util.ArrayList;
import java.util.HashMap;

/* loaded from: classes2.dex */
public interface interact {
    void OnStart(WebView webView);

    void OnStartsite(WebView webView);

    void Onfinesh(WebView webView);

    void OnreciveVideourl(WebView webView, String str, String str2, String str3);

    void onAudioSourcesItercept(String str, String str2);

    void onAudioSrcItercept(String str, String str2);

    void onItemClick(String str, int i, ArrayList<HashMap<String, String>> arrayList);

    void onVideoSourcesItercept(String str, String str2);

    void onVideoSrcItercept(String str, String str2);

    void oncreate(WebView webView);

    void onplay(String str, String str2, String str3);
}
