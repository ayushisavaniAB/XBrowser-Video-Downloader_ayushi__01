package com.privatebrowser.safebrowser.download.video.download.browser;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.download.blockedAds.AdBlockManager;
import com.privatebrowser.safebrowser.download.video.download.blockedAds.AdBlocker;
import com.privatebrowser.safebrowser.download.video.utils.PreferenceUtil;

public class BrowserManager extends Fragment {
    public List<BrowserWindow> windows;
    @Deprecated
    private AdBlocker adBlocker;
    private AdBlockManager adblock = new AdBlockManager();
    PreferenceUtil prefernces;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        windows = new ArrayList<>();
        prefernces = new PreferenceUtil(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupAdBlock();
    }

    public List<BrowserWindow> getAllWindow() {
        return windows;
    }

    private void setupAdBlock() {
        String lastUpdated = prefernces.getString(getString(R.string.adFiltersLastUpdated), "");
        adblock.update(lastUpdated, new AdBlockManager.UpdateListener() {
            @Override
            public void onAdBlockUpdateBegins() {
            }

            @Override
            public void onAdBlockUpdateEnds() {
            }

            @Override
            public void onUpdateFiltersLastUpdated(String today) {
                if (isAdded()) {
                    prefernces.putString(getString(R.string.adFiltersLastUpdated), today);
                }
            }

            @Override
            public void onSaveFilters() {
                if (getActivity() != null) {
                    adblock.saveFilters(getActivity());
                }
            }

            @Override
            public void onLoadFilters() {
                adblock.loadFilters(getActivity());
            }
        });
    }

    @Deprecated
    private void setupAdBlocker() {
        try {
            File file = new File(getActivity().getFilesDir(), "ad_filters.dat");
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                adBlocker = (AdBlocker) objectInputStream.readObject();
                objectInputStream.close();
                fileInputStream.close();
            } else {
                adBlocker = new AdBlocker();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(adBlocker);
                objectOutputStream.close();
                fileOutputStream.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void newWindow(String url) {
        Bundle data = new Bundle();
        data.putString("url", url);
        BrowserWindow window = new BrowserWindow();
        window.setArguments(data);
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.mainFrame, window, null)
                .commit();
        windows.add(window);
        ((BrowsingActivity) getActivity()).setOnBackPressedListener(window);
        if (windows.size() > 1) {
            window = windows.get(windows.size() - 2);
            if (window != null && window.getView() != null) {
                window.getView().setVisibility(View.GONE);
            }
        }
        updateNumWindows();
    }

    public void closeWindow2(BrowserWindow window) {
        windows.remove(window);
        getFragmentManager().beginTransaction().remove(window).commit();
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow != null && topWindow.getView() != null) {
                topWindow.getView().setVisibility(View.VISIBLE);
            }
            ((BrowsingActivity) getActivity()).setOnBackPressedListener(topWindow);
        } else {
            ((BrowsingActivity) getActivity()).setOnBackPressedListener(null);
        }
        updateNumWindows();
    }

    public void closeWindow(BrowserWindow window) {
        windows.remove(window);
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow != null && topWindow.getView() != null) {
                topWindow.getView().setVisibility(View.VISIBLE);
            }
            ((BrowsingActivity) getActivity()).setOnBackPressedListener(topWindow);
        } else {
            ((BrowsingActivity) getActivity()).finish();
            ((BrowsingActivity) getActivity()).setOnBackPressedListener(null);
        }
        updateNumWindows();
    }

    public void closeWindowAll() {
        if (windows == null) {
            windows.clear();
        }
        windows = new ArrayList<>();
        updateNumWindows();
    }

    public void switchWindow(int index) {
        BrowserWindow topWindow = windows.get(windows.size() - 1);
        if (topWindow.getView() != null) {
            topWindow.getView().setVisibility(View.GONE);
        }
        BrowserWindow window = windows.get(index);
        windows.remove(index);
        windows.add(window);
        if (window.getView() != null) {
            window.getView().setVisibility(View.VISIBLE);
            ((BrowsingActivity) getActivity()).setOnBackPressedListener(window);
        }
    }

    void updateNumWindows() {
        for (BrowserWindow window : windows) {
            window.updateNumWindows(windows.size());
        }
    }

    public void hideCurrentWindow() {
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow.getView() != null) {
                topWindow.getView().setVisibility(View.GONE);
            }
        }
    }

    public void unhideCurrentWindow() {
        if (windows.size() > 0) {
            BrowserWindow topWindow = windows.get(windows.size() - 1);
            if (topWindow.getView() != null) {
                topWindow.getView().setVisibility(View.VISIBLE);
                ((BrowsingActivity) getActivity()).setOnBackPressedListener(topWindow);
            }
        } else {
            ((BrowsingActivity) getActivity()).setOnBackPressedListener(null);
        }
    }

    @Deprecated
    public void updateAdFilters() {
        if (adBlocker != null) {
            adBlocker.update(getActivity());
        } else {
            setupAdBlocker();
            if (adBlocker != null) {
                adBlocker.update(getActivity());
            } else {
                File file = new File(getActivity().getFilesDir(), "ad_filters.dat");
                if (file.exists()) {
                    if (file.delete()) {
                        setupAdBlocker();
                        if (adBlocker != null) {
                            adBlocker.update(getActivity());
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    public boolean checkUrlIfAds(String url) {
        return adBlocker.checkThroughFilters(url);
    }

    public boolean isUrlAd(String url) {
//        Log.i("loremarTest", "Finding ad in url: " + url);
        boolean isAd = adblock.checkThroughFilters(url);
        if (isAd) {
//            Log.i("loremarTest", "Detected ad: " + url);
        }
        return isAd;
    }

}
