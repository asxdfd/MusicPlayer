package com.example.lenovo.musicplayer.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.model.Video;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class VideoListAdapter extends BaseAdapter {

    private final List<Video> mValues;
    Context context;

    public VideoListAdapter(List<Video> items, Context context) {
        this.mValues = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            viewHolder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(context);
            convertView = mInflater.inflate(R.layout.video_item, null);
            viewHolder.jcVideoPlayer = (JCVideoPlayer) convertView.findViewById(R.id.videoplayer);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Video video = mValues.get(position);
        viewHolder.jcVideoPlayer.setUp(
                video.getUrl(),
                video.getThumb(),
                video.getTitle());
        return convertView;
    }

    class ViewHolder {
        JCVideoPlayer jcVideoPlayer;
    }
}
