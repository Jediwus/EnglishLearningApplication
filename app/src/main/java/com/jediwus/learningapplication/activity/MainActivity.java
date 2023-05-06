package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.menu.FragmentUser;
import com.jediwus.learningapplication.activity.menu.FragmentReview;
import com.jediwus.learningapplication.activity.menu.FragmentWord;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.service.NotificationService;

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
            animation.setDuration(1300);
            bottomNavigationView.startAnimation(animation);
        }

        Fragment fragmentWord = new FragmentWord();
        Fragment fragmentReview = new FragmentReview();
        Fragment fragmentUser = new FragmentUser();
        fragments = new Fragment[]{fragmentWord, fragmentReview, fragmentUser};

        switch (lastFragment) {
            case 0:
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_frag_container,
                        fragmentWord).show(fragmentWord).commit();
                break;
            case 1:
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_frag_container,
                        fragmentReview).show(fragmentReview).commit();
                break;
            case 2:
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_frag_container,
                        fragmentUser).show(fragmentUser).commit();
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

        // 设置通知栏单词
        if (DataConfig.getIsNotificationOn()) {
            if (!BaseActivity.isServiceOn(MyApplication.getContext(), NotificationService.class.getName())) {
                // 为三种选词范围类型，设置默认值
                AuxFunctionActivity.initDefaultMode();
                AuxFunctionActivity.startMyService(DataConfig.getRangeMode());
            }
        }
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
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
        builder.setTitle("精灵的挽留")
                .setMessage("小骑士要离开了吗，再玩会儿怎么样？")
                .setPositiveButton("该休息啦", (dialog, which) -> {
                    needRefresh = true;
                    ActivityCollector.finishAll();
                })
                .setNegativeButton("留下", null)
                .show();
    }


}