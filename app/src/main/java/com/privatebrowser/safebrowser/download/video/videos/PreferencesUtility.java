package com.privatebrowser.safebrowser.download.video.videos;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtility {
    private static final String TOGGLE_ALBUM_GRID = "TOGGLE_ALBUM_GRID" ;
    private static final String BACKGROUND_AUDIO = "BACKGROUND_AUDIO" ;
    private static final String SCEENORIENTATITION = "screen_orientation";
    private static final String KEY_SORT = "key_sort";
    private static final String KEY_VIDEO_VIEW = "key_video_view";
    private static final String KEY_VIDEO_FOLDER_VIEW = "key_video_folder_view";
    private static PreferencesUtility sInstance;
    private static SharedPreferences mPreferences;

    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }
    public boolean isAlbumsInGrid() {
        return mPreferences.getBoolean(TOGGLE_ALBUM_GRID, true);
    }

    public void setAlbumsInGrid(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(TOGGLE_ALBUM_GRID, b);
        editor.apply();
    }

    public String getSortingOrder() {
        return mPreferences.getString(KEY_SORT,"date_asc");
    }

    public void setSortingOrder(final String sort) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_SORT,sort);
        editor.apply();
    }

    public String getVideoViewType() {
        return mPreferences.getString(KEY_VIDEO_VIEW,"List");
    }

    public void setVideoViewType(final String view) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_VIDEO_VIEW,view);
        editor.apply();
    }

    public String getVideoFolderViewType() {
        return mPreferences.getString(KEY_VIDEO_FOLDER_VIEW,"Grid");
    }

    public void setVideoFolderViewType(final String view) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(KEY_VIDEO_FOLDER_VIEW,view);
        editor.apply();
    }

    public boolean isAllowBackgroundAudio() {
        return mPreferences.getBoolean(BACKGROUND_AUDIO, false);
    }

    public void setAllowBackgroundAudio(final boolean b) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(BACKGROUND_AUDIO, b);
        editor.apply();
    }
    public  int getScreenOrientation() {
        return mPreferences.getInt(SCEENORIENTATITION, 10);
    }

    public void setScreenOrientation(final int value) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(SCEENORIENTATITION, value);
        editor.apply();
    }

}
