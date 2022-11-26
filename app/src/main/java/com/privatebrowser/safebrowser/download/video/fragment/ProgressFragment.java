package com.privatebrowser.safebrowser.download.video.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;


import java.io.File;

import com.privatebrowser.safebrowser.download.video.databinding.FragmentProgressBinding;
import com.privatebrowser.safebrowser.download.video.download.DownloadManager;
import com.privatebrowser.safebrowser.download.video.download.DownloadNotifier;
import com.privatebrowser.safebrowser.download.video.mainUi.HelpActivity;
import com.privatebrowser.safebrowser.download.video.mainUi.PlayApp;
import com.privatebrowser.safebrowser.download.video.utils.Glob;
import think.outside.the.box.handler.APIManager;

public class ProgressFragment extends Fragment {
    public static boolean downloadComplete = false;
    public static Context context;
    private Handler handler = new Handler();
    FragmentProgressBinding binding;
    public static Intent downloadServiceIntent;
    private DownloadingRunnable downloadingRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProgressBinding.inflate(inflater, container, false);
        Glob.videoDownloader=true;
        context = getActivity();
        APIManager.showNative(binding.adNative);
        binding.howUse.setOnClickListener(v -> {
            APIManager.showInter(getActivity(), false, b -> {
                startActivity(new Intent(getActivity(), HelpActivity.class));
            });
        });

        binding.navigation.setOnClickListener(view -> getActivity().onBackPressed());

        downloadingRunnable = new DownloadingRunnable();
        downloadingRunnable.run();
        return binding.getRoot();
    }

    private class DownloadingRunnable implements Runnable {
        @SuppressLint("SetTextI18n")
        @Override
        public void run() {
            String downloadFolder = DownloadManager.getDownloadFolder();
            downloadServiceIntent = DownloadNotifier.downloadServiceIntent;

            if (downloadFolder != null && downloadServiceIntent != null) {
                binding.downloadProcess.setVisibility(View.VISIBLE);
                binding.noHistory.setVisibility(View.GONE);
                String filename = downloadServiceIntent.getStringExtra("name") + "." +
                        downloadServiceIntent.getStringExtra("type");

                if (downloadServiceIntent.getBooleanExtra("chunked", false)) {

                    binding.linearProgress.setVisibility(View.VISIBLE);
                    binding.seekBar.setVisibility(View.GONE);

                    File file = new File(downloadFolder, filename);

                    String downloaded;
                    if (file.exists()) {
                        downloaded = android.text.format.Formatter.formatFileSize(PlayApp.getInstance
                                ().getApplicationContext(), file.length());
                    } else {
                        downloaded = "0KB";
                    }

                    if (downloadComplete) {
                        binding.downloadProcess.setVisibility(View.GONE);
                        binding.noHistory.setVisibility(View.VISIBLE);
                        downloadServiceIntent = null;
                        handler.removeCallbacks(this);
                    }

                    binding.fileName.setText(filename);
                    binding.progressText.setText("" + downloaded);
                    handler.postDelayed(this, 1000);

                } else {

                    binding.linearProgress.setVisibility(View.GONE);
                    binding.seekBar.setVisibility(View.VISIBLE);

                    File file = new File(downloadFolder, filename);
                    String sizeString = downloadServiceIntent.getStringExtra("size");
                    int progress = (int) Math.ceil(((double) file.length() / (double) Long.parseLong
                            (sizeString)) * 100);
                    progress = progress >= 100 ? 100 : progress;

                    String downloaded = android.text.format.Formatter.formatFileSize(PlayApp
                            .getInstance().getApplicationContext(), file.length());
                    String total = android.text.format.Formatter.formatFileSize(PlayApp.getInstance()
                            .getApplicationContext(), Long.parseLong
                            (sizeString));
                    binding.fileName.setText(filename);
                    binding.seekBar.setProgress(progress);
                    binding.progressText.setText("" + downloaded + "/" + total + "   " + progress + "%");
                    if (progress == 100) {
                        binding.downloadProcess.setVisibility(View.GONE);
                        binding.noHistory.setVisibility(View.VISIBLE);
                        downloadServiceIntent = null;
                    }
                    handler.postDelayed(this, 1000);
                }
            } else {
                binding.downloadProcess.setVisibility(View.GONE);
                binding.noHistory.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void downloadComplete() {
        downloadComplete = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        downloadingRunnable = new DownloadingRunnable();
        downloadingRunnable.run();
    }
}