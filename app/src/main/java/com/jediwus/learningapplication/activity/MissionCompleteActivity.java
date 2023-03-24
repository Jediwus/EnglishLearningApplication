package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.media.audiofx.BassBoost;
import android.os.Bundle;

import com.jediwus.learningapplication.R;

public class MissionCompleteActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_complete);
    }
}