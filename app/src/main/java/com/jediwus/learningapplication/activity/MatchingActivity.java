package com.jediwus.learningapplication.activity;

import static com.jediwus.learningapplication.config.DataConfig.defaultMatchingNumber;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.MatchingAdapter;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.pojo.ItemMatching;

import java.util.ArrayList;
import java.util.List;

public class MatchingActivity extends BaseActivity {
    private static final String TAG = "MatchingActivity";

    public static List<Word> wordList = new ArrayList<>();

    public static List<ItemMatching> itemMatchingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        RecyclerView recyclerView = findViewById(R.id.recycler_matching);
        RelativeLayout layout_home = findViewById(R.id.layout_matching_home);
        layout_home.setOnClickListener(view -> onBackPressed());
        RelativeLayout layout_help = findViewById(R.id.layout_matching_help);
        layout_help.setOnClickListener(view -> {
            String tips = "\t\t\t糟糕！这些单词和它的释义被打乱了，找出并点击相匹配的两项，将他们消除吧！" +
                    "\n\t\t\t系统默认一局提供" + defaultMatchingNumber + "个词，可在『词书计划』调整。";
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MatchingActivity.this);
            builder.setTitle("帮助")
                    .setMessage(tips)
                    .setPositiveButton("确定", null)
                    .show();
        });

        explosionAnimation();

        // 分两列显示
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        MatchingAdapter matchingAdapter = new MatchingAdapter(itemMatchingList);
        recyclerView.setAdapter(matchingAdapter);

    }
}