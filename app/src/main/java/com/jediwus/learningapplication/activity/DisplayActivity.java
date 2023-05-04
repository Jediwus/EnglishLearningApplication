package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.DisplayAdapter;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.pojo.ItemDisplay;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends BaseActivity {

    private static final String TAG = "DisplayActivity";

    public final int FINISH = 0;

    public static final String DISPLAY_TYPE = "displayType";
    public static final int TYPE_MATCHING = 1;
    public static final int TYPE_QUICK = 2;
    public static final int TYPE_GAME = 3;

    private final List<ItemDisplay> itemDisplayList = new ArrayList<>();
    private final List<Word> wordList = new ArrayList<>();

    private DisplayAdapter displayAdapter;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FINISH:
                    displayAdapter.notifyDataSetChanged();
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
        setContentView(R.layout.activity_display);

        RecyclerView recyclerView = findViewById(R.id.recycler_display);
        MaterialButton btn_home = findViewById(R.id.btn_display_Home);
        btn_home.setOnClickListener(view -> onBackPressed());

        new Thread(() -> {
            // 检测 intent 传进来的 type类型
            typeDetection();
            // 绑定单词数据到 展示List 中
            dataBinding();

            Message message = new Message();
            message.what = FINISH;
            handler.sendMessage(message);
        }).start();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        displayAdapter = new DisplayAdapter(itemDisplayList);
        recyclerView.setAdapter(displayAdapter);

    }

    /**
     * 绑定单词数据到 展示List 中
     */
    private void dataBinding() {
        itemDisplayList.clear();
        for (Word word : wordList) {
            List<Translation> translationList = LitePal.where("wordId = ?", word.getWordId() + "").find(Translation.class);
            StringBuilder stringBuilder = new StringBuilder();
            for (Translation translation : translationList) {
                stringBuilder.append(translation.getWordType())
                        .append(". ")
                        .append(translation.getCnMeaning())
                        .append(" ");
            }
            if (word.getIsCollected() == 1) {
                itemDisplayList.add(new ItemDisplay(word.getWordId(), word.getWord(), stringBuilder.toString(), true));
            } else {
                itemDisplayList.add(new ItemDisplay(word.getWordId(), word.getWord(), stringBuilder.toString(), false));
            }

        }
    }

    /**
     * 检测 intent 传进来的 type类型
     */
    private void typeDetection() {
        wordList.clear();
        int type = getIntent().getIntExtra(DISPLAY_TYPE, 0);
        switch (type) {
            case TYPE_QUICK:
                wordList.addAll(QuickActivity.wordList);
                break;

            case TYPE_MATCHING:
                wordList.addAll(MatchingActivity.wordList);
                break;

            case TYPE_GAME:
                for (Integer integer : GameActivity.gameWordIdList) {
                    List<Word> words = LitePal.where("wordId = ?", integer + "").find(Word.class);
                    wordList.add(words.get(0));
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        ActivityCollector.startOtherActivity(DisplayActivity.this, MainActivity.class);
        // 释放
        QuickActivity.wordList.clear();
        MatchingActivity.wordList.clear();
        MatchingActivity.itemMatchingList.clear();
        GameActivity.gameWordIdList.clear();
        Log.d(TAG, "onBackPressed: 清空队列");
        finish();
    }
}