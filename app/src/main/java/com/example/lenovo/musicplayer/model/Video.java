package com.example.lenovo.musicplayer.model;

import cn.bmob.v3.BmobObject;

/**
 * File: Video.java
 * Name: 张袁峰
 * Student ID: 16301170
 * date: 2018/12/26
 */
public class Video extends BmobObject {

    private String url;
    private String thumb;
    private String title;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
