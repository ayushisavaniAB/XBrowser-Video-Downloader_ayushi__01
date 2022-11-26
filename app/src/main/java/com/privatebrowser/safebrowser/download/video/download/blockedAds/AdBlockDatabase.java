package com.privatebrowser.safebrowser.download.video.download.blockedAds;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {AdFilter.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AdBlockDatabase extends RoomDatabase {
    public abstract AdFilterDao adFilterDao();
}
