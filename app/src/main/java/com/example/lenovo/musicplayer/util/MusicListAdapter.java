package com.example.lenovo.musicplayer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.musicplayer.MusicPlayerApplication;
import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.model.Music;

import org.jsoup.Connection;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    private int mPlayingPosition;

    public void setPlayingPosition(int position) {
        mPlayingPosition = position;
    }

    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    @Override
    public int getCount() {
        return MusicUtil.sMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return MusicUtil.sMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;

        if(convertView == null) {
            convertView = View.inflate(MusicPlayerApplication.sContext, R.layout.music_item, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.item_img);
            holder.title = (TextView) convertView.findViewById(R.id.item_name);
            holder.artist = (TextView) convertView.findViewById(R.id.item_singer);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageResource(R.drawable.ic_item_music);
        holder.title.setText(MusicUtil.sMusicList.get(position).getName());
        holder.artist.setText(MusicUtil.sMusicList.get(position).getSinger());
        holder.url = MusicUtil.sMusicList.get(position).getUrl();

        return convertView;
    }

    static class ViewHolder {
        ImageView image;
        TextView title;
        TextView artist;
        String url;
    }
}
