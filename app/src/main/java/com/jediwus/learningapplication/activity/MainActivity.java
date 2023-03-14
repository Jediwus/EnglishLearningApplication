package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.fragment.FragmentMe;
import com.jediwus.learningapplication.activity.fragment.FragmentReview;
import com.jediwus.learningapplication.activity.fragment.FragmentWord;
import com.jediwus.learningapplication.myUtil.ActivityCollector;

public class MainActivity extends BaseActivity {

    private Fragment[] fragments;

    //用于记录上个选择的Fragment
    public static int lastFragment;

    public static boolean needRefresh = true;

    private static final String TAG = "MainActivity";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: 进入主页面");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        // 导航栏上升动画
        if (needRefresh) {
            TranslateAnimation animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
            );
            animation.setDuration(1000);
            bottomNavigationView.startAnimation(animation);
        }

        Fragment fragWord = new FragmentWord();
        Fragment fragReview = new FragmentReview();
        Fragment fragMe = new FragmentMe();
        fragments = new Fragment[]{fragWord, fragReview, fragMe};

        switch (lastFragment) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_frag_container,
                        fragWord).show(fragWord).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_frag_container,
                        fragReview).show(fragReview).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_frag_container,
                        fragMe).show(fragMe).commit();
                break;
            default:
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bnavigation_word:
                    if (lastFragment != 0) {
                        switchFragment(lastFragment, 0);
                        lastFragment = 0;
                    }
                    return true;
                case R.id.bnavigation_review:
                    if (lastFragment != 1) {
                        switchFragment(lastFragment, 1);
                        lastFragment = 1;
                    }
                    return true;
                case R.id.bnavigation_me:
                    if (lastFragment != 2) {
                        switchFragment(lastFragment, 2);
                        lastFragment = 2;
                    }
                    return true;
            }
            return true;
        });
    }

    private void switchFragment(int lastIndex, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //隐藏上个Fragment
        transaction.hide(fragments[lastIndex]);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.linear_frag_container, fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("精灵的挽留")
                .setMessage("\t\t作为词星骑士的你，难道今天就到此为止了吗？")
                .setPositiveButton("该休息啦", (dialog, which) -> {
                    needRefresh = true;
                    ActivityCollector.finishAll();
                })
                .setNegativeButton("再瞥一眼", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}