/*
 *     LM videodownloader is a browser app for android, made to easily
 *     download videos.
 *     Copyright (C) 2018 Loremar Marabillas
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.privatebrowser.safebrowser.download.video.download.browser;

import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.DialogAddBookmarkBinding;
import com.privatebrowser.safebrowser.download.video.databinding.DialogDeleteBinding;
import com.privatebrowser.safebrowser.download.video.download.DownloadManager;
import com.privatebrowser.safebrowser.download.video.download.DownloadQueues;
import com.privatebrowser.safebrowser.download.video.download.DownloadVideo;
import com.privatebrowser.safebrowser.download.video.download.ShowDownloadActivity;
import com.privatebrowser.safebrowser.download.video.download.TouchableWebView;
import com.privatebrowser.safebrowser.download.video.download.bookmark.BookmarksSQLite;
import com.privatebrowser.safebrowser.download.video.download.bookmark.ShowBookMarkActivity;
import com.privatebrowser.safebrowser.download.video.download.history.ShowHistoryActivity;
import com.privatebrowser.safebrowser.download.video.download.video.VideoContentSearch;
import com.privatebrowser.safebrowser.download.video.download.video.VideoDetectionInitiator;
import com.privatebrowser.safebrowser.download.video.download.video.VideoList;
import com.privatebrowser.safebrowser.download.video.mainUi.PlayApp;
import com.privatebrowser.safebrowser.download.video.newFlow.utils.AppConfig;
import com.privatebrowser.safebrowser.download.video.newFlow.utils.FileUtils;
import com.privatebrowser.safebrowser.download.video.newFlow.utils.app_inteface.Ajax;
import com.privatebrowser.safebrowser.download.video.newFlow.utils.app_inteface.interact;
import com.privatebrowser.safebrowser.download.video.utils.Glob;
import com.privatebrowser.safebrowser.download.video.utils.PreferenceUtil;
import com.privatebrowser.safebrowser.download.video.utils.Utility;
import think.outside.the.box.callback.AdsCallback;
import think.outside.the.box.handler.APIManager;


public class BrowserWindow extends Fragment implements interact, View.OnTouchListener, View.OnClickListener, BrowsingActivity.OnBackPressedListener, View.OnLongClickListener {
    public interact con;
    public static String url;
    public static boolean newUrlLoad = false;
    private View view;
    private TouchableWebView page;
    private SSLSocketFactory defaultSSLSF;
    private View videosFoundHUD;
    private float prevX, prevY;
    private ProgressBar findingVideoInProgress;
    private TextView videosFoundText;
    private boolean moved = false;
    private GestureDetector gesture;
    private VideoDetectionInitiator videoDetectionInitiator;
    private boolean isDetecting;

    private View foundVideosWindow;
    private VideoList videoList;
    private ImageView foundVideosQueue;
    private ImageView foundVideosDelete;
    private ImageView foundVideosClose;

    private ProgressBar loadingPageProgress;

    private int orientation;
    private boolean loadedFirsTime;
    public static BrowserManager browserManager;

    public List<DownloadVideo> downloads;
    public DownloadQueues queues;
    PreferenceUtil prefernces;
    TextView numWindows;
    FrameLayout numWindowsBtn;
    public ImageView prevBtn, nextBtn, bookmarkBtn, reloadBtn, newWindowBtn;
    public ImageView backbtn, closebtn;
    private BookmarksSQLite bookmarksSQLite;
    Intent intent;


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.e("page", "onCreate: " + page.canGoBack() + "  " + page.canGoForward());
        if (v == videosFoundHUD) {
            gesture.onTouchEvent(event);

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (!moved) v.performClick();
                    moved = false;
                    break;
                case MotionEvent.ACTION_DOWN:
                    prevX = event.getRawX();
                    prevY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    moved = true;
                    float moveX = event.getRawX() - prevX;
                    videosFoundHUD.setX(videosFoundHUD.getX() + moveX);
                    prevX = event.getRawX();
                    float moveY = event.getRawY() - prevY;
                    videosFoundHUD.setY(videosFoundHUD.getY() + moveY);
                    prevY = event.getRawY();
                    float width = getResources().getDisplayMetrics().widthPixels;
                    float height = getResources().getDisplayMetrics().heightPixels;
                    if ((videosFoundHUD.getX() + videosFoundHUD.getWidth()) >= width || videosFoundHUD.getX() <= 0) {
                        videosFoundHUD.setX(videosFoundHUD.getX() - moveX);
                    }
                    if ((videosFoundHUD.getY() + videosFoundHUD.getHeight()) >= height || videosFoundHUD.getY() <= 0) {
                        videosFoundHUD.setY(videosFoundHUD.getY() - moveY);
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == videosFoundHUD) {
            if (isDetecting) {
                foundVideosWindow.setVisibility(View.VISIBLE);
            } else {
                isDetecting = true;
                videosFoundText.setVisibility(View.VISIBLE);
                videoDetectionInitiator.initiate();
            }
            if (videoList.getSize() == 0) {
                foundVideosWindow.setVisibility(View.GONE);
            }
        } else if (v == foundVideosQueue) {
            foundVideosWindow.setVisibility(View.GONE);
            videoList.saveCheckedItemsForDownloading();
            videoList.deleteCheckedItems();
            updateFoundVideosBar();
            if (Utility.isServiceRunning(DownloadManager.class, getActivity().getApplicationContext())) {
                pauseDownload();
            } else {
                startDownload();
            }

        } else if (v == foundVideosDelete) {
            videoList.deleteCheckedItems();
            updateFoundVideosBar();
        } else if (v == foundVideosClose) {
            foundVideosWindow.setVisibility(View.GONE);
        }
    }

    public void startDownload() {
        downloads = new ArrayList<>();
        queues = DownloadQueues.load(getActivity());
        downloads = queues.getList();
        Intent downloadService = PlayApp.getInstance().getDownloadService();
        if (downloads.size() > 0) {
            DownloadVideo topVideo = downloads.get(0);
            Log.e("Link", "startDownload: " + topVideo.link);
            downloadService.putExtra("link", topVideo.link);
            downloadService.putExtra("name", topVideo.name);
            downloadService.putExtra("type", topVideo.type);
            downloadService.putExtra("size", topVideo.size);
            downloadService.putExtra("page", topVideo.page);
            downloadService.putExtra("chunked", topVideo.chunked);
            downloadService.putExtra("website", topVideo.website);
            PlayApp.getInstance().startService(downloadService);
            Toast.makeText(getActivity(), "Downloading Starting", Toast.LENGTH_SHORT).show();
        }
    }

    public void pauseDownload() {
        DownloadManager.stop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        url = data.getString("url");
        prefernces = new PreferenceUtil(getActivity());
        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory();
        videoDetectionInitiator = new VideoDetectionInitiator(new ConcreteVideoContentSearch());
        isDetecting = prefernces.getBoolean(getString(R.string.autoVideoDetect), true);

        setRetainInstance(true);

    }

    private void createNavigationBar() {
        prevBtn = view.findViewById(R.id.btn_previous);
        nextBtn = view.findViewById(R.id.btn_next);
        bookmarkBtn = view.findViewById(R.id.btn_bookmark);
        reloadBtn = view.findViewById(R.id.btn_refresh);
        numWindowsBtn = view.findViewById(R.id.layout_num_Windows);
        numWindows = view.findViewById(R.id.txt_num_Windows);
        newWindowBtn = view.findViewById(R.id.btn_more);

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page.canGoBack()) page.goBack();
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page.canGoForward()) page.goForward();
            }
        });
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookmarksSQLite.contain(page.getUrl())) {
                    final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(getActivity()), null, false);
                    dialog.setContentView(deleteBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    deleteBinding.titleText.setText(R.string.delete_bookmark);
                    deleteBinding.msgText.setText(R.string.this_bookmark_delete);

                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bookmarksSQLite.deleteBookmark(page.getUrl());
                            bookmarkBtn.setImageResource(R.drawable.ic_bookmark_disable);
                            Toast.makeText(getActivity(), "Delete from Bookmark.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                } else {

                    final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
                    DialogAddBookmarkBinding addBookmarkBinding = DialogAddBookmarkBinding.inflate(dialog.getLayoutInflater());
                    dialog.setContentView(addBookmarkBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    addBookmarkBinding.addBookmarkTitle.setText(page.getTitle());
                    addBookmarkBinding.addBookmarkURL.setText(page.getUrl());

                    addBookmarkBinding.okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (addBookmarkBinding.addBookmarkTitle.getText().toString().equals("")) {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.empty_edit_toast), Toast.LENGTH_SHORT).show();
                            } else {
                                String title = addBookmarkBinding.addBookmarkTitle.getText().toString().trim();
                                byte[] bytes;
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                if (page.getFavicon() != null && page.getFavicon().compress(Bitmap.CompressFormat.PNG, 100, out)) {
                                    bytes = out.toByteArray();
                                } else {
                                    bytes = null;
                                }
                                bookmarksSQLite.add(bytes, title, page.getUrl());
                                dialog.dismiss();
                                Toast.makeText(getActivity(), "Page saved into bookmarks", Toast.LENGTH_SHORT).show();
                                bookmarkBtn.setImageResource(R.drawable.ic_bookmark_enable);
                            }
                        }
                    });
                    addBookmarkBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        reloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page.reload();
            }
        });

        numWindowsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OpenWindowActivity.class);
                APIManager.showInter(getActivity(), false, (AdsCallback) b -> {
                    startActivityForResult(intent, 123);
                });
            }
        });

        newWindowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(getActivity(), v);
                menu.inflate(R.menu.popup_browser);
                if (SDK_INT >= Build.VERSION_CODES.Q) {
                    menu.setForceShowIcon(true);
                }
                menu.show();
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_new_tab:
                                browserManager.newWindow("https://google.com/");
                                break;

                            case R.id.menu_bookmark:
                                intent = new Intent(getActivity(), ShowBookMarkActivity.class);


                                APIManager.showInter(getActivity(), false, (AdsCallback) b -> {
                                    startActivity(intent);

                                    //     someActivityResultLauncher.launch(intent);

                                });
                                break;

                            case R.id.menu_history:
                                intent = new Intent(getActivity(), ShowHistoryActivity.class);
                                APIManager.showInter(getActivity(), false, (AdsCallback) b -> {
                                    startActivity(intent);
                                });
                                break;

                            case R.id.menu_download:
                                intent = new Intent(getActivity(), ShowDownloadActivity.class);
                                APIManager.showInter(getActivity(), false, (AdsCallback) b -> {
                                    startActivity(intent);
                                });
                                break;
                        }
                        return true;
                    }
                });
            }
        });


    }

    private void createVideosFoundHUD() {
        videosFoundHUD = view.findViewById(R.id.videosFoundHUD);
        videosFoundHUD.setOnTouchListener(this);
        videosFoundHUD.setOnClickListener(this);
        gesture = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                videosFoundHUD.performClick();
                return true;
            }
        });

        findingVideoInProgress = videosFoundHUD.findViewById(R.id.findingVideosInProgress);
        findingVideoInProgress.setVisibility(View.GONE);

        videosFoundText = videosFoundHUD.findViewById(R.id.videosFoundText);

        setupVideosFoundHUDText();
    }

    private void setupVideosFoundHUDText() {
        isDetecting = prefernces.getBoolean(getString(R.string.autoVideoDetect), true);
        if (isDetecting) {
            videosFoundText.setVisibility(View.VISIBLE);
        } else {
            videosFoundText.setVisibility(View.GONE);
        }
    }

    private void createFoundVideosWindow() {
        View oldFoundVideosWindow = foundVideosWindow;
        foundVideosWindow = view.findViewById(R.id.foundVideosWindow);
        if (videoList != null) {
            videoList.recreateVideoList((RecyclerView) foundVideosWindow.findViewById(R.id.videoList));
        } else {
            videoList = new VideoList(getActivity(), (RecyclerView) foundVideosWindow.findViewById(R.id.videoList)) {
                @Override
                public void onItemDeleted() {
                    updateFoundVideosBar();
                }
            };
        }

        if (oldFoundVideosWindow != null) {
            switch (oldFoundVideosWindow.getVisibility()) {
                case View.VISIBLE:
                    foundVideosWindow.setVisibility(View.VISIBLE);
                    break;
                case View.GONE:
                    foundVideosWindow.setVisibility(View.GONE);
                    break;
                case View.INVISIBLE:
                    foundVideosWindow.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            foundVideosWindow.setVisibility(View.GONE);
        }

        foundVideosQueue = foundVideosWindow.findViewById(R.id.btn_download);
        foundVideosDelete = foundVideosWindow.findViewById(R.id.btn_delete);
        foundVideosClose = foundVideosWindow.findViewById(R.id.btn_close_list);
        foundVideosQueue.setOnClickListener(this);
        foundVideosDelete.setOnClickListener(this);
        foundVideosClose.setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null || getResources().getConfiguration().orientation != orientation) {
            int visibility = View.VISIBLE;
            if (view != null) {
                visibility = view.getVisibility();
            }
            view = inflater.inflate(R.layout.fragment_browser_window, container, false);
            regexVideo();
            view.setVisibility(visibility);

            browserManager = ((BrowsingActivity) getActivity()).getBrowserManager();

            bookmarksSQLite = new BookmarksSQLite(getActivity());

            if (page == null) {
                page = view.findViewById(R.id.page);
            } else {
                View page1 = view.findViewById(R.id.page);
                ((ViewGroup) view).removeView(page1);
                ((ViewGroup) page.getParent()).removeView(page);
                ((ViewGroup) view).addView(page);
                ((ViewGroup) view).bringChildToFront(view.findViewById(R.id.videosFoundHUD));
                ((ViewGroup) view).bringChildToFront(view.findViewById(R.id.foundVideosWindow));
            }
            loadingPageProgress = view.findViewById(R.id.loadingPageProgress);
            loadingPageProgress.setVisibility(View.GONE);

            backbtn = view.findViewById(R.id.btn_back);
            closebtn = view.findViewById(R.id.btn_close);

            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(getActivity()), null, false);
                    dialog.setContentView(deleteBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    deleteBinding.titleText.setText(R.string.alert);
                    deleteBinding.msgText.setText(R.string.close_all_window_2);

                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((BrowsingActivity) getActivity()).finish();
                            ((BrowsingActivity) getActivity()).setOnBackPressedListener(null);
                            dialog.dismiss();
                            APIManager.showInter(getActivity(), false, isfail -> {

                            });

                        }
                    });

                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                }
            });

            closebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
                    DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(getActivity()), null, false);
                    dialog.setContentView(deleteBinding.getRoot());
                    dialog.setCancelable(false);
                    dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    dialog.show();

                    deleteBinding.titleText.setText(R.string.alert);
                    deleteBinding.msgText.setText(R.string.close_this_window_2);

                    deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getFragmentManager().beginTransaction().remove(BrowserWindow.this).commit();
                            browserManager.closeWindow(BrowserWindow.this);
                            dialog.dismiss();
                            APIManager.showInter(getActivity(), false, isfail -> {

                            });

                        }
                    });

                    deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                dialog.dismiss();


                        }
                    });
                }
            });

            createNavigationBar();

            createVideosFoundHUD();

            createFoundVideosWindow();

            updateFoundVideosBar();

            if (getResources().getConfiguration().orientation != orientation) {
                browserManager.updateNumWindows();
                orientation = getResources().getConfiguration().orientation;
            }
        }

        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!loadedFirsTime) {
            HandlerThread thread = new HandlerThread("Video Extraction Thread");
            thread.start();
            final Handler extractVideoHandler = new Handler(thread.getLooper());

            WebSettings webSettings = page.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
            this.con = this;
            Ajax ajax = new Ajax(getActivity(), this.con);
            page.addJavascriptInterface(ajax, "MyBridg");
            page.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
            interact interactVar = this.con;
            if (interactVar != null) {
                interactVar.oncreate(this.page);
            }

            page.setWebViewClient(new WebViewClient() {//it seems not setting webclient, launches
                //default browser instead of opening the page in webview

                private VideoExtractionRunnable videoExtract = new VideoExtractionRunnable();
                private ConcreteVideoContentSearch videoSearch = new ConcreteVideoContentSearch();
                private String currentPage = page.getUrl();

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!url.startsWith("intent")) {
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                    return true;
                }

                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if (!request.getUrl().toString().startsWith("intent")) {
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                    return true;
                }

                @Override
                public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            TextView urlBox = BrowserWindow.this.view.findViewById(R.id.urlBox);
                            urlBox.setText(url);
                            urlBox.setSingleLine(true);
                            urlBox.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            urlBox.setSelected(true);
                            BrowserWindow.this.url = url;
                        }
                    });
                    loadingPageProgress.setVisibility(View.VISIBLE);
                    setupVideosFoundHUDText();
                    super.onPageStarted(view, url, favicon);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    loadingPageProgress.setVisibility(View.GONE);
                }

                @Override
                public void onLoadResource(final WebView view, String url) {

                    if (url.contains("instagram")) {
                        if (!FileUtils.isVideo(url, AppConfig.REGEXVIDEOS) || url.contains(".m3u8") || Glob.url11.contains("hentaihaven.org")) {
                            if (isTagCheck() && !url.contains("pornhub.com") && !Glob.url11.contains("hentaihaven.org")) {
                                Log.e("MYTAGS1", "onLoadResource: " + url);
                                view.loadUrl("javascript:window.local_obj.showSource(document.querySelector('video').querySelector('source[type=\"video/mp4\"]').src)");
                                view.loadUrl("javascript:window.local_obj.showSource(document.querySelector('video').src)");
                                url = Glob.url11;
                                Glob.url11 = "";
                                updateFoundVideosBar();
                            }
                        }
                        view.loadUrl(script());
                        Ajax.title = "";
                    }

                    if (page.canGoBack()) {
                        prevBtn.setImageResource(R.drawable.ic_left_arrow_enable);
                    } else {
                        prevBtn.setImageResource(R.drawable.ic_left_arrow_enable);
                    }
                    if (page.canGoForward()) {
                        nextBtn.setImageResource(R.drawable.ic_right_arrow_enable);
                    } else {
                        nextBtn.setImageResource(R.drawable.ic_right_arrow_disable);
                    }
                    if (bookmarksSQLite.contain(page.getUrl())) {
                        bookmarkBtn.setImageResource(R.drawable.ic_bookmark_enable);
                    } else {
                        bookmarkBtn.setImageResource(R.drawable.ic_bookmark_disable);
                    }

                    final String page = view.getUrl();
                    final String title = view.getTitle();

                    if (!page.equals(currentPage)) {
                        currentPage = page;
                        videoDetectionInitiator.clear();
                    }

                    videoExtract.setUrl(url);
                    videoExtract.setTitle(title);
                    videoExtract.setPage(page);
                    extractVideoHandler.post(videoExtract);
                }

                class VideoExtractionRunnable implements Runnable {
                    private String url = "https://";
                    private String title = "";
                    private String page = "";

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public void setTitle(String title) {
                        this.title = title;
                    }

                    public void setPage(String page) {
                        this.page = page;
                    }

                    @Override
                    public void run() {
                        try {
                            String urlLowerCase = url.toLowerCase();
                            String[] filters = getResources().getStringArray(R.array.videourl_filters);
                            boolean urlMightBeVideo = false;
                            for (String filter : filters) {
                                if (urlLowerCase.contains(filter)) {
                                    urlMightBeVideo = true;
                                    break;
                                }
                            }

                            if (urlMightBeVideo) {
                                videoSearch.newSearch(url, page, title);

                                if (isDetecting) {
                                    videoSearch.run();
                                } else {
                                    videoDetectionInitiator.reserve(url, page, title);
                                }
                            }
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    if (getActivity() != null) {
                        if (prefernces.getBoolean(getString(R.string.adBlockON), true) && (url.contains("ad") || url.contains("banner") || url.contains("pop")) && browserManager.isUrlAd(url)) {
                            return new WebResourceResponse("text/javascript", "UTF-8", null);
                        }
                    }
                    return super.shouldInterceptRequest(view, url);
                }

                @Override
                public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && ((BrowsingActivity) getActivity()) != null) {
                        if (prefernces.getBoolean(getString(R.string.adBlockON), true) && (request.getUrl().toString().contains("ad") || request.getUrl().toString().contains("banner") || request.getUrl().toString().contains("pop")) && browserManager.isUrlAd(request.getUrl().toString())) {
                            return new WebResourceResponse(null, null, null);
                        } else return null;
                    } else {
                        return shouldInterceptRequest(view, request.getUrl().toString());
                    }
                }
            });
            page.setWebChromeClient(new BrowserWebChromeClient(getActivity(), loadingPageProgress, ((BrowsingActivity) getActivity())));
            page.setOnLongClickListener(this);
            page.loadUrl(url);
            loadedFirsTime = true;

        } else {
            TextView urlBox = view.findViewById(R.id.urlBox);
            urlBox.setText(url);
            urlBox.setSingleLine(true);
            urlBox.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            urlBox.setSelected(true);
        }
    }

    public boolean isTagCheck() {
        return !Glob.url11.contains("instagram.com");
    }

    @Override
    public void onDestroy() {
        videoList.closeVideoDetailsFetcher();
        page.stopLoading();
        page.destroy();
        super.onDestroy();
    }

    private void updateFoundVideosBar() {
        final String videosFoundString = "Videos : " + videoList.getSize() + " found";
        final SpannableStringBuilder sb = new SpannableStringBuilder(videosFoundString);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(PlayApp.getInstance().getApplicationContext().getResources().getColor(R.color.white));
        final StyleSpan bss = new StyleSpan(Typeface.BOLD);
        sb.setSpan(fcs, 8, 8 + String.valueOf(videoList.getSize()).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 8, 8 + String.valueOf(videoList.getSize()).length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                videosFoundText.setText(sb);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (foundVideosWindow.getVisibility() == View.VISIBLE) {
            foundVideosWindow.setVisibility(View.GONE);
        } else if (page.canGoBack()) {
            page.goBack();
        } else {
            final Dialog dialog = new Dialog(getActivity(), R.style.WideDialog);
            DialogDeleteBinding deleteBinding = DialogDeleteBinding.inflate(LayoutInflater.from(getActivity()), null, false);
            dialog.setContentView(deleteBinding.getRoot());
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.show();

            deleteBinding.titleText.setText(R.string.alert);
            deleteBinding.msgText.setText(R.string.close_this_window_2);

            deleteBinding.yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browserManager.closeWindow(BrowserWindow.this);
                    dialog.dismiss();
                    getActivity().onBackPressed();
                }
            });

            deleteBinding.noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final WebView.HitTestResult hit = page.getHitTestResult();
        if (hit.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            if (hit.getExtra() != null) {
                View point = new View(getActivity());
                point.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (view != null) {
                    ((ViewGroup) view).addView(point);
                }
                point.getLayoutParams().height = 10;
                point.getLayoutParams().width = 10;
                point.setX(page.getClickX());
                point.setY(page.getClickY());
                PopupMenu menu = new PopupMenu(getActivity(), point);
                menu.getMenu().add("Open in new window");
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        browserManager.newWindow(hit.getExtra());
                        return true;
                    }
                });
                menu.show();
            }
        }
        return true;
    }

    public void updateNumWindows(int num) {
        final String numWindowsString = num + "";
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                numWindows.setText(numWindowsString);
            }
        });
    }

    public WebView getWebView() {
        return page;
    }


    public class ConcreteVideoContentSearch extends VideoContentSearch {

        @Override
        public void onStartInspectingURL() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (findingVideoInProgress.getVisibility() == View.GONE) {
                        findingVideoInProgress.setVisibility(View.VISIBLE);
                    }
                }
            });

            Utility.disableSSLCertificateChecking();
        }

        @Override
        public void onFinishedInspectingURL(boolean finishedAll) {
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSF);
            if (finishedAll) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        findingVideoInProgress.setVisibility(View.GONE);
                    }
                });
            }
        }

        @Override
        public void onVideoFound(String size, String type, String link, String name, String page, boolean chunked, String website) {
            videoList.addItem(size, type, link, name, page, chunked, website);
            updateFoundVideosBar();
        }
    }

    private void regexVideo() {
        AppConfig.REGEXVIDEOS.add(".*//vssauth.waqu\\.com/.*/normal\\.mp4.*");
        AppConfig.REGEXVIDEOS.add(".*365yg.com.*/video/[a-z]/.*");
        AppConfig.REGEXVIDEOS.add(".*hotsoon\\.snssdk\\.com/hotsoon/item/video.*");
        AppConfig.REGEXVIDEOS.add(".*music.qqvideo.tc\\.qq\\.com/.*\\.mp4.*");
        AppConfig.REGEXVIDEOS.add(".*v7\\.pstatp.com.*/origin/.*");
        AppConfig.REGEXVIDEOS.add(".*pstatp.com.*/video/.*");
        AppConfig.REGEXVIDEOS.add(".*googlevideo.com.*");
        AppConfig.REGEXVIDEOS.add(".*//video.weibo.com/media/.*");
        AppConfig.REGEXVIDEOS.add(".*tumblr.com/video_file/.*");
        AppConfig.REGEXVIDEOS.add(".*//baobab.kaiyanapp.com.*vid=.*");
        AppConfig.REGEXVIDEOS.add("^((?!vmind\\.qqvideo|btrace\\.video|www\\.facebook\\.com|data\\.video\\.qiyi\\.com).)*(\\.mp4|\\.3gp|\\.swf|\\.flv)(?!\\?vframe|%3F|%22%2C|v=).*$");
    }

    public static String script() {
        return "javascript:function toAbsoluteUrl(url)\n{\n    if(url.search(/^\\/\\//) != -1) return window.location.protocol + url;\n    if(url.search(/:\\/\\//) != -1) return url;\n    if(url.search(/^\\//)   != -1) return window.location.origin + url;\n\n    return window.location.href.match(/(.*\\/)/)[0] + url;\n}\n\nfunction getSources(mediaTag)\n{\n    var sources = mediaTag.getElementsByTagName(\"source\");\n    var sourceUrls = [];\n\n    if(sources.length == 0)\n    {\n        return null;\n    }\n\n    for(var i = 0; i < sources.length; ++i)\n    {\n        if(sources[i].hasAttribute(\"src\"))\n        {\n            sourceUrls.push(toAbsoluteUrl(sources[i].getAttribute(\"src\")));\n        }\n    }\n\n    return sourceUrls;\n}\n\nfunction processMediaTag(mediaTag, srcCallBack, sourcesCallBack)\n{\n    if(!mediaTag)\n    {\n        return;\n    }\n    \n    if(mediaTag.hasAttribute(\"src\"))\n    {\n        srcCallBack(toAbsoluteUrl(mediaTag.getAttribute(\"src\")));\n        return;\n    }\n\n    var sourceUrls = getSources(mediaTag);\n\n    if(sourceUrls)\n    {\n        sourcesCallBack(JSON.stringify(sourceUrls));\n        return;\n    }\n}\n\nfunction getFirstPlayingMediaTag(mediaTags)\n{\n    for(var i = 0; i < mediaTags.length; ++i)\n    {\n        if(!mediaTags[i].paused && !mediaTags[i].ended)\n        {\n            return mediaTags[i];\n        }\n    }\n    \n    return null;\n}\n\nfunction onVideoSrcItercept(url)     { window.MyBridg.onVideoSrcItercept(url,document.title);     }\nfunction onVideoSourcesItercept(url) { window.MyBridg.onVideoSourcesItercept(url,document.title); }\n\nfunction onAudioSrcItercept(url)     { window.MyBridg.onAudioSrcItercept(url,document.title);     }\nfunction onAudioSourcesItercept(url) { window.MyBridg.onAudioSourcesItercept(url,document.title); }\n\nfunction processVideoEvent(event)\n{\n    if(event.target.nodeName.toUpperCase() === \"VIDEO\")\n    {\n        processMediaTag(event.target, onVideoSrcItercept, onVideoSourcesItercept);\n    }\n    else if(event.target.contentDocument && event.target.contentDocument.getElementsByTagName('VIDEO').length > 0)\n    {\n        var videoTags = event.target.contentDocument.getElementsByTagName('VIDEO');\n        processMediaTag(getFirstPlayingMediaTag(videoTags), onVideoSrcItercept, onVideoSourcesItercept);\n    }\n}\n\nfunction processAudioEvent(event)\n{\n    if(event.target.nodeName.toUpperCase() === \"AUDIO\")\n    {\n        processMediaTag(event.target, onAudioSrcItercept, onAudioSourcesItercept);\n    }\n    else if(event.target.contentDocument && event.target.contentDocument.getElementsByTagName('AUDIO').length > 0)\n    {\n        var audioTags = event.target.contentDocument.getElementsByTagName('AUDIO');\n        processMediaTag(getFirstPlayingMediaTag(audioTags), onAudioSrcItercept, onAudioSourcesItercept);\n    }\n}\n\ndocument.addEventListener(\"play\", processVideoEvent, true);\ndocument.addEventListener(\"play\", processAudioEvent, true);";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (newUrlLoad) {
//            page.loadUrl(url);
            browserManager.newWindow(url);
            newUrlLoad = false;
        }
    }

    public final class InJavaScriptLocalObj {
        InJavaScriptLocalObj() {
        }

        @JavascriptInterface
        public void getDailymotionUrl(String str) {
        }

        @JavascriptInterface
        public void getDailymotionVideoId(String str) {
        }
    }

    @Override
    public void OnStart(WebView webView) {
    }

    @Override
    public void OnStartsite(WebView webView) {
    }

    @Override
    public void Onfinesh(WebView webView) {
    }

    @Override
    public void OnreciveVideourl(WebView webView, String str, String str2, String str3) {
        Glob.url11 = str;
        Ajax.title = "";
    }

    @Override
    public void onAudioSourcesItercept(String str, String str2) {
        Glob.url11 = str;
        Ajax.title = "";
    }

    @Override
    public void onAudioSrcItercept(String str, String str2) {

    }

    @Override
    public void onItemClick(String str, int i, ArrayList<HashMap<String, String>> arrayList) {
    }


    @Override
    public void onVideoSourcesItercept(String str, String str2) {
        if (str.startsWith("[")) {
            str = str.replace("[", "");
        }
        String[] split = str.replace("]", "").split(",");
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].replace("\"", "");
        }
        Glob.url11 = split[0];
    }

    @Override
    public void onVideoSrcItercept(String str, String str2) {
        Log.e("iiiiiiiiiiiiiiiii", "onVideoSrcItercept.....str...." + str);
        if (str.contains(AppConfig.MP4)) {
            Glob.url11 = str.replace("[", "").replace("]", "");
            Log.e("iiiiiiiiiiiiiiiii", "onVideoSrcItercept.....11111....url11......" + Glob.url11);
            getActivity().runOnUiThread(new Runnable() { // from class: com.mms.videodownloader.AppData.Activity.SearchActivity.2
                @Override // java.lang.Runnable
                public void run() {
                }
            });
        } else {
            Log.e("iiiiiiiiiiiiiiiii", "onVideoSrcItercept.........url11......" + Glob.url11);
        }
        Ajax.title = "";
    }

    @Override
    public void oncreate(WebView webView) {

    }

    @Override
    public void onplay(String str, String str2, String str3) {

    }

/*    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d("~~~", "ProcessActivisty   ");

              finish();
            });*/


}
