package com.example.lenovo.musicplayer.fragment;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.activity.MainActivity;
import com.example.lenovo.musicplayer.model.Music;
import com.example.lenovo.musicplayer.model.Video;
import com.example.lenovo.musicplayer.service.PlayService;
import com.example.lenovo.musicplayer.util.CustomTouchListener;
import com.example.lenovo.musicplayer.util.MusicListAdapter;
import com.example.lenovo.musicplayer.util.StorageUtil;
import com.example.lenovo.musicplayer.util.VideoListAdapter;
import com.example.lenovo.musicplayer.util.onItemClickListener;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

public class MusicFragment extends Fragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<Music> audioList = new ArrayList<>();
    boolean isPlay = false;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MusicFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static MusicFragment newInstance(int columnCount) {
        MusicFragment fragment = new MusicFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_music);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnItemTouchListener(new CustomTouchListener(view.getContext(), new onItemClickListener() {
            @Override
            public void onClick(View view, int index) {
                playAudio(index);
            }
        }));
        ((ImageView) view.findViewById(R.id.btn_previous)).setOnClickListener(this);
        ((ImageView) view.findViewById(R.id.btn_play)).setOnClickListener(this);
        ((ImageView) view.findViewById(R.id.btn_next)).setOnClickListener(this);
        BmobQuery<Music> bmobQuery = new BmobQuery<Music>();
        bmobQuery.findObjects(new FindListener<Music>() {
            @Override
            public void done(List<Music> list, BmobException e) {
                if (e == null) {
                    System.out.println(System.currentTimeMillis() + "r");
                    audioList.addAll(list);
                    recyclerView.setAdapter(new MusicListAdapter(list, getActivity()));
                } else {
                    Log.e("BMOB", e.getMessage());
                }
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void playAudio(int audioIndex) {
        //Check is service is active
        if (!((MainActivity) getActivity()).getServiceBound()) {
            //Store Serializable audioList to SharedPreferences
            StorageUtil storage = new StorageUtil(getActivity().getApplicationContext());
            storage.storeAudio((ArrayList<Music>) audioList);
            storage.storeAudioIndex(audioIndex);

            Intent playerIntent = new Intent(getActivity(), PlayService.class);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, ((MainActivity) getActivity()).getServiceConnection(), Context.BIND_AUTO_CREATE);
        } else {
            //Store the new audioIndex to SharedPreferences
            StorageUtil storage = new StorageUtil(getActivity().getApplicationContext());
            storage.storeAudioIndex(audioIndex);

            //Service is active
            //Send a broadcast to the service -> PLAY_NEW_AUDIO
            Intent broadcastIntent = new Intent("com.example.lenovo.musicplayer.PlayNewAudio");
            getActivity().sendBroadcast(broadcastIntent);
        }
        isPlay = true;
        ((ImageView) getActivity().findViewById(R.id.btn_play)).setImageResource(R.drawable.new_pause_video);
        ((TextView) getActivity().findViewById(R.id.music_name)).setText(audioList.get(audioIndex).getName());
        ((TextView) getActivity().findViewById(R.id.music_singer)).setText(audioList.get(audioIndex).getSinger());
    }

    @Override
    public void onClick(View v) {
        Intent broadcastIntent;
        switch (v.getId()) {
            case R.id.btn_previous:
                broadcastIntent = new Intent("com.example.lenovo.musicplayer.ACTION_PREVIOUS");
                getActivity().sendBroadcast(broadcastIntent);
            case R.id.btn_play:
                if (isPlay) {
                    ((ImageView) getActivity().findViewById(R.id.btn_play)).setImageResource(R.drawable.new_play_video);
                    broadcastIntent = new Intent("com.example.lenovo.musicplayer.ACTION_PLAY");
                    isPlay = false;
                } else {
                    ((ImageView) getActivity().findViewById(R.id.btn_play)).setImageResource(R.drawable.new_pause_video);
                    broadcastIntent = new Intent("com.example.lenovo.musicplayer.ACTION_PAUSE");
                    isPlay = true;
                }
                getActivity().sendBroadcast(broadcastIntent);
            case R.id.btn_next:
                broadcastIntent = new Intent("com.example.lenovo.musicplayer.ACTION_NEXT");
                getActivity().sendBroadcast(broadcastIntent);
        }
    }
}
