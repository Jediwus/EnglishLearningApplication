package com.jediwus.learningapplication.myUtil;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import com.jediwus.learningapplication.config.ExternalData;

import java.io.IOException;

public class MediaHelper {
    private static final String TAG = "MediaHelper";
    // 英式发音
    public static final int ENGLISH_VOICE = 1;
    // 美式发音
    public static final int AMERICA_VOICE = 0;
    // 默认发音——英式
    public static final int DEFAULT_VOICE = ENGLISH_VOICE;

    // MediaPlayer对象
    public static MediaPlayer myMediaPlayer;

    // 是否暂停
    private static boolean isPause = false;

    public static void play(int type, String wordName) {
        // 初始化资源
        if (myMediaPlayer != null) {
            releaseMediaPlayer();
        }
        myMediaPlayer = new MediaPlayer();

        try {
            if (type == ENGLISH_VOICE) {
                myMediaPlayer.setDataSource(ExternalData.YOU_DAO_VOICE_UK + wordName);
            } else if (type == AMERICA_VOICE) {
                myMediaPlayer.setDataSource(ExternalData.YOU_DAO_VOICE_US + wordName);
            }
            myMediaPlayer.prepareAsync();
            myMediaPlayer.setOnPreparedListener(mediaPlayer -> myMediaPlayer.start());
            myMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if (myMediaPlayer != null) {
                    myMediaPlayer.release();
                    myMediaPlayer = null;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void play(String wordName) {
        play(DEFAULT_VOICE, wordName);
    }

    public static void playInternetSource(String address) {
        // 初始化资源
        if (myMediaPlayer != null) {
            releaseMediaPlayer();
        }
        myMediaPlayer = new MediaPlayer();

        try {
            myMediaPlayer.setDataSource(address);
            myMediaPlayer.prepareAsync();
            myMediaPlayer.setOnPreparedListener(mediaPlayer -> myMediaPlayer.start());
            myMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if (myMediaPlayer != null) {
                    myMediaPlayer.release();
                    myMediaPlayer = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playLocalSource(int sourceAddress) {
        // 初始化资源
        if (myMediaPlayer != null) {
            releaseMediaPlayer();
            Log.d(TAG, "playLocalSource: 一次性播放");
        }
        myMediaPlayer = new MediaPlayer();
        Log.d(TAG, "playLocalSourceLoop: myMediaPlayer 对象已创建");
        try {
            AssetFileDescriptor fileDescriptor = MyApplication.getContext().getResources().openRawResourceFd(sourceAddress);
            myMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            myMediaPlayer.prepareAsync();
            myMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                myMediaPlayer.start();
                Log.d(TAG, "playLocalSource: 播放媒体");
            });
            myMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if (myMediaPlayer != null) {
                    myMediaPlayer.reset();
                    myMediaPlayer.release();
                    myMediaPlayer = null;
                    Log.d(TAG, "playLocalSource: 媒体播放完，释放资源");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playLocalSourceLoop(final int sourceAddress) {
        // 初始化资源
        if (myMediaPlayer != null) {
            releaseMediaPlayer();
            Log.d(TAG, "playLocalSourceLoop: 清空之前的资源");
        }
        myMediaPlayer = new MediaPlayer();
        Log.d(TAG, "playLocalSourceLoop: myMediaPlayer 对象已创建,");
        try {
            AssetFileDescriptor fileDescriptor = MyApplication.getContext().getResources().openRawResourceFd(sourceAddress);
            myMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            myMediaPlayer.prepareAsync();
            myMediaPlayer.setOnPreparedListener(mediaPlayer -> {
                boolean b = myMediaPlayer != null;
                Log.d(TAG, "playLocalSourceLoop: 播放媒体，媒体不为空吗——" + b);
                myMediaPlayer.start();
            });
            myMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                Log.d(TAG, "playLocalSourceLoop: 媒体播放完，再次播放");
                playLocalSourceLoop(sourceAddress);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这样做是将 MediaPlayer 重置为未初始化状态
     */
    public static void releaseMediaPlayer() {
        if (myMediaPlayer != null) {
            if (myMediaPlayer.isPlaying()) {
                myMediaPlayer.stop();
            }
            myMediaPlayer.reset();
            myMediaPlayer.release();
            myMediaPlayer = null;
        } else {
            Log.d(TAG, "releaseMediaPlayer: 没找到媒体文件，可能是本来就没有，不用释放资源");
        }
    }

    /**
     * 暂停播放
     */
    public static void pauseMediaPlayer() {
        if (myMediaPlayer != null && myMediaPlayer.isPlaying() && !isPause) {
            //暂停播放
            myMediaPlayer.pause();
            isPause = true;
        } else {
            Log.d(TAG, "pauseMediaPlayer: 没找到媒体文件");
        }
    }

    /**
     * 继续播放
     */
    public static void resumeMediaPlayer() {
        if (myMediaPlayer != null && isPause) {
            // 继续播放
            myMediaPlayer.start();
            isPause = false;
        } else {
            Log.d(TAG, "resumeMediaPlayer: 没找到媒体文件");
        }
    }

}
