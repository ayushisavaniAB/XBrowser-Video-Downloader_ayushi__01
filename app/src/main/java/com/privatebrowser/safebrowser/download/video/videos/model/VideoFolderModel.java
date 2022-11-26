package com.privatebrowser.safebrowser.download.video.videos.model;

import java.io.Serializable;
import java.util.ArrayList;


public class VideoFolderModel implements Serializable {

    int id;
    String name;
    String path;
    int count;
    ArrayList<VideoModel> media_data;

    public VideoFolderModel(int id,String name, String path, ArrayList<VideoModel> media_data) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.media_data = media_data;
    }

    public VideoFolderModel(int id, String name, String path, int count) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.count = count;
    }

    public VideoFolderModel(int id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public VideoFolderModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<VideoModel> getMedia_data() {
        return this.media_data;
    }

    public void setMedia_data(ArrayList<VideoModel> arrayList) {
        this.media_data = arrayList;
    }

}
