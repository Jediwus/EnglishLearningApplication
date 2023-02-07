package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jediwus.learningapplication.R;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private LinearLayout linearLayout;

    private Fragment fragWord, fragReview, fragMe;

    private Fragment[] fragments;

    //用于记录上个选择的Fragment
    public static int lastFragment;

    public static boolean needRefresh = true;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        initView();

        if (needRefresh) {
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
            );
            animation.setDuration(2000);
            //bottomNavigationView.startAnimation(animation);
        }
        initFragment();

    }

    private void initFragment() {

    }

    private void initView() {
        bottomNavigationView = findViewById(R.id.bottom_nav);
        linearLayout = findViewById(R.id.linear_frag_container);
    }
}