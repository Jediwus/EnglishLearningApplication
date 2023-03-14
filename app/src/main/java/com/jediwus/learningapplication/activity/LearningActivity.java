package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jediwus.learningapplication.R;

public class LearningActivity extends BaseActivity {



    public static boolean needUpdate = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);
    }
}