package com.privatebrowser.safebrowser.download.video.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.privatebrowser.safebrowser.download.video.videos.model.VideoFolderModel;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoModel;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class DataUtils {
    public static ArrayList<VideoModel> videoList;
    public static ArrayList<VideoFolderModel> videoFolderList;

    public static ArrayList<VideoModel> getVideosByFolder(Context context, String bucket, String order) {
        ArrayList<VideoModel> videoList = new ArrayList<>();
        Log.e("TAG", "getVideosByFolder: "+bucket+"  "+order);

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
        };
        Cursor query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
        if (order.equals("name_asc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.DISPLAY_NAME + " ASC");
        } else if (order.equals("name_dsc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.DISPLAY_NAME + " DESC");
        } else if (order.equals("date_asc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.DATE_MODIFIED + " ASC");
        } else if (order.equals("date_dsc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
        } else if (order.equals("size_asc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.SIZE + " ASC");
        } else if (order.equals("size_dsc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, "bucket_display_name =?", new String[]{bucket}, MediaStore.Video.Media.SIZE + " DESC");
        }
        Log.e("TAG", "getVideosByFolder: "+query.getCount() );


        while (query.moveToNext()) {
            VideoModel  videoItem = new VideoModel();
            videoItem.setId(Integer.parseInt(query.getString(0)));
            videoItem.setTitle(query.getString(2));
            videoItem.setPath(query.getString(3));
            videoItem.setName(query.getString(4));
            if(query.getString(5)!=null)
            videoItem.setDuration(Long.parseLong(query.getString(5)));
            Log.e("TAG", "getVideosByFoldersss: "+query.getString(5)+"   "+videoItem.getDuration());
            videoList.add(videoItem);
        }


        Log.e("TAG", "getVideosByFolder:videoList "+videoList.size() );
//        if (query != null) {
//            while (query.moveToNext()) {
//
//
//                VideoModel videoModel = new VideoModel(query.getInt(0), query.getString(1), query.getString(2), query.getString(3), query.getString(4), query.getString(5), query.getString(6), query.getString(7), query.getString(8));
//                videoList.add(videoModel);
//            }
//            query.close();
//        }
        return videoList;
    }


    public static ArrayList<VideoFolderModel> getVideoFolder(Context context) {
        if (videoFolderList != null) {
            videoFolderList.clear();
        }
        videoFolderList = new ArrayList<>();
        Cursor query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"bucket_display_name", "bucket_id"}, null, null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
//        Log.e("count", "getVideoFolder: " + query.getCount() );
//        ArrayList arrayList = new ArrayList();
//        ArrayList arrayList2 = new ArrayList();
//        String folderName = "";

        if (query != null) {
            Set set = new TreeSet();
//            Set set2 = new TreeSet();
//            LinkedHashSet linkedHashSet = new LinkedHashSet();
//            LinkedHashSet linkedHashSet2 = new LinkedHashSet();
//            linkedHashSet.clear();
//            linkedHashSet2.clear();
            while (query.moveToNext()) {
                if(query.getString(0) != null) {
                    set.add(query.getString(0));
//                    set2.add(query.getString(1));
                }
//                linkedHashSet.add(query.getString(0));
//                linkedHashSet2.add(query.getString(1));
//                Log.e("name", "getVideoFolder: " + folderName + "  " + query.getString(0) );
//                if(query.getString(0) != null) {
//                    if (folderName.equals("")) {
//                        Log.e("call if", "getVideoFolder: " + folderName);
//                        folderName = query.getString(0);
//                        arrayList.add(query.getString(0));
//                        arrayList2.add(query.getString(1));
//                    }
////                    Log.e("name2", "getVideoFolder: " + folderName + "  " + query.getString(0));
//                    Log.e("ignore", "getVideoFolder: " + folderName.equalsIgnoreCase(query.getString(0)) );
//                    Log.e("ignore not", "getVideoFolder: " + folderName.equals(query.getString(0)) );
//                    if (!folderName.equalsIgnoreCase(query.getString(0))) {
//                        folderName = query.getString(0);
//                        Log.e("call if2", "getVideoFolder: " + folderName + "  " + query.getString(0));
//                        arrayList.add(query.getString(0));
//                        arrayList2.add(query.getString(1));
//                    }
//                }
//                Log.e("add", "getVideoFolder: " + query.getString(0) + "  " + query.getString(1) );
            }
            query.close();

//            ArrayList arrayList = new ArrayList(linkedHashSet);
//            ArrayList arrayList2 = new ArrayList(linkedHashSet2);
            ArrayList arrayList = new ArrayList(set);
//            ArrayList arrayList2 = new ArrayList(set2);

            int count = 0;
            long fid;
//            Log.e("array size", "getVideoFolder: " + arrayList.size() + "  " /*+ arrayList2.size()*/);
            for (int i = 0; i < arrayList.size(); i++) {
//                Log.e("name", "getVideoFolder: " + arrayList.get(i) );
//                if (arrayList.get(i) != null) {
//                    Log.e("i", "getVideoFolder: " + arrayList.get(i)+ "  2  " + arrayList2.get(i) );
                String name = arrayList.get(i).toString();
//                    fid = Integer.parseInt(arrayList2.get(i).toString());
                fid = getVideoFolderID(context,name);
//                Log.e("fid", "getVideoFolder: " + fid );
                Cursor query2 = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_data"}, "bucket_display_name =?", new String[]{name}, null);
                if (query2 != null) {
                    count = query2.getCount();
                }
                videoFolderList.add(new VideoFolderModel((int) fid, name, count));
//                }
            }
        }
        return videoFolderList;
    }

    public static long getVideoFolderID(Context context, String name) {
        long id = 0;
        ContentResolver cr = context.getContentResolver();

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.BUCKET_DISPLAY_NAME;
        String[] selectionArgs = {name};
        String[] projection = {MediaStore.Video.Media._ID};

        Cursor cursor = cr.query(uri, projection, selection + "=?", selectionArgs, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID);
                id = Long.parseLong(cursor.getString(idIndex));
//                Log.e("id", "getVideoFolderID: " + id);
            }
        }
        return id;
    }

    public static ArrayList<VideoModel> getAllVideos(Context context, String order) {
        if (videoList != null) {
            videoList.clear();
        }
        videoList = new ArrayList<>();
        String[] strArr = {"_id", "_data", "title", "_display_name", "bucket_display_name", "duration", "_size", "date_modified", "resolution"};
        Cursor query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
        if (order.equals("name_asc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.DISPLAY_NAME + " ASC");
        } else if (order.equals("name_dsc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.DISPLAY_NAME + " DESC");
        } else if (order.equals("date_asc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.DATE_MODIFIED + " ASC");
        } else if (order.equals("date_dsc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.DATE_MODIFIED + " DESC");
        } else if (order.equals("size_asc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.SIZE + " ASC");
        } else if (order.equals("size_dsc")) {
            query = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, strArr, null, null, MediaStore.Video.Media.SIZE + " DESC");
        }
        if (query != null) {
            while (query.moveToNext()) {
                VideoModel videoModel = new VideoModel(query.getInt(0), query.getString(1), query.getString(2), query.getString(3), query.getString(4), query.getLong(5), query.getString(6), query.getString(7), query.getString(8));
                videoList.add(videoModel);
            }
            query.close();
        }
        return videoList;
    }
}
