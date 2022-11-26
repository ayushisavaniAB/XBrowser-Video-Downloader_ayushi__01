package com.privatebrowser.safebrowser.download.video.download;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;


import java.io.File;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.mainUi.PlayApp;

public class DownloadNotifier {
    private final int ID = 77777;
    public static Intent downloadServiceIntent;
    private Handler handler;
    private NotificationManager notificationManager;
    private DownloadingRunnable downloadingRunnable;

    private class DownloadingRunnable implements Runnable {
        @Override
        public void run() {
            final String downloadFolder = DownloadManager.getDownloadFolder();
            if (downloadFolder != null) {
                String filename = downloadServiceIntent.getStringExtra("name") + "." +
                        downloadServiceIntent.getStringExtra("type");

                Notification.Builder NB;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NB = new Notification.Builder(PlayApp.getInstance().getApplicationContext(), "download_01")
                            .setStyle(new Notification.BigTextStyle());
                } else {
                    NB = new Notification.Builder(PlayApp.getInstance().getApplicationContext())
                            .setSound(null)
                            .setPriority(Notification.PRIORITY_LOW);
                }

                NB.setContentTitle("Downloading " + filename)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(PlayApp.getInstance().getApplicationContext().getResources(), R.mipmap.ic_launcher))
                        .setOngoing(true);

                if (downloadServiceIntent.getBooleanExtra("chunked", false)) {

                    File file = new File(downloadFolder, filename);

                    String downloaded;
                    if (file.exists()) {
                        downloaded = android.text.format.Formatter.formatFileSize(PlayApp.getInstance
                                ().getApplicationContext(), file.length());
                    } else {
                        downloaded = "0KB";
                    }
                    for (int i = 0; i < 100; i += 5) {
                        NB.setProgress(100, i, true);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NB.setStyle(new Notification.BigTextStyle().bigText(downloaded));
                    } else {
                        NB.setContentText(downloaded);
                        NB.setDefaults(0);
                    }

                    notificationManager.notify(ID, NB.build());
                    handler.postDelayed(this, 1000);

                } else {

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

                    NB.setProgress(100, progress, false);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NB.setStyle(new Notification.BigTextStyle().bigText(downloaded + "/" + total + "   " + progress + "%"));
                    } else {
                        NB.setContentText(downloaded + "/" + total + "   " + progress + "%");
                        NB.setDefaults(0);
                    }
                    notificationManager.notify(ID, NB.build());
                    handler.postDelayed(this, 1000);

                }
            }
        }
    }

    DownloadNotifier(Intent downloadServiceIntent) {
        this.downloadServiceIntent = downloadServiceIntent;

        notificationManager = (NotificationManager) PlayApp.getInstance().getApplicationContext().getSystemService
                (Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel channel = new NotificationChannel("download_01", "Download Notification", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
            notificationManager.createNotificationChannel(new NotificationChannel("download_02",
                    "Download Notification", NotificationManager.IMPORTANCE_LOW));
            channel.setSound(null, null);
        }

        HandlerThread thread = new HandlerThread("downloadNotificationThread");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    void notifyDownloading() {
        downloadingRunnable = new DownloadingRunnable();
        downloadingRunnable.run();
    }

    void notifyDownloadFinished() {
        handler.removeCallbacks(downloadingRunnable);
        notificationManager.cancel(ID);

        String filename = downloadServiceIntent.getStringExtra("name") + "." +
                downloadServiceIntent.getStringExtra("type");
        Notification.Builder NB;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NB = new Notification.Builder(PlayApp.getInstance().getApplicationContext(), "download_02")
                    .setTimeoutAfter(1500)
                    .setContentTitle("Download Finished")
                    .setContentText(filename)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(PlayApp.getInstance().getApplicationContext().getResources(),
                            R.mipmap.ic_launcher));
            notificationManager.notify(8888, NB.build());
        } else {
            NB = new Notification.Builder(PlayApp.getInstance().getApplicationContext())
                    .setTicker("Download Finished")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(PlayApp.getInstance().getApplicationContext().getResources(),
                            R.mipmap.ic_launcher));
            notificationManager.notify(8888, NB.build());
            notificationManager.cancel(8888);
        }
    }

    void cancel() {
        if (downloadingRunnable != null) {
            handler.removeCallbacks(downloadingRunnable);
        }
        notificationManager.cancel(ID);
    }
}
