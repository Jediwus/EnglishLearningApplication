package com.jediwus.learningapplication.activity;

import static com.jediwus.learningapplication.config.DataConfig.defaultQuickNumber;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MyApplication;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class QuickActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "QuickActivity";

    private MediaPlayer mediaPlayer;

    private boolean isPause = true;

    private boolean isFirst = true;

    private int pointer = 0;
    // 单词列表
    public static List<Word> wordList = new ArrayList<>();

    private ObjectAnimator objectAnimator;
    private CardView cardView_circle;
    private LinearLayout layout_word;
    private AutofitTextView tv_word;
    private TextView tv_button;
    private TextView tv_number;
    private TextView tv_mean;
    private TextView tv_phone;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick);

        ImageView imgHelp = findViewById(R.id.img_quick_help);
        imgHelp.setOnClickListener(this);
        cardView_circle = findViewById(R.id.card_quick_circle);
        cardView_circle.setOnClickListener(this);
        RelativeLayout layout_home = findViewById(R.id.layout_quick_home);
        layout_home.setOnClickListener(this);
        RelativeLayout layout_pause = findViewById(R.id.layout_quick_pause);
        layout_pause.setOnClickListener(this);
        layout_word = findViewById(R.id.layout_quick_word);
        layout_word.setOnClickListener(this);
        tv_word = findViewById(R.id.text_ls_word);
        tv_phone = findViewById(R.id.text_ls_phone);
        tv_mean = findViewById(R.id.text_ls_mean);
        tv_number = findViewById(R.id.text_quick_top);
        tv_number.setText(pointer + " / " + wordList.size());
        tv_button = findViewById(R.id.text_quick_pause);
        // 进场动画效果
        explosionAnimation();
        // 卡片旋转动画
        initAnimation();
    }

    /**
     * 卡片旋转动画
     */
    private void initAnimation() {
        objectAnimator = ObjectAnimator.ofFloat(cardView_circle, "rotation", 0.0f, 360.0f);
        objectAnimator.setDuration(18000);
        // 无限循环
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        // 匀速
        objectAnimator.setInterpolator(new LinearInterpolator());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 返回主页按钮
            case R.id.layout_quick_home:
                onBackPressed();
                break;
            // 开始与暂停
            case R.id.layout_quick_pause:
                if (isFirst) {
                    isFirst = false;
                    isPause = false;
                    tv_button.setText("暂 停");
                    playWordPronunciation();
                    // 开始动画
                    objectAnimator.start();
                } else if (isPause) {
                    isPause = false;
                    // 重启动画
                    objectAnimator.resume();
                    tv_button.setText("暂 停");
                    if (pointer >= wordList.size() - 1) {
                        pointer = wordList.size() - 1;
                    }
                    playWordPronunciation();
                } else {
                    isPause = true;
                    // 暂停动画
                    objectAnimator.pause();
                    tv_button.setText("继 续");
                }
                break;

            case R.id.img_quick_help:
                String tips = "\t\t\t仿照随身听的复古设计，点击开始后，系统将每隔1.5s自动播放英语发音，点击卡片" +
                        "可进行显示和隐藏单词，Go ahead！去练习你的听力吧！\n\t\t\t请确保网络连接通畅。系统默认" +
                        "一局提供" + defaultQuickNumber + "个词，可在『词书计划』调整。";
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(QuickActivity.this);
                builder.setTitle("帮助")
                        .setMessage(tips)
                        .setPositiveButton("确定", null)
                        .show();
                break;

            case R.id.layout_quick_word:
                cardView_circle.setVisibility(View.VISIBLE);
                layout_word.setVisibility(View.GONE);
                break;

            case R.id.card_quick_circle:
                cardView_circle.setVisibility(View.GONE);
                layout_word.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

    /**
     * 显示单词数目
     */
    @SuppressLint("SetTextI18n")
    private void showWordCount() {
        tv_number.setText((pointer + 1) + " / " + wordList.size());
        tv_word.setText(wordList.get(pointer).getWord());
        List<Translation> translationList =
                LitePal.where("wordId = ?", wordList.get(pointer).getWordId() + "")
                        .find(Translation.class);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < translationList.size(); i++) {
            if (i != (translationList.size() - 1)) {
                stringBuilder.append(translationList.get(i).getWordType())
                        .append(". ")
                        .append(translationList.get(i).getCnMeaning())
                        .append("\n");
            } else {
                stringBuilder.append(translationList.get(i).getWordType())
                        .append(". ")
                        .append(translationList.get(i).getCnMeaning());
            }
        }
        Log.d(TAG, "showWordCount: 当前单词----" + wordList.get(pointer).getWord());
        Log.d(TAG, "showWordCount: 单词发音----" + wordList.get(pointer).getUkPhone());
        tv_phone.setText(wordList.get(pointer).getUkPhone());
        tv_mean.setText(stringBuilder.toString());
    }

    /**
     * 播放单词发音
     */
    private void playWordPronunciation() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(ExternalData.YOU_DAO_VOICE_US + wordList.get(pointer).getWord());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                if (!isPause) {
                    mediaPlayer.start();
                    showWordCount();
                }
            });

            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                // 若不是最后一个单词，延迟1.5s后播放下一个
                if (pointer != wordList.size() - 1) {
                    if (!isPause) {
                        new Handler().postDelayed(() -> {
                            pointer++;
                            playWordPronunciation();
                        }, 1500);
                    }
                } else {
                    new Handler().postDelayed(() -> {
                        finish();
                        Toast.makeText(QuickActivity.this, "单词播放完啦", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuickActivity.this, DisplayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DisplayActivity.DISPLAY_TYPE, DisplayActivity.TYPE_QUICK);
                        startActivity(intent);
                    }, 1500);
                }
            });

            mediaPlayer.setOnErrorListener((mediaPlayer, what, extra) -> {
                Toast.makeText(MyApplication.getContext(), "发生了点小错误，请检查设备互联网设置", Toast.LENGTH_SHORT).show();
                onBackPressed();
                return true;
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isPause = true;
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}