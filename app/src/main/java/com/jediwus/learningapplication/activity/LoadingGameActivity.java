package com.jediwus.learningapplication.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.MyVideoView;
import com.jediwus.learningapplication.myUtil.NumberController;
import com.jediwus.learningapplication.pojo.GameQueue;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class LoadingGameActivity extends BaseActivity {

    private boolean isReady = false;
    private final int FINISH = 1;

    private MyVideoView myVideoView;
    private RelativeLayout layout_ui;
    private LinearLayout layout_skip;
    private ProgressBar progressBar;


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FINISH:
                    new Handler().postDelayed(() -> {
                        layout_skip.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        isReady = true;
                    }, 2500);
                    break;
                case 666:
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_game);

        myVideoView = findViewById(R.id.video_loading_game);
        // 指定视频的URI
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chrono_trigger);
        // 设置视频URI
        myVideoView.setVideoURI(videoUri);
        // 当装载流媒体完毕的时候回调
        myVideoView.setOnPreparedListener(mediaPlayer -> myVideoView.start());
        // 网络流媒体播放结束时回调
        myVideoView.setOnCompletionListener(mediaPlayer -> myVideoView.start());
        layout_ui = findViewById(R.id.layout_loading_game_dashboard);
        layout_ui.setVisibility(View.VISIBLE);

        // 圆形加载条
        progressBar = findViewById(R.id.progress_loading_game);
        progressBar.setVisibility(View.VISIBLE);

        // 帮助
        ImageView img_help = findViewById(R.id.img_loading_game_help);
        img_help.setOnClickListener(view -> {
            String tips = "\t\t\tA.D.1000年是人类与魔族大战取得完全胜利的第四百年。\n\t\t\t千年庆典上，克罗洛和玛鲁" +
                    "不小心掉入发明家露卡创造的时空裂隙，奇迹般来到大战前夕的A.D.600年，不料魔王部下误把玛鲁当做人类公主" +
                    "掳去了魔王城！" +
                    "\n\t\t\t现在情况紧急，请你为克罗洛选择正确选项，在魔王赶到魔王城之前救出玛鲁！";
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoadingGameActivity.this);
            builder.setTitle("剧情梗概")
                    .setMessage(tips)
                    .setPositiveButton("我明白了", null)
                    .show();
        });

        // 跳过
        layout_skip = findViewById(R.id.layout_loading_game_skip);
        layout_skip.setVisibility(View.GONE);
        layout_skip.setOnClickListener(view -> {
            MediaHelper.releaseMediaPlayer();
            ActivityCollector.startOtherActivity(LoadingGameActivity.this, GameActivity.class);
            finish();
        });

        new Thread(() -> {
            // 获取词库
            List<Word> vocabularyList = LitePal.select("wordId", "word").find(Word.class);
            // 由随机算法从词库中挑选出的 50 个单词
            int[] randomNumberArray = NumberController.getRandomNumberArray(
                    0,
                    vocabularyList.size() - 1,
                    DataConfig.EXTRACT_WORDS_NUM);
            for (int i = 0; i < DataConfig.EXTRACT_WORDS_NUM; i++) {
                assert randomNumberArray != null;
                Translation translation =
                        LitePal.where("wordId = ?", vocabularyList.get(randomNumberArray[i]).getWordId() + "")
                                .find(Translation.class)
                                .get(0);
                GameActivity.gameQueueList.add(new GameQueue(
                        vocabularyList.get(randomNumberArray[i]).getWordId(),
                        vocabularyList.get(randomNumberArray[i]).getWord(),
                        translation.getWordType() + ". " + translation.getCnMeaning()));
            }
            // 打乱 游戏词汇队列 顺序
            Collections.shuffle(GameActivity.gameQueueList);

            Message message = new Message();
            message.what = FINISH;
            handler.sendMessage(message);

        }).start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        myVideoView.resume();
        if (isReady) {
            layout_skip.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            layout_skip.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        myVideoView.suspend();
    }

    @Override
    public void onBackPressed() {
        layout_ui.setVisibility(View.GONE);
        MainActivity.lastFragment = 1;
        MainActivity.needRefresh = false;
        MediaHelper.releaseMediaPlayer();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myVideoView.stopPlayback();
    }

}