package com.privatebrowser.safebrowser.download.video.download.blockedAds;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AdFilterDao {
    @Query("SELECT * FROM adfilter")
    List<AdFilter> getAll();

    @Insert
    void insertAll(List<AdFilter> adFilters);

    @Query("DELETE FROM adfilter")
    void deleteAll();
}
