package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jediwus.learningapplication.R;

public class AlarmActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
    }


    public static void startAlarm(int hour, int minute, boolean isRepeat, boolean isTip) {

    }
}