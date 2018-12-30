package com.example.lenovo.musicplayer.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * File: Music.java
 * Name: 张袁峰
 * Student ID: 16301170
 * date: 2018/12/26
 */
public class Music {
    private String name;
    private String singer;
    private String url;

    public String getSinger() {
        return singer;
    }

    public void setSinger(String artist) {
        this.singer = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String musicName) {
        this.name = musicName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
