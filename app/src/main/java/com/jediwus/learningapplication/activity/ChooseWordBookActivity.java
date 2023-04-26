package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.WordBookAdapter;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.pojo.ItemWordBook;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChooseWordBookActivity extends BaseActivity {

    private final List<ItemWordBook> itemWordBookList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_word_book);

        RecyclerView recyclerView = findViewById(R.id.recycler_word_book_list);
//        ImageView imgRestore = findViewById(R.id.img_data_recovery);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        initWordBookData();

        WordBookAdapter wordBookAdapter = new WordBookAdapter(itemWordBookList);
        recyclerView.setAdapter(wordBookAdapter);

    }

    // 词书数据初始化
    private void initWordBookData() {
        itemWordBookList.add(new ItemWordBook(
                ExternalData.CET4_CoreWord,
                ExternalData.getBookNameById(ExternalData.CET4_CoreWord),
                ExternalData.getWordsTotalNumbersById(ExternalData.CET4_CoreWord),
                "来源于：有道考神团队",
                ExternalData.getBookPicById(ExternalData.CET4_CoreWord),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.CET4_All,
                ExternalData.getBookNameById(ExternalData.CET4_All),
                ExternalData.getWordsTotalNumbersById(ExternalData.CET4_All),
                "来源于：有道词典",
                ExternalData.getBookPicById(ExternalData.CET4_All),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.CET6_CoreWord,
                ExternalData.getBookNameById(ExternalData.CET6_CoreWord),
                ExternalData.getWordsTotalNumbersById(ExternalData.CET6_CoreWord),
                "来源于：有道考神团队",
                ExternalData.getBookPicById(ExternalData.CET6_CoreWord),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.CET6_All,
                ExternalData.getBookNameById(ExternalData.CET6_All),
                ExternalData.getWordsTotalNumbersById(ExternalData.CET6_All),
                "来源于：有道词典",
                ExternalData.getBookPicById(ExternalData.CET6_All),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.KaoYan_CoreWord,
                ExternalData.getBookNameById(ExternalData.KaoYan_CoreWord),
                ExternalData.getWordsTotalNumbersById(ExternalData.KaoYan_CoreWord),
                "来源于：有道考神团队",
                ExternalData.getBookPicById(ExternalData.KaoYan_CoreWord),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.kaoYan_All,
                ExternalData.getBookNameById(ExternalData.kaoYan_All),
                ExternalData.getWordsTotalNumbersById(ExternalData.kaoYan_All),
                "来源于：有道词典",
                ExternalData.getBookPicById(ExternalData.kaoYan_All),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.Level4_CoreWord,
                ExternalData.getBookNameById(ExternalData.Level4_CoreWord),
                ExternalData.getWordsTotalNumbersById(ExternalData.Level4_CoreWord),
                "来源于：有道考神团队",
                ExternalData.getBookPicById(ExternalData.Level4_CoreWord),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.Level4_All,
                ExternalData.getBookNameById(ExternalData.Level4_All),
                ExternalData.getWordsTotalNumbersById(ExternalData.Level4_All),
                "来源于：有道词典",
                ExternalData.getBookPicById(ExternalData.Level4_All),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.Level8_CoreWord,
                ExternalData.getBookNameById(ExternalData.Level8_CoreWord),
                ExternalData.getWordsTotalNumbersById(ExternalData.Level8_CoreWord),
                "来源于：有道考神团队",
                ExternalData.getBookPicById(ExternalData.Level8_CoreWord),
                false));

        itemWordBookList.add(new ItemWordBook(
                ExternalData.Level8_All,
                ExternalData.getBookNameById(ExternalData.Level8_All),
                ExternalData.getWordsTotalNumbersById(ExternalData.Level8_All),
                "来源于：有道词典",
                ExternalData.getBookPicById(ExternalData.Level8_All),
                false));
        itemWordBookList.add(new ItemWordBook(
                -1,
                "作者制作中...",
                0,
                "敬请期待...",
                "",
                true));
    }


    @Override
    public void onBackPressed() {
        // 已登录
        if (LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(UserPreference.class).get(0).getCurrentBookId() != -1) {
            super.onBackPressed();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChooseWordBookActivity.this);
            builder.setTitle("精灵的挽留")
                    .setMessage("\n您确定要退出吗?")
                    .setPositiveButton("确定", (dialogInterface, i) -> ActivityCollector.finishAll())
                    .setNegativeButton("取消", null)
                    .show();
        }

    }
}