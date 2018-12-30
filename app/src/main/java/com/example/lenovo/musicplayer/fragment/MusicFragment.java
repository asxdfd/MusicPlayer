package com.example.lenovo.musicplayer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lenovo.musicplayer.R;
import com.example.lenovo.musicplayer.activity.MainActivity;
import com.example.lenovo.musicplayer.model.Music;
import com.example.lenovo.musicplayer.util.MusicListAdapter;
import com.example.lenovo.musicplayer.util.MusicUtil;
import com.nostra13.universalimageloader.utils.L;

public class MusicFragment extends Fragment implements View.OnClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private MusicListAdapter mMusicListAdapter = new MusicListAdapter();

    private MainActivity mActivity;
    private ListView mMusicListView;
    private TextView mMusicTitle;
    private TextView mMusicArtist;

    private ImageView mPreImageView;
    private ImageView mPlayImageView;
    private ImageView mNextImageView;

    private boolean isPause;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.music_list, container, false);

        mMusicListView = (ListView) view.findViewById(R.id.list_music);
        mMusicTitle = (TextView) view.findViewById(R.id.music_name);
        mMusicArtist = (TextView) view.findViewById(R.id.music_singer);
        mPreImageView = (ImageView) view.findViewById(R.id.btn_previous);
        mPlayImageView = (ImageView) view.findViewById(R.id.btn_play);
        mNextImageView = (ImageView) view.findViewById(R.id.btn_next);

        mMusicListView.setAdapter(mMusicListAdapter);
        mMusicListView.setOnItemClickListener(mMusicItemClickListener);

        mPreImageView.setOnClickListener(this);
        mPlayImageView.setOnClickListener(this);
        mNextImageView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        L.i("fragment", "onViewCreated");
        mActivity.allowBindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        isPause = false;
    }

    @Override
    public void onPause() {
        isPause = true;
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (MainActivity) activity;
    }

    public void onStop() {
        super.onStop();
        L.i("fragment", "onDestroyView");
        mActivity.allowUnbindService();
    }

    private AdapterView.OnItemClickListener mMusicItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            play(position);
        }
    };

    private void onItemPlay(int position) {
        // 将ListView列表滑动到播放的歌曲的位置，是播放的歌曲可见
        mMusicListView.smoothScrollToPosition(position);
        // 获取上次播放的歌曲的position
        int prePlayingPosition = mMusicListAdapter.getPlayingPosition();

        // 设置新的播放位置
        mMusicListAdapter.setPlayingPosition(position);

        // 如果新的播放位置不在可视区域
        // 则直接返回
        if (mMusicListView.getLastVisiblePosition() < position
                || mMusicListView.getFirstVisiblePosition() > position)
            return;

        // 如果在可视区域
        // 手动设置改item visible
        int currentItem = position - mMusicListView.getFirstVisiblePosition();
        ((ViewGroup) mMusicListView.getChildAt(currentItem)).getChildAt(0)
                .setVisibility(View.VISIBLE);
    }

    /**
     * 播放音乐item
     *
     * @param position
     */
    private void play(int position) {
        int pos = mActivity.getPlayService().play(position);
        onPlay(pos);
    }

    /**
     * 播放时，更新控制面板
     *
     * @param position
     */
    public void onPlay(int position) {
        if (MusicUtil.sMusicList.isEmpty() || position < 0)
            return;
        //设置进度条的总长度
        onItemPlay(position);

        Music music = MusicUtil.sMusicList.get(position);
        mMusicTitle.setText(music.getName());
        mMusicArtist.setText(music.getSinger());

        if (mActivity.getPlayService().isPlaying()) {
            mPlayImageView.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            mPlayImageView.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (mActivity.getPlayService().isPlaying()) {
                    mActivity.getPlayService().pause(); // 暂停
                    mPlayImageView
                            .setImageResource(android.R.drawable.ic_media_play);
                } else {
                    onPlay(mActivity.getPlayService().resume()); // 播放
                }
                break;
            case R.id.btn_next:
                mActivity.getPlayService().next(); // 下一曲
                break;
            case R.id.btn_previous:
                mActivity.getPlayService().pre(); // 上一曲
                break;
        }
    }

    public MusicListAdapter getmMusicListAdapter() {
        return mMusicListAdapter;
    }
}
