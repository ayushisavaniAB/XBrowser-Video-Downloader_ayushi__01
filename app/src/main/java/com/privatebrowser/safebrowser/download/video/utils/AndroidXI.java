package com.privatebrowser.safebrowser.download.video.utils;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;

import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;


public class AndroidXI {

    private Context context;

    public static AndroidXI getInstance() {
        return new AndroidXI();
    }

    public AndroidXI with(Context context) {
        this.context = context;
        return this;
    }

    /**
     * Create new media uri.
     */
    public Uri create(String directory, String filename, String mimetype) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();

        //Set filename, if you don't system automatically use current timestamp as name
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);

        //Set mimetype if you want
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimetype);

        //To create folder in Android directories use below code
        //pass your folder path here, it will create new folder inside directory
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directory);
        }

        //pass new ContentValues() for no values.
        //Specified uri will save object automatically in android specified directories.
        //ex. MediaStore.Images.Media.EXTERNAL_CONTENT_URI will save object into android Pictures directory.
        //ex. MediaStore.Videos.Media.EXTERNAL_CONTENT_URI will save object into android Movies directory.
        //if content values not provided, system will automatically add values after object was written.
        return contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
    }

    /**
     * Delete file.
     * <p>
     * If {@link ContentResolver} failed to delete the file, use trick,
     * SDK version is >= 29(Q)? use {@link SecurityException} and again request for delete.
     * SDK version is >= 30(R)? use {@link MediaStore#createDeleteRequest(ContentResolver, Collection)}.
     */
    public void delete(ActivityResultLauncher<IntentSenderRequest> launcher, String path) {

        ContentResolver contentResolver = context.getContentResolver();
        long fileId = getFilePathToMediaID(path);
        Uri uriDelete = ContentUris.withAppendedId(
                MediaStore.Video.Media.getContentUri("external"),
                fileId
        );
        try {
            //delete object using resolver
            int result = contentResolver.delete(uriDelete, null, null);

        } catch (SecurityException e) {
            PendingIntent pendingIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                ArrayList<Uri> collection = new ArrayList<>();
                collection.add(uriDelete);
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                //if exception is recoverable then again send delete request using intent
                if (e instanceof RecoverableSecurityException) {
                    RecoverableSecurityException exception = (RecoverableSecurityException) e;
                    pendingIntent = exception.getUserAction().getActionIntent();
                }
            }

            if (pendingIntent != null) {
                IntentSender sender = pendingIntent.getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                launcher.launch(request);
            }
        }
    }

    public void deleteMultiple(ActivityResultLauncher<IntentSenderRequest> launcher, ArrayList<VideoModel> list) {
        ContentResolver contentResolver = context.getContentResolver();
        try {
            for(VideoModel videoModel: list ) {
                long fileId = getFilePathToMediaID(videoModel.getPath());
                Uri uriDelete = ContentUris.withAppendedId(
                        MediaStore.Video.Media.getContentUri("external"),
                        fileId
                );
                //delete object using resolver
                int result = contentResolver.delete(uriDelete, null, null);
            }

        } catch (SecurityException e) {
            PendingIntent pendingIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                ArrayList<Uri> collection = new ArrayList<>();
                for(VideoModel videoModel: list ) {
                    long fileId = getFilePathToMediaID(videoModel.getPath());
                    Uri uriDelete = ContentUris.withAppendedId(
                            MediaStore.Video.Media.getContentUri("external"),
                            fileId
                    );
                    collection.add(uriDelete);
                }
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                //if exception is recoverable then again send delete request using intent
                if (e instanceof RecoverableSecurityException) {
                    RecoverableSecurityException exception = (RecoverableSecurityException) e;
                    pendingIntent = exception.getUserAction().getActionIntent();
                }
            }

            if (pendingIntent != null) {
                IntentSender sender = pendingIntent.getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                launcher.launch(request);
            }
        }
    }

    public void deleteAudio(ActivityResultLauncher<IntentSenderRequest> launcher, String path) {
        ContentResolver contentResolver = context.getContentResolver();
        long fileId = getFilePathToMediaIDAudio(path);
        Uri uriDelete = ContentUris.withAppendedId(
                MediaStore.Audio.Media.getContentUri("external"),
                fileId
        );
        try {

            //delete object using resolver
            int result = contentResolver.delete(uriDelete, null, null);

        } catch (SecurityException e) {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ArrayList<Uri> collection = new ArrayList<>();
                collection.add(uriDelete);
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, collection);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //if exception is recoverable then again send delete request using intent
                if (e instanceof RecoverableSecurityException) {
                    RecoverableSecurityException exception = (RecoverableSecurityException) e;
                    pendingIntent = exception.getUserAction().getActionIntent();
                }
            }
            if (pendingIntent != null) {
                IntentSender sender = pendingIntent.getIntentSender();
                IntentSenderRequest request = new IntentSenderRequest.Builder(sender).build();
                launcher.launch(request);
            }
        }
    }

    public long getFilePathToMediaIDAudio(String path) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.DATA;
        String[] selectionArgs = {path};
        String[] projection = {MediaStore.Audio.Media._ID};

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
            }
        }
        return id;
    }

    public void rename(Uri uri, String rename) {

        //create content values with new name and update
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, rename);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int result = context.getContentResolver().update(uri, contentValues, null);
        }
    }

    public long getFilePathToMediaID(String videoPath) {
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

    /**
     * Rename file.
     *
     * @param path    - filepath.
     * @param rename - the name you want to replace with original.
     */
    public boolean rename(String path, String rename) {

        Uri uri = getUri(context.getContentResolver(), path);

        //create content values with new name and update
        int result = -1 ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, rename);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            result = context.getContentResolver().update(uri, contentValues, null);
        }
        if (result == -1) {
            return false;
        }else {
//            Log.e("rename", "rename: " + new File() );
            return true;
        }
    }

    public boolean renameAudio(String path, String rename) {

        Uri uriRename = getUri(context.getContentResolver(), path);


        //create content values with new name and update
        int result = -1 ;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, rename);
//        contentValues.put(MediaStore.MediaColumns.TITLE, rename.split("\\.")[0]);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            result = context.getContentResolver().update(uriRename, contentValues, null);
        }
        if (result == -1)
            return false;
        else
            return true;
    }

    public static Uri getUri(ContentResolver cr, String path){
        Uri mediaUri = MediaStore.Files.getContentUri("external");
        Cursor ca = cr.query(mediaUri, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
//            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            int id = ca.getInt(0);
            ca.close();
            return  MediaStore.Files.getContentUri("external",id);
        }
        if(ca != null) {
            ca.close();
        }
        return null;
    }
    /**
     * Duplicate file.
     *
     * @param uri - filepath.
     */
    public Uri duplicate(Uri uri) {

        ContentResolver contentResolver = context.getContentResolver();

        Uri output = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());

        String input = getPathFromUri(uri);

        try (InputStream inputStream = new FileInputStream(input)) { //input stream

            OutputStream out = contentResolver.openOutputStream(output); //output stream

            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                out.write(buf, 0, len); //write input file data to output file
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output;
    }

    /**
     * Get file path from uri.
     */
    @SuppressLint("Range")
    public String getPathFromUri(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        String text = null;

        if (cursor.moveToNext()) {
            text = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        }

        cursor.close();

        return text;
    }
}