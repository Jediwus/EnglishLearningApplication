package com.jediwus.learningapplication.myUtil;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.jediwus.learningapplication.config.ExternalData;

import java.io.IOException;

public class MediaHelper {

    // 英式发音
    public static final int ENGLISH_VOICE = 1;
    // 美式发音
    public static final int AMERICA_VOICE = 0;

    // 默认
    public static final int DEFAULT_VOICE = ENGLISH_VOICE;

    public static MediaPlayer myMediaPlayer;

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
        }
        myMediaPlayer = new MediaPlayer();

        try {
            AssetFileDescriptor fileDescriptor = MyApplication.getContext().getResources().openRawResourceFd(sourceAddress);
            myMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
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

    public static void playLocalSourceRepeat(final int sourceAddress) {
        // 初始化资源
        if (myMediaPlayer != null) {
            releaseMediaPlayer();
        }
        myMediaPlayer = new MediaPlayer();

        try {
            AssetFileDescriptor fileDescriptor = MyApplication.getContext().getResources().openRawResourceFd(sourceAddress);
            myMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
            myMediaPlayer.prepareAsync();
            myMediaPlayer.setOnPreparedListener(mediaPlayer -> myMediaPlayer.start());
            myMediaPlayer.setOnCompletionListener(mediaPlayer -> playLocalSourceRepeat(sourceAddress));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void releaseMediaPlayer() {
        if (myMediaPlayer != null) {
            if (myMediaPlayer.isPlaying()) {
                myMediaPlayer.stop();
            }
            myMediaPlayer.release();
            myMediaPlayer = null;
        }
    }

}
