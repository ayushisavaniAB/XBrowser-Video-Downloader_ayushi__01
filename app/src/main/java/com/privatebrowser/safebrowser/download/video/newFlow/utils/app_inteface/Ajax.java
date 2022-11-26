package com.privatebrowser.safebrowser.download.video.newFlow.utils.app_inteface;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

/* loaded from: classes2.dex */
public class Ajax {
    public static String title = "";
    public interact con;
    public Context context;

    public void ajaxBegin() {
    }

    public void ajaxDone() {
    }

    public Ajax(Context context, interact interactVar) {
        this.con = null;
        this.context = context;
        this.con = interactVar;
    }

    @JavascriptInterface
    public void onAudioSourcesItercept(final String str, final String str2) {
        if (title.equals("")) {
            title = str2;
            Log.e("iiiiiiiiiiiiiiiii", "onAudioSourcesItercept.....onAudioSourcesItercept....str......" + str);
            new Handler().postDelayed(new Runnable() { // from class: com.mms.videodownloader.AppData.app_inteface.Ajax.1

                /* renamed from: com.mms.videodownloader.AppData.app_inteface.Ajax$1$C09121 */
                /* loaded from: classes2.dex */
                class C09121 implements Runnable {
                    C09121() {
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (Ajax.this.con != null) {
                            Ajax.this.con.onAudioSourcesItercept(str, str2);
                        }
                    }
                }

                @Override // java.lang.Runnable
                public void run() {
                    ((Activity) Ajax.this.context).runOnUiThread(new C09121());
                }
            }, 500L);
        }
    }

    @JavascriptInterface
    public void onAudioSrcItercept(final String str, final String str2) {
        if (title.equals("")) {
            title = str2;
            Log.e("iiiiiiiiiiiiiiiii", "onAudioSrcItercept.....onAudioSrcItercept....str......" + str);
            new Handler().postDelayed(new Runnable() { // from class: com.mms.videodownloader.AppData.app_inteface.Ajax.2

                /* renamed from: com.mms.videodownloader.AppData.app_inteface.Ajax$2$C09141 */
                /* loaded from: classes2.dex */
                class C09141 implements Runnable {
                    C09141() {
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (Ajax.this.con != null) {
                            Ajax.this.con.onAudioSrcItercept(str, str2);
                        }
                    }
                }

                @Override // java.lang.Runnable
                public void run() {
                    ((Activity) Ajax.this.context).runOnUiThread(new C09141());
                }
            }, 500L);
        }
    }

    @JavascriptInterface
    public void onVideoSourcesItercept(final String str, final String str2) {
        if (title.equals("")) {
            title = str2;
            Log.e("iiiiiiiiiiiiiiiii", "onVideoSourcesItercept.....onVideoSourcesItercept....str......" + str);
            new Handler().postDelayed(new Runnable() { // from class: com.mms.videodownloader.AppData.app_inteface.Ajax.3

                /* renamed from: com.mms.videodownloader.AppData.app_inteface.Ajax$3$C09161 */
                /* loaded from: classes2.dex */
                class C09161 implements Runnable {
                    C09161() {
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (Ajax.this.con != null) {
                            Ajax.this.con.onVideoSourcesItercept(str, str2);
                        }
                    }
                }

                @Override // java.lang.Runnable
                public void run() {
                    ((Activity) Ajax.this.context).runOnUiThread(new C09161());
                }
            }, 500L);
        }
    }

    @JavascriptInterface
    public void onVideoSrcItercept(final String str, final String str2) {
        if (title.equals("")) {
            title = str2;
            new Handler().postDelayed(new Runnable() { // from class: com.mms.videodownloader.AppData.app_inteface.Ajax.4

                /* renamed from: com.mms.videodownloader.AppData.app_inteface.Ajax$4$C09181 */
                /* loaded from: classes2.dex */
                class C09181 implements Runnable {
                    C09181() {
                    }

                    @Override // java.lang.Runnable
                    public void run() {
                        if (Ajax.this.con != null) {
                            Log.e("iiiiiiiiiiiiiiiii", "onVideoSrcItercept.....str....str......" + str);
                            Ajax.this.con.onVideoSrcItercept(str, str2);
                        }
                    }
                }

                @Override // java.lang.Runnable
                public void run() {
                    ((Activity) Ajax.this.context).runOnUiThread(new C09181());
                }
            }, 500L);
        }
    }
}
