package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.DisplayAdapter;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.pojo.ItemDisplay;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends BaseActivity {

    public final String DISPLAY_TYPE = "displayType";
    public final int FINISH = 0;
    public final int TYPE_MATCHING = 1;
    public final int TYPE_QUICK = 2;
    public final int TYPE_GAME = 3;

    private List<ItemDisplay> itemDisplayList = new ArrayList<>();
    private List<Word> wordList = new ArrayList<>();

    private RecyclerView recyclerView;
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

        recyclerView = findViewById(R.id.recycler_display);
        MaterialButton btn_home = findViewById(R.id.btn_display_Home);
        btn_home.setOnClickListener(view -> onBackPressed());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        displayAdapter = new DisplayAdapter(itemDisplayList);
        recyclerView.setAdapter(displayAdapter);

        new Thread(() -> {
            // 检测 intent 传进来的 type类型
            typeDetection();
            // 绑定单词数据到 展示List 中
            databaseList();

            Message message = new Message();
            message.what = FINISH;
            handler.sendMessage(message);
        }).start();

    }

    private void typeDetection() {

    }

    @Override
    public void onBackPressed() {
        ActivityCollector.startOtherActivity(DisplayActivity.this, MainActivity.class);
        finish();
    }
}