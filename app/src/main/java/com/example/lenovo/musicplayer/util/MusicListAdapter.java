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

import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.model.Music;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private final List<Music> mValues;
    private Context context;

    public MusicListAdapter(List<Music> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.mNameView.setText(mValues.get(position).getName());
        holder.mSingerView.setText(mValues.get(position).getSinger());
    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return mValues.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mNameView;
        public final TextView mSingerView;

        public ViewHolder(View view) {
            super(view);
            mNameView = (TextView) view.findViewById(R.id.item_name);
            mSingerView = (TextView) view.findViewById(R.id.item_singer);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
