package com.privatebrowser.safebrowser.download.video.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Converters {
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
    public static int SlowMoFactorConverter(int slowMoConstant)
    {
        slowMoConstant=slowMoConstant-150;
        int y=slowMoConstant/50;
        return y+2;

    }

    public static byte[] getImageFromResource(Context context, int resource){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapdata = stream.toByteArray();
        return bitmapdata;
    }

    public static final String makeShortTimeString(long durationValue) {
        long h =  TimeUnit.MILLISECONDS.toHours((long) durationValue);
        long m =  TimeUnit.MILLISECONDS.toMinutes((long) durationValue) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) durationValue));
        long s =  TimeUnit.MILLISECONDS.toSeconds((long) durationValue) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) durationValue));
//        Log.e("TAG", "makeShortTimeString: " + h  + ":" + m +" : " + s );
        if(h == 0) {
           return String.format("%02d:%02d", m, s);
       }else {
           return String.format("%02d:%02d:%02d", h, m, s);
       }
    }

    public static String formatSize(long j) {
        if (j <= 0) {
            return "0";
        }
        double d = (double) j;
        int log10 = (int) (Math.log10(d) / Math.log10(1024.0d));
        StringBuilder sb = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.#");
        double pow = Math.pow(1024.0d, (double) log10);
        Double.isNaN(d);
        sb.append(decimalFormat.format(d / pow));
        sb.append(" ");
        sb.append(new String[]{"B", "KB", "MB", "GB", "TB"}[log10]);
        return sb.toString();
    }

    public static String BytesToMb(String bytes)
    {
        String size;
        Double bytesInDouble = Double.parseDouble(bytes);
        Double kb = bytesInDouble/1024.0;
        Double mb = kb/1024.0;
        Double gb = kb/1048576.0;

        DecimalFormat dec = new DecimalFormat("0.00");

        if ( gb>1 ) {
            size = dec.format(gb).concat(" GB");
        } else if ( mb>1 ) {
            size = dec.format(mb).concat(" MB");
        } else {
            size = dec.format(kb).concat(" KB");
        }
        return size;
    }
}
