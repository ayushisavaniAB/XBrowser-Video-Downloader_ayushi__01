package com.privatebrowser.safebrowser.download.video.videos.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.privatebrowser.safebrowser.download.video.videos.model.PlayListModel;
import com.privatebrowser.safebrowser.download.video.videos.model.VideoFolderModel;

import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "PlayLYDatabase.db";
    public String DB_PATH;
    public Context context;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        this.DB_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
//        Log.e("path", "DataBaseHelper: " + DB_PATH );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE PlayListFolderTable (fid INTEGER PRIMARY KEY AUTOINCREMENT,folderName Text)");
        sqLiteDatabase.execSQL("CREATE TABLE PlayListTable (vid INTEGER PRIMARY KEY AUTOINCREMENT,fid INTEGER, title TEXT, name TEXT, path TEXT,  length TEXT, duration TEXT, modifiedDate TEXT, resolution TEXT, favorite INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE HistoryTable (hid INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, name TEXT, path TEXT,  length TEXT, duration TEXT, modifiedDate TEXT, resolution TEXT, favorite INTEGER)");
        sqLiteDatabase.execSQL("CREATE TABLE AudioPlayListFolderTable (fid INTEGER PRIMARY KEY AUTOINCREMENT,folderName Text)");
        sqLiteDatabase.execSQL("CREATE TABLE AudioPlayListTable (aid INTEGER PRIMARY KEY AUTOINCREMENT, fid INTEGER, title TEXT, name TEXT, path TEXT, size TEXT, duration TEXT, modifiedDate TEXT, albumId LONG, album TEXT, artistId LONG, artist Text, favorite INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS PlayListFolderTable");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS PlayListTable");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS HistoryTable");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS AudioPlayListFolderTable");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS AudioPlayListTable");
    }

    public boolean insertPlayListFolderData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("folderName", name);
        long result = db.insert("PlayListFolderTable", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertAudioPlayListFolderData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("folderName", name);
        long result = db.insert("AudioPlayListFolderTable", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertPlayListData(int fid, String title, String name, String path, String length, String duration, String modifiedDate, String resolution, int favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fid", fid);
        contentValues.put("title", title);
        contentValues.put("name", name);
        contentValues.put("path", path);
        contentValues.put("length", length);
        contentValues.put("duration", duration);
        contentValues.put("modifiedDate", modifiedDate);
        contentValues.put("resolution", resolution);
        contentValues.put("favorite", favorite);
        long result = db.insert("PlayListTable", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertHistoryData(String title, String name, String path, String length, String duration, String modifiedDate, String resolution/*, int favorite*/) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor rawQuery = db.rawQuery("select path from HistoryTable", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    if (path.equals(rawQuery.getString(0))) {
                        int res = db.delete("HistoryTable", "path = ?", new String[]{path});
                    }
                } while (rawQuery.moveToNext());
            }
            rawQuery.close();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("name", name);
        contentValues.put("path", path);
        contentValues.put("length", length);
        contentValues.put("duration", duration);
        contentValues.put("modifiedDate", modifiedDate);
        contentValues.put("resolution", resolution);
        long result = db.insert("HistoryTable", null, contentValues);
        Log.e("result", "insertHistoryData: " + result);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertAudioPlayListData(int fid, String title, String name, String path, String length, String duration, String modifiedDate, long albumId, String album, long artistId, String artist, int favorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fid", fid);
        contentValues.put("title", title);
        contentValues.put("name", name);
        contentValues.put("path", path);
        contentValues.put("size", length);
        contentValues.put("duration", duration);
        contentValues.put("modifiedDate", modifiedDate);
        contentValues.put("albumId", albumId);
        contentValues.put("album", album);
        contentValues.put("artistId", artistId);
        contentValues.put("artist", artist);
        contentValues.put("favorite", favorite);
        long result = db.insert("AudioPlayListTable", null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public ArrayList<VideoFolderModel> getPlayListFolder() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ArrayList<VideoFolderModel> videoFolderModelList = new ArrayList();
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListFolderTable", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    int count = 0;
                    Cursor rawQuery2 = writableDatabase.rawQuery("select count(fid) from PlayListTable where fid = " + rawQuery.getInt(0), null);
                    if (rawQuery2 != null) {
                        rawQuery2.moveToFirst();
                        count = rawQuery2.getInt(0);
                        rawQuery2.close();
                    }
                    VideoFolderModel videoFolderModel = new VideoFolderModel(rawQuery.getInt(0), rawQuery.getString(1), count);
                    videoFolderModelList.add(videoFolderModel);
                } while (rawQuery.moveToNext());
            }
            rawQuery.close();
        }
        return videoFolderModelList;
    }

    public ArrayList<PlayListModel> getAudioPlayListFolder() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ArrayList<PlayListModel> playListModels = new ArrayList();
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListFolderTable", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    int count = 0;
                    Cursor rawQuery2 = writableDatabase.rawQuery("select count(fid) from AudioPlayListTable where fid = " + rawQuery.getInt(0), null);
                    if (rawQuery2 != null) {
                        rawQuery2.moveToFirst();
                        count = rawQuery2.getInt(0);
                        rawQuery2.close();
                    }
                    PlayListModel playListModel = new PlayListModel(rawQuery.getInt(0), rawQuery.getString(1), count);
                    playListModels.add(playListModel);
                } while (rawQuery.moveToNext());
            }
            rawQuery.close();
        }
        return playListModels;
    }

    public ArrayList<VideoFolderModel> getPlayListFolderName() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ArrayList<VideoFolderModel> videoFolderModelList = new ArrayList();
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListFolderTable where fid != 1 ", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    VideoFolderModel videoFolderModel = new VideoFolderModel(rawQuery.getInt(0), rawQuery.getString(1));
                    videoFolderModelList.add(videoFolderModel);
                } while (rawQuery.moveToNext());
            }
            rawQuery.close();
        }
        return videoFolderModelList;
    }

    public ArrayList<PlayListModel> getAudioPlayListFolderName() {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ArrayList<PlayListModel> playListModelArrayList = new ArrayList();
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListFolderTable where fid != 1 ", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    PlayListModel playListModel = new PlayListModel(rawQuery.getInt(0), rawQuery.getString(1));
                    playListModelArrayList.add(playListModel);
                } while (rawQuery.moveToNext());
            }
            rawQuery.close();
        }
        return playListModelArrayList;
    }

    public boolean isVideoFavorite(String folder, String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isFavorite = false;
        Cursor rawQuery = writableDatabase.rawQuery("select video.favorite from PlayListFolderTable as folder inner join PlayListTable as video where folder.fid == video.fid and folder.folderName = '" + folder + "' and video.path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    if (rawQuery.getInt(0) == 1) {
                        isFavorite = true;
                    }
                } while (rawQuery.moveToNext());
            }
        }
        rawQuery.close();
        return isFavorite;
    }

    public boolean isVideoFavorite(String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isFavorite = false;
        Cursor rawQuery = writableDatabase.rawQuery("select favorite from PlayListTable where path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    if (rawQuery.getInt(0) == 1) {
                        isFavorite = true;
                    }
                } while (rawQuery.moveToNext());
            }
        }
        rawQuery.close();
        return isFavorite;
    }

    public boolean isAudioFavorite(String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isFavorite = false;
        Cursor rawQuery = writableDatabase.rawQuery("select favorite from AudioPlayListTable where path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    if (rawQuery.getInt(0) == 1) {
                        isFavorite = true;
                    }
                } while (rawQuery.moveToNext());
            }
        }
        rawQuery.close();
        return isFavorite;
    }

    public boolean isVideoAdded(int fid, String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isAdded = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListFolderTable as folder inner join PlayListTable as video where folder.fid == video.fid and folder.fid = " + fid + " and video.path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                isAdded = true;
            }
        }
        rawQuery.close();
        return isAdded;
    }

    public boolean isSongAdded(int fid, String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isAdded = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListFolderTable as folder inner join AudioPlayListTable as song where folder.fid == song.fid and folder.fid = " + fid + " and song.path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                isAdded = true;
            }
        }
        rawQuery.close();
        return isAdded;
    }

    public boolean isVideoExistandDelete(String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isAdded = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListTable  where path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                long result = writableDatabase.delete("PlayListTable", "path = ?", new String[]{rawQuery.getString(4)});
                if (result == -1)
                    isAdded = false;
                else
                    isAdded = true;
            }
        }
        rawQuery.close();
        return isAdded;
    }

    public boolean isAudioExistandDelete(String path) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isAdded = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListTable  where path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                long result = writableDatabase.delete("AudioPlayListTable", "path = ?", new String[]{rawQuery.getString(4)});
                if (result == -1)
                    isAdded = false;
                else
                    isAdded = true;
            }
        }
        rawQuery.close();
        return isAdded;
    }

    public boolean isVideoExistandRename(String oldpath, String newpath, String title, String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListTable  where path = '" + oldpath + "'", null);
        long result = 0;
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                contentValues.put("title", title);
                contentValues.put("path", newpath);
                result = writableDatabase.update("PlayListTable", contentValues, "path = ?", new String[]{oldpath});
            }
        }
        rawQuery.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean renameHistoryVideo(String oldpath, String newpath, String title, String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select * from HistoryTable  where path = '" + oldpath + "'", null);
        long result = 0;
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                contentValues.put("title", title);
                contentValues.put("path", newpath);
                result = writableDatabase.update("HistoryTable", contentValues, "path = ?", new String[]{oldpath});
            }
        }
        rawQuery.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean isAudioExistandRename(String oldpath, String newpath, String title, String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListTable  where path = '" + oldpath + "'", null);
        long result = 0;
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", name);
                contentValues.put("title", title);
                contentValues.put("path", newpath);
                result = writableDatabase.update("AudioPlayListTable", contentValues, "path = ?", new String[]{oldpath});
            }
        }
        rawQuery.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public int getFolderId(String folder) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int fid = 0;
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListFolderTable where folderName = '" + folder + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                fid = rawQuery.getInt(0);
            }
        }
        rawQuery.close();
        return fid;
    }

    public int getAudioFolderId(String folder) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        int fid = 0;
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListFolderTable where folderName = '" + folder + "'", null);
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                fid = rawQuery.getInt(0);
            }
        }
        rawQuery.close();
        return fid;
    }

    public boolean isFolderNameExist(String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isExist = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListFolderTable where folderName = '" + name + "'", null);
        if (rawQuery != null && rawQuery.getCount() != 0) {
            rawQuery.moveToFirst();
            isExist = true;
        }
        rawQuery.close();
        return isExist;
    }

    public boolean isAudioFolderNameExist(String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isExist = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListFolderTable where folderName = '" + name + "'", null);
        if (rawQuery != null && rawQuery.getCount() != 0) {
            rawQuery.moveToFirst();
            isExist = true;
        }
        rawQuery.close();
        return isExist;
    }

    public boolean renameAudioFolder(int fid, String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("folderName", name);
        long result = writableDatabase.update("AudioPlayListFolderTable", contentValues, "fid = ?", new String[]{String.valueOf(fid)});
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean renameFolder(int fid, String name) {
        SQLiteDatabase writableDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("folderName", name);
        long result = writableDatabase.update("PlayListFolderTable", contentValues, "fid = ?", new String[]{String.valueOf(fid)});
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean removePlayListFolder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("PlayListFolderTable", "fid = ?", new String[]{String.valueOf(id)});
        long result2 = db.delete("PlayListTable", "fid = ?", new String[]{String.valueOf(id)});
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;
    }

    public boolean removePlayListVideo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("PlayListTable", "vid = ?", new String[]{String.valueOf(id)});
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean removePlayListVideo(int fid, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("PlayListTable", "fid = ? and path = ?", new String[]{String.valueOf(fid), path});
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean removeHistoryVideo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("HistoryTable", "hid = ?", new String[]{String.valueOf(id)});
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean removePlayListAudio(int fid, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("AudioPlayListTable", "fid = ? and path = ?", new String[]{String.valueOf(fid), path});
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean removeAudioPlayListFolder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete("AudioPlayListFolderTable", "fid = ?", new String[]{String.valueOf(id)});
        long result2 = db.delete("AudioPlayListTable", "fid = ?", new String[]{String.valueOf(id)});
        if (result == -1 && result2 == -1)
            return false;
        else
            return true;
    }

    public boolean isVideoExistInPlayList(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        boolean isExist = false;
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListTable where path = '" + path + "'", null);
        if (rawQuery != null) {
            if (rawQuery.getCount() > 0) {
                isExist = true;
            }
        }
        return isExist;
    }

    public boolean updateFavorite(int favorite, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select * from PlayListTable where path = '" + path + "'", null);
        long result = 0;
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("favorite", favorite);
                    result = db.update("PlayListTable", contentValues, "path = ?", new String[]{path});
                } while (rawQuery.moveToNext());
            }
        }
        rawQuery.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean updateAudioFavorite(int favorite, String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase writableDatabase = getWritableDatabase();
        Cursor rawQuery = writableDatabase.rawQuery("select * from AudioPlayListTable where path = '" + path + "'", null);
        long result = 0;
        if (rawQuery != null) {
            if (rawQuery.moveToFirst()) {
                do {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("favorite", favorite);
                    result = db.update("AudioPlayListTable", contentValues, "path = ?", new String[]{path});
                } while (rawQuery.moveToNext());
            }
        }
        rawQuery.close();
        if (result == -1)
            return false;
        else
            return true;
    }

    public void deleteAllHistoryData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from HistoryTable");
        db.close();
    }
}
