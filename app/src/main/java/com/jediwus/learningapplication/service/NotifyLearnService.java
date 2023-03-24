package com.jediwus.learningapplication.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.jediwus.learningapplication.database.Word;

import java.util.List;

public class NotifyLearnService extends Service {

    public static final int ALL_MODE = 0;

    public static final int STAR_MODE = 1;

    public static final int LEARN_MODE = 2;

    public static final int RANDOM_MODE = 3;

    public static int currentMode = -1;

    public static int currentIndex = 0;

    public static List<Word> needWords;

    public NotifyLearnService() {
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
