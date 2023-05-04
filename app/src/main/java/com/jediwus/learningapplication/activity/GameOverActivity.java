package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.MediaHelper;

public class GameOverActivity extends BaseActivity {
    // 是否静音
    private boolean isMuted = false;

    // 游戏输赢
    public static final String LOSE_OR_WIN = "gameResult";
    public static final int GAME_WIN = 1;
    public static final int GAME_LOSE = -1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // 播放背景音乐
        MediaHelper.playLocalSourceLoop(R.raw.game_over);

        TextView tv_logo = findViewById(R.id.text_game_over_logo);

        ImageView img_exit = findViewById(R.id.img_game_over_exit);
        img_exit.setOnClickListener(view -> onBackPressed());

        ImageView img_bgm = findViewById(R.id.img_game_over_bgm);
        img_bgm.setOnClickListener(view -> {
            if (!isMuted) {
                Glide.with(this)
                        .load(R.drawable.icon_music_mute)
                        .into(img_bgm);
                MediaHelper.pauseMediaPlayer();
                isMuted = true;
            } else {
                Glide.with(this)
                        .load(R.drawable.icon_music)
                        .into(img_bgm);
                MediaHelper.resumeMediaPlayer();
                isMuted = false;
            }
        });

        MaterialButton btn_again = findViewById(R.id.btn_game_over_option1);
        btn_again.setOnClickListener(view -> {
            MediaHelper.releaseMediaPlayer();
            GameActivity.gameQueueList.clear();
            ActivityCollector.startOtherActivity(GameOverActivity.this, LoadingGameActivity.class);
            finish();
        });

        MaterialButton btn_settle = findViewById(R.id.btn_game_over_option2);
        btn_settle.setOnClickListener(view -> {
            MediaHelper.releaseMediaPlayer();
            Intent intent = new Intent(GameOverActivity.this, DisplayActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(DisplayActivity.DISPLAY_TYPE, DisplayActivity.TYPE_GAME);
            startActivity(intent);
            finish();
        });

        int gameResult = getIntent().getIntExtra(LOSE_OR_WIN, -1);
        if (gameResult == GAME_WIN) {
            tv_logo.setText("YOU WIN!");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ActivityCollector.startOtherActivity(GameOverActivity.this, MainActivity.class);
        MediaHelper.releaseMediaPlayer();
    }
}