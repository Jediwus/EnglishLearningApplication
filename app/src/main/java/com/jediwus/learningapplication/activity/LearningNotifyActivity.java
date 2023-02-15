package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jediwus.learningapplication.R;

public class LearningNotifyActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_notify);
    }

    public static void checkIsAvailable() {
    }

    public static void startService(int mode) {
    }
}