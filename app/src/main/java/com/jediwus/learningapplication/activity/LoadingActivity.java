package com.jediwus.learningapplication.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.myUtil.LearningController;
import com.jediwus.learningapplication.myUtil.NumberController;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.List;

public class LoadingActivity extends BaseActivity {

    private ProgressBar progressBar;

    private TextView textViewTips;

    private Handler mHandler;

    private Runnable runnable;

    int progressStatus = 0;

    private final String[] messages = {
            "做题时可别经常偷看答案哦",
            "感觉不确定？我会给出例句和发音的",
            "每日新学完单词都会进行趁热打铁哦",
            "趁热打铁，既有新铁，也有老铁",
            "冲冲冲！",
            "记得用心去记忆",
            "干就完了！"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        explosionAnimation();

        progressBar = findViewById(R.id.progress_wait);

        textViewTips = findViewById(R.id.progress_tips);
        textViewTips.setText(messages[NumberController.getRandomNumber(0, 6)]);

        ImageView img_loading_1 = findViewById(R.id.img_loading_1);
        Glide.with(this)
                .load(R.drawable.gif_dogs)
                .into(img_loading_1);

        ImageView img_loading_2 = findViewById(R.id.img_loading_2);
        Glide.with(this)
                .load(R.drawable.gif_hikari)
                .into(img_loading_2);

        // 子线程初始化数据
        Thread thread = new Thread(() -> {
            List<UserPreference> userConfigs = LitePal
                    .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                    .find(UserPreference.class);
            LearningController.setWordsNeededToLearn(userConfigs.get(0).getLastStartTime());
            LearningController.setWordsNeededToReview();
            LearningController.wordNeedReciteNumber = LearningController.wordsNeedToReviewList.size();
            TimeController.todayDate = TimeController.getCurrentDateStamp();
            LearningActivity.flagNeedRefresh = true;
            LearningActivity.lastWordId = -1;
            LearningActivity.lastWord = "";
            LearningActivity.lastWordMeaning = "";
            UserPreference userPreference = new UserPreference();
            userPreference.setLastStartTime(TimeController.getCurrentTimeStamp());
            userPreference.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");
        });
        // 开启子线程
        thread.start();
        // 等待子线程结束
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progressStatus++);
                if (progressStatus == 50) {
                    textViewTips.setText(messages[NumberController.getRandomNumber(0, 6)]);
                }
                if (progressStatus == 100) {
                    // 停止计时
                    stopTime();
                    Intent mIntent = new Intent(LoadingActivity.this, LearningActivity.class);
                    startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(LoadingActivity.this).toBundle());
                    // 结束该活动
                    finish();
                }
                // 每隔 20ms 循环执行 run 方法
                mHandler.postDelayed(this, 50);
            }

        };
        mHandler = new Handler();
        mHandler.postDelayed(runnable, 120);
    }

    // 停止计时
    private void stopTime() {
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onBackPressed() {
        // TODO: 啥也不做
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(runnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(runnable);
    }
}