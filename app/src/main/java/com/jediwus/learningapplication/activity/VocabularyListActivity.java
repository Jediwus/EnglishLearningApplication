package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.WordPagerAdapter;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.UserPreference;

import org.litepal.LitePal;

import java.util.List;

public class VocabularyListActivity extends BaseActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_list);

        List<UserPreference> userPreferenceList = LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(UserPreference.class);
        int bookId = userPreferenceList.get(0).getCurrentBookId();
        int currentWordNumber = ExternalData.getWordsTotalNumbersById(bookId);

        ImageView imageHome = findViewById(R.id.img_vocabulary_home);
        imageHome.setOnClickListener(view -> onBackPressed());

        ImageView imgBook = findViewById(R.id.img_vocabulary_book);
        Glide.with(this).load(ExternalData.getBookPicById(bookId)).into(imgBook);

        TextView tv_name = findViewById(R.id.text_vocabulary_name);
        tv_name.setText(ExternalData.getBookNameById(bookId));

        TextView tv_number = findViewById(R.id.text_vocabulary_num);
        tv_number.setText("词汇总数: " + currentWordNumber);

        ViewPager2 viewPager = findViewById(R.id.view_pager_vocabulary);
        TabLayout tabLayout = findViewById(R.id.tab_vocabulary_layout);

        // 创建适配器并将其设置到 ViewPager2 中
        WordPagerAdapter wordPagerAdapter = new WordPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(wordPagerAdapter);

        // 将 TabLayout 与 ViewPager2 关联起来
//        tabLayout.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("全部词汇");
                            tab.setIcon(R.drawable.icon_document);
                            break;
                        case 1:
                            tab.setText("生词本");
                            tab.setIcon(R.drawable.icon_star);
                            break;
                        case 2:
                            tab.setText("熟知词");
                            tab.setIcon(R.drawable.icon_delete);
                            break;
                    }
                }
        ).attach();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}