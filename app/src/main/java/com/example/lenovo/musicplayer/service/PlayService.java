package com.example.lenovo.musicplayer.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.widget.Toast;

import com.example.lenovo.musicplayer.activity.MainActivity;
import com.example.lenovo.musicplayer.fragment.HomeFragment;
import com.example.lenovo.musicplayer.fragment.MusicFragment;
import com.example.lenovo.musicplayer.util.Constants;
import com.example.lenovo.musicplayer.util.MusicListAdapter;
import com.example.lenovo.musicplayer.util.MusicUtil;
import com.example.lenovo.musicplayer.util.SpUtils;
import com.nostra13.universalimageloader.utils.L;

import org.jdom2.JDOMException;

import java.io.IOException;

/**
 * File: PlayService.java
 * Name: 张袁峰
 * Student ID: 16301170
 * date: 2018/12/30
 */
public class PlayService extends Service implements
        MediaPlayer.OnCompletionListener {

    private static final String TAG =
            PlayService.class.getSimpleName();

    private SensorManager mSensorManager;

    private MediaPlayer mPlayer;
    private OnMusicEventListener mListener;
    private int mPlayingPosition; // 当前正在播放
    private PowerManager.WakeLock mWakeLock = null;//获取设备电源锁，防止锁屏后服务被停止
    private boolean isShaking;

    public class PlayBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mSensorManager.registerListener(mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        return new PlayBinder();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();
        acquireWakeLock();//获取设备电源锁
        mSensorManager = (SensorManager)
                getSystemService(Context.SENSOR_SERVICE);
        final MusicListAdapter musicListAdapter = HomeFragment.getHomeFragment().getmMusicFragment().getmMusicListAdapter();

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                MusicUtil.initMusicList();
                mPlayingPosition = (Integer)
                        SpUtils.get(PlayService.this, Constants.PLAY_POS, 0);
                Uri uri = Uri.parse(MusicUtil.sMusicList.get(
                        getPlayingPosition()).getUrl());
                mPlayer = MediaPlayer.create(PlayService.this,uri);
                mPlayer.setOnCompletionListener(PlayService.this);
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MusicListAdapter musicListAdapter = HomeFragment.getHomeFragment().getmMusicFragment().getmMusicListAdapter();
                    musicListAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    /**
     * 感应器的时间监听器
     */
    private SensorEventListener mSensorEventListener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (isShaking)
                        return;

                    if (Sensor.TYPE_ACCELEROMETER == event.sensor.getType()) {
                        float[] values = event.values;
                        /**
                         * 监听三个方向上的变化，数据变化剧烈，next()方法播放下一首歌曲
                         */
                        if (Math.abs(values[0]) > 8 && Math.abs(values[1]) > 8
                                && Math.abs(values[2]) > 8) {
                            isShaking = true;
                            next();
                            // 延迟200毫秒 防止抖动
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isShaking = false;
                                }
                            }, 200);
                        }
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };

    /**
     * 设置回调
     *
     * @param l
     */
    public void setOnMusicEventListener(OnMusicEventListener l) {
        mListener = l;
    }

    /**
     * 播放
     *
     * @param position
     *            音乐列表播放的位置
     * @return 当前播放的位置
     */
    public int play(int position) {
        L.i(TAG, "play(int position)方法");
        if (position < 0)
            position = 0;
        if (position >= MusicUtil.sMusicList.size())
            position = MusicUtil.sMusicList.size() - 1;

        try {
            mPlayer.reset();
            mPlayer.setDataSource(MusicUtil
                    .sMusicList.get(position).getUrl());
            mPlayer.prepare();

            start();
            if (mListener != null)
                mListener.onChange(position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mPlayingPosition = position;
        SpUtils.put(Constants.PLAY_POS, mPlayingPosition);
        return mPlayingPosition;
    }

    /**
     * 继续播放
     *
     * @return 当前播放的位置 默认为0
     */
    public int resume() {
        if (isPlaying())
            return -1;
        mPlayer.start();
        return mPlayingPosition;
    }

    /**
     * 暂停播放
     *
     * @return 当前播放的位置
     */
    public int pause() {
        if (!isPlaying())
            return -1;
        mPlayer.pause();
        return mPlayingPosition;
    }

    /**
     * 下一曲
     *
     * @return 当前播放的位置
     */
    public int next() {
        if (mPlayingPosition >= MusicUtil.sMusicList.size() - 1) {
            return play(0);
        }
        return play(mPlayingPosition + 1);
    }

    /**
     * 上一曲
     *
     * @return 当前播放的位置
     */
    public int pre() {
        if (mPlayingPosition <= 0) {
            return play(MusicUtil.sMusicList.size() - 1);
        }
        return play(mPlayingPosition - 1);
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return null != mPlayer && mPlayer.isPlaying();
    }

    /**
     * 获取正在播放的歌曲在歌曲列表的位置
     *
     * @return
     */
    public int getPlayingPosition() {
        return mPlayingPosition;
    }

    /**
     * 获取当前正在播放音乐的总时长
     *
     * @return
     */
    public int getDuration() {
        if (!isPlaying())
            return 0;
        return mPlayer.getDuration();
    }

    /**
     * 拖放到指定位置进行播放
     *
     * @param msec
     */
    public void seek(int msec) {
        if (!isPlaying())
            return;
        mPlayer.seekTo(msec);
    }

    /**
     * 开始播放
     */
    private void start() {
        mPlayer.start();
    }

    /**
     * 音乐播放完毕 自动下一曲
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        L.i("play service", "unbind");
        pause();
        mSensorManager.unregisterListener(mSensorEventListener);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        resume();
        if (mListener != null)
            mListener.onChange(mPlayingPosition);
    }

    @Override
    public void onDestroy() {
        L.i(TAG, "PlayService.java的onDestroy()方法调用");
        release();
        stopForeground(true);
        mSensorManager.unregisterListener(mSensorEventListener);
        super.onDestroy();
    }

    /**
     * 服务销毁时，释放各种控件
     */
    private void release() {
        //释放设备电源锁
        releaseWakeLock();
        if (mPlayer != null)
            mPlayer.release();
        mPlayer = null;
    }

    // 申请设备电源锁
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock() {
        L.i(TAG, "正在申请电源锁");
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, "");
            if (null != mWakeLock) {
                mWakeLock.acquire();
                L.i(TAG, "电源锁申请成功");
            }
        }
    }

    // 释放设备电源锁
    private void releaseWakeLock() {
        L.i(TAG, "正在释放电源锁");
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
            L.i(TAG, "电源锁释放成功");
        }
    }

    /**
     * 音乐播放回调接口
     */
    public interface OnMusicEventListener {
        public void onPublish(int percent);

        public void onChange(int position);
    }
}