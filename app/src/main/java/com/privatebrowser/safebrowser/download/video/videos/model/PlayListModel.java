package com.privatebrowser.safebrowser.download.video.videos.model;

import java.io.Serializable;


public class PlayListModel implements Serializable {

    int id;
    String name;
    int count;

    public PlayListModel(int id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public PlayListModel(int id, String name) {
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
