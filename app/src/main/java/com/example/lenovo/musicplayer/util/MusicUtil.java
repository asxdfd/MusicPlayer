package com.example.lenovo.musicplayer.util;

import com.example.lenovo.musicplayer.model.Music;

import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * File: MusicUtil.java
 * Name: 张袁峰
 * Student ID: 16301170
 * date: 2018/12/30
 */
public class MusicUtil {
    // 存放歌曲列表
    public static ArrayList<Music> sMusicList = new ArrayList<Music>();

    public static void initMusicList() {
        // 获取歌曲列表
        try {
            if (sMusicList.size() == 0)
                sMusicList.addAll(NetworkMusicUtil.queryMusic());
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
