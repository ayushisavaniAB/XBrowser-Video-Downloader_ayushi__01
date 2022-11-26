package com.privatebrowser.safebrowser.download.video.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.material.color.MaterialColors;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.privatebrowser.safebrowser.download.video.R;
import com.privatebrowser.safebrowser.download.video.databinding.DialogVideoDetailsBinding;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;
import think.outside.the.box.handler.APIManager;

public class Utility {

    public static boolean isConverter = false;

    public static void hideNavigation(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static String getFileName(String fileName) {
        return fileName.split("\\.")[0];
    }

    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getFileExtension(String fileName) {
        String fileNameArray[] = fileName.split("\\.");
        return fileNameArray[fileNameArray.length - 1];

    }

    public static String getNameFromFile(String name) {
        if (name.indexOf(".") > 0)
            name = name.substring(0, name.lastIndexOf("."));
        return name;
    }

//    public static String getVideoName(String path){
//        return new File(path).getName();
//    }

    public static long getFilePathToMediaID(String videoPath, Context context) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Video.Media.DATA;
        String[] selectionArgs = {videoPath};
        String[] projection = {MediaStore.Video.Media._ID};
        String sortOrder = MediaStore.Video.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }

        return id;
    }

    public static long getFilePathToMediaIDAudio(String songPath, Context context) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {songPath};
        String[] projection = {MediaStore.Audio.Media._ID};
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }

        return id;
    }


    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
            Log.e("pm", "appInstalledOrNot: " + pm + "  " + uri);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("TAG ", "appInstalledOrNot: " + e.getMessage());
            app_installed = false;
        }
        return app_installed;
    }

    public static String getFormatType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String mime = fileNameMap.getContentTypeFor("file://" + path);
        return mime;
    }

    public static String getModifiedDateString(String longDate) {
        return (String) DateFormat.format("dd/MM/yyyy hh:mm:ss", new Date(Long.parseLong(longDate)));
    }

    public static String getModifiedDateString(long longDate) {
        return (String) DateFormat.format("dd/MM/yyyy", new Date(longDate));
    }

    public static String getFileNameFromPath(String path) {
        if (path == null) return "Unknown File";
        int i = path.lastIndexOf("/");
        if (i == 0) return path;
        return path.substring(i);

    }

    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @SuppressLint("BadHostnameVerifier")
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity, IBinder token) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && token != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    token, 0);
        }
    }

    public static int[] getGradientColor(Context context) {
        int[] shaderColor = new int[]{
                MaterialColors.getColor(context, R.attr.gradientColo1, context.getResources().getColor(R.color.color01)),
                MaterialColors.getColor(context, R.attr.gradientColo2, context.getResources().getColor(R.color.color02)),
                MaterialColors.getColor(context, R.attr.gradientColo3, context.getResources().getColor(R.color.color03))
        };
        return shaderColor;
    }

    public static Bitmap addGradient(Context context, Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);

        canvas.drawBitmap(originalBitmap, 0, 0, null);

        Paint paint = new Paint();
//        LinearGradient shader = new LinearGradient(0, 0, 0, height, shaderColor, /*new float[]{2, 1, 0}*/null, Shader.TileMode.CLAMP);
        LinearGradient shaderA = new LinearGradient(0, 0, 0, height, getGradientColor(context), null, Shader.TileMode.CLAMP);
//        GradientDrawable appTheme = new GradientDrawable(GradientDrawable.Orientation.BL_TR, shaderColor);

//        Shader shaderB = new BitmapShader(appTheme, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        paint.setShader(new ComposeShader(shaderA, shaderB, PorterDuff.Mode.SRC_IN));
        paint.setShader(shaderA);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);

        return updatedBitmap;
    }

//    public static Bitmap addGradient(Context context, Bitmap blurBitmap) {
////        int[] mBackShadowColors = new int[] { 0xFF000000 , 0x00000000};
//        int[] shaderColor = new int[]{
//                context.getResources().getColor(R.color.black),
//                context.getResources().getColor(R.color.white),
//                context.getResources().getColor(R.color.color2),
//        };
//        GradientDrawable gradientDrawable = new GradientDrawable(
//                GradientDrawable.Orientation.BOTTOM_TOP, shaderColor);
//        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//        gradientDrawable.setBounds(0, 0, blurBitmap.getWidth(), blurBitmap.getHeight());
////        Canvas canvas = new Canvas(blurBitmap);
//        int width = blurBitmap.getWidth();
//        int height = blurBitmap.getHeight();
//        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(updatedBitmap);
////
//        canvas.drawBitmap(blurBitmap, 0, 0, null);
//        gradientDrawable.draw(canvas);
//        return updatedBitmap;
//    }

    public static Shader setTextGradient(Context context, TextView txt) {
        Shader textShader = new LinearGradient(0, 0,
                txt.getPaint().measureText(txt.getText().toString()),
                txt.getTextSize(),
                getGradientColor(context), null, Shader.TileMode.REPEAT);
        return textShader;
    }

    public static long getDuration(Context context,/* Uri uri*/File path) {
        int duration = 0;
        Uri uri = getRealUri(context, path);
        ;
        MediaPlayer mp = MediaPlayer.create(context, uri);
        if (mp != null) {
            duration = mp.getDuration();
        }

        Log.e("duration", "getVideoDuration: " + duration);

//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(path);
//        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
//////        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//////        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
//        retriever.release();

        return duration;
    }

    public static String getVideoResolution(/*Context context*/String path) {
        try {
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(path);
            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//            Log.e("w h ", "getVideoResolution: " + width + "  " + height );
            return width + " x " + height;
        } catch (Exception e) {
            Log.e("error", "getVideoResolution: " + e.getMessage());
            return "";
        }
    }

    public static void detailsDialog(Context context, VideoModel videoModel) {
        final Dialog dialog = new Dialog(context, R.style.WideDialog);
        DialogVideoDetailsBinding videoDetailsBinding = DialogVideoDetailsBinding.inflate(LayoutInflater.from(context), null, false);
        dialog.setContentView(videoDetailsBinding.getRoot());
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        if (videoModel.getPath() != null) {
//            videoDetailsBinding.nameText.setText(Utility.getVideoName(videoItem.getPath()));
            videoDetailsBinding.locationText.setText(videoModel.getPath());
            videoDetailsBinding.formatText.setText(Utility.getFormatType(videoModel.getPath()));
        }

        if (videoModel.getName() != null) {
            videoDetailsBinding.nameText.setText(videoModel.getName());
//            videoDetailsBinding.formatText.setText(Utility.getFileExtension(videoModel.getName()));
        }

        videoDetailsBinding.durationText.setText(Converters.makeShortTimeString(videoModel.getDuration()));


        if (videoModel.getModifiedDate() != null) {
            videoDetailsBinding.dateText.setText(Utility.getModifiedDateString(videoModel.getModifiedDate()));
        } else {
            videoDetailsBinding.dateText.setText(Utility.getModifiedDateString(new File(videoModel.getPath()).lastModified()));
        }

        if (videoModel.getLength() != null) {
            videoDetailsBinding.fileSizeText.setText(Converters.formatSize(Long.parseLong(videoModel.getLength())));
        } else {
            videoDetailsBinding.fileSizeText.setText(Converters.formatSize(new File(videoModel.getPath()).length()));
        }

        if (videoModel.getResolution() != null) {
            videoDetailsBinding.resolutionText.setText(videoModel.getResolution());
        } else {
            videoDetailsBinding.resolutionText.setText(Utility.getVideoResolution(videoModel.getPath()));
        }

        videoDetailsBinding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIManager.showInter((Activity) context, false, isfail -> {
                    dialog.dismiss();
                });
            }
        });
    }

    //
//    public static void audioDetailsDialog(Context context, SongsModel songsModel) {
//        final Dialog dialog = new Dialog(context, R.style.WideDialog);
//        DialogAudioDetailsBinding audioDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_audio_details, null, false);
//        dialog.setContentView(audioDetailsBinding.getRoot());
//        dialog.setCancelable(false);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        dialog.show();
//
//        if (songsModel.getName() != null) {
//            audioDetailsBinding.titleText.setText(Utility.getFileName(songsModel.getName()));
//        }
//
//        if (songsModel.getAlbum() != null) {
//            audioDetailsBinding.albumText.setText(songsModel.getAlbum());
//        }
//
//        if (songsModel.getArtist() != null) {
//            audioDetailsBinding.artistText.setText(songsModel.getArtist());
//        }
//
//        if (songsModel.getPath() != null) {
//            audioDetailsBinding.locationText.setText(songsModel.getPath());
////            audioDetailsBinding.formatText.setText(Utility.getFileExtension(songsModel.getPath()));
//            audioDetailsBinding.formatText.setText(Utility.getFormatType(songsModel.getPath()));
//        }
//
//        if (songsModel.getDuration() != 0) {
//            audioDetailsBinding.durationText.setText(Converters.makeShortTimeString(songsModel.getDuration()));
//        } else {
//            audioDetailsBinding.durationText.setText(Converters.makeShortTimeString(Utility.getDuration(context, new File(songsModel.getPath()))));
//        }
//
//        if (songsModel.getDateModified() != null) {
//            audioDetailsBinding.dateText.setText(Utility.getModifiedDateString(songsModel.getDateModified()));
//        } else {
//            audioDetailsBinding.dateText.setText(Utility.getModifiedDateString(new File(songsModel.getPath()).lastModified()));
//        }
//
//        if (songsModel.getSize() != null) {
//            audioDetailsBinding.fileSizeText.setText(Converters.formatSize(Long.parseLong(songsModel.getSize())));
//        } else {
//            audioDetailsBinding.fileSizeText.setText(Converters.formatSize(new File(songsModel.getPath()).length()));
//        }
//
//        audioDetailsBinding.okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//    }
//
    public static Intent shareVideo(final Context context, VideoModel videoItem) {


        if (videoItem == null) return new Intent();

        String filePath = videoItem.getPath();
        if (filePath == null) return new Intent();
        File shareFile = new File(filePath);
        if (!shareFile.exists()) return new Intent();
        String fileType = getFileExtension(filePath);

        if (shareFile.exists()) {
            try {
                if (fileType.equals("Mp4") || filePath.equals("mp4") || filePath.equals("MP4"))
                    return new Intent()
                            .setAction(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_STREAM, getRealUri(context, shareFile))
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .setType("video/*");
                else {
                    return new Intent()
                            .setAction(Intent.ACTION_SEND)
                            .putExtra(Intent.EXTRA_STREAM, getRealUri(context, shareFile))
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            .setType("file/*");
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                return new Intent();
            }
        }
        //Toast.makeText(context, "File cannot share, (:", Toast.LENGTH_SHORT).show();
        return new Intent();
    }

    public static Uri getRealUri(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT > 21) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
//
//    public static void shareMultiVideo(final Context context, List<VideoModel> videoItems) {
//        ArrayList<Uri> files = new ArrayList<>();
//        File f;
//        for (VideoModel videoItem : videoItems) {
//            f = new File(videoItem.getPath());
//            if (f.exists()) {
//                Uri uri;
//                try {
//                    uri = getRealUri(context, f);
//
//                } catch (IllegalArgumentException e) {
//                    uri = null;
//                    e.printStackTrace();
//                }
//                if (uri != null) files.add(uri);
//            }
//        }
//        if (files.size() > 0) {
//            try {
//                Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
//                share.putExtra(Intent.EXTRA_SUBJECT, "Share all video files.");
//                share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
//                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                share.setType("file/*");
//                context.startActivity(Intent.createChooser(share, "Share To"));
//            } catch (IllegalArgumentException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
