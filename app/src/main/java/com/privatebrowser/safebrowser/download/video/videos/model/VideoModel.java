package com.privatebrowser.safebrowser.download.video.videos.model;

import java.io.Serializable;

public class VideoModel implements Serializable {

    int id;
    String path;
    String title;
    String name;
    String folder;
    long duration;
    String length;
    String modifiedDate;
    String resolution;
    int favorite;
//    public int videoLastPlayPosition = 0;

    public VideoModel() {
        this.name = "";
        this.path = "";
        this.duration =0;
    }

    public VideoModel(int id, String path, String title, String name, String folder, long duration, String length, String modifiedDate, String resolution) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.name = name;
        this.folder = folder;
        this.duration = duration;
        this.length = length;
        this.modifiedDate = modifiedDate;
        this.resolution = resolution;
    }

    public VideoModel(int id, String path, String title, String name, String folder, long duration, String length, String modifiedDate, String resolution, int favorite) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.title = title;
        this.folder = folder;
        this.duration = duration;
        this.length = length;
        this.modifiedDate = modifiedDate;
        this.resolution = resolution;
        this.favorite = favorite;
    }

    public VideoModel(int id, String path, String title, String name, long duration, String length, String modifiedDate, String resolution, int favorite) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.length = length;
        this.modifiedDate = modifiedDate;
        this.resolution = resolution;
        this.favorite = favorite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }



//    public int getVideoLastPlayPosition() {
//        return videoLastPlayPosition;
//    }
//
//    public void setVideoLastPlayPosition(int videoLastPlayPosition) {
//        this.videoLastPlayPosition = videoLastPlayPosition;
//    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }
}
