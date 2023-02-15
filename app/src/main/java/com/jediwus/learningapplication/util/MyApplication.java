package com.jediwus.learningapplication.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.jediwus.learningapplication.config.ConfigData;

import org.litepal.LitePal;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @SuppressLint("VisibleForTests")
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePal.initialize(this);
        Glide.init(this, new GlideBuilder());

//        // 夜间模式的判断
//        if (ConfigData.getIsNight()) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }

    }

    public static Context getContext() {
        return context;
    }
}
