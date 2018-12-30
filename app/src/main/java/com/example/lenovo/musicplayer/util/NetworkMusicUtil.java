package com.example.lenovo.musicplayer.util;

import android.database.Cursor;
import android.provider.MediaStore;

import com.example.lenovo.musicplayer.MusicPlayerApplication;
import com.example.lenovo.musicplayer.model.Music;

import net.sf.json.JSON;
import net.sf.json.JSONArray;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * File: NetworkMusicUtil.java
 * Name: 张袁峰
 * Student ID: 16301170
 * date: 2018/12/30
 */
public class NetworkMusicUtil {

    public static String queryMusicById(int musicId) {
        String result = null;
        Cursor cursor = MusicPlayerApplication.sContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA},
                MediaStore.Audio.Media._ID + "=?",
                new String[]{String.valueOf(musicId)}, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
            result = cursor.getString(0);
            break;
        }

        cursor.close();
        return result;
    }

    public static List<Music> queryMusic() throws JDOMException, IOException {
        List<Music> results = new ArrayList<Music>();

        // id title singer data time image
        results = jsonToMusic();

        return results;
    }

    public static List<Music> jsonToMusic() {
        // Create a NewsFeed object
        List<Music> list = new ArrayList<>();
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&format=json&method=baidu.ting.billboard.billList&type=1");
            URLConnection uc = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"UTF-8"));
            String inputLine = null;
            while ( (inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONArray jsonArray = JSONArray.fromObject("[" + json.toString() + "]");
        jsonArray = JSONArray.fromObject(jsonArray.getJSONObject(0).getJSONArray("song_list"));
        for (int i = 0; i < jsonArray.size(); i++) {
            String title = jsonArray.getJSONObject(i).getString("title");
            String artist = jsonArray.getJSONObject(i).getString("artist_name");
            String songid = jsonArray.getJSONObject(i).getString("song_id");
            json.setLength(0);
            json = new StringBuilder();
            try {
                URL urlObject = new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?from=webapp_music&format=json&method=baidu.ting.song.play&songid=" + songid);
                URLConnection uc = urlObject.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
                String inputLine = null;
                while ((inputLine = in.readLine()) != null) {
                    json.append(inputLine);
                }
                in.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONArray array = JSONArray.fromObject("[" + json.toString() + "]");
            String a = array.getJSONObject(0).getString("bitrate");
            array = JSONArray.fromObject("[" + a + "]");
            String url = array.getJSONObject(0).getString("file_link");

            // Set Music attribute
            Music music = new Music();
            music.setName(title);
            music.setSinger(artist);
            music.setUrl(url);
            list.add(music);
        }

        return list;
    }

}
