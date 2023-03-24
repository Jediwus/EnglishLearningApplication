package com.jediwus.learningapplication.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.SearchAdapter;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.pojo.ItemSearch;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";

    private RecyclerView recyclerViewSearch;

    private RelativeLayout layoutResult;

    private final List<ItemSearch> itemSearchList = new ArrayList<>();

    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        EditText editText = findViewById(R.id.edit_search);
        TextView textCancel = findViewById(R.id.text_search_cancel);
        ImageView imageSearchNoResult= findViewById(R.id.search_error);
        recyclerViewSearch = findViewById(R.id.recycler_search);
        layoutResult = findViewById(R.id.layout_search_result);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewSearch.setLayoutManager(linearLayoutManager);
        searchAdapter = new SearchAdapter(itemSearchList);
        recyclerViewSearch.setAdapter(searchAdapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString().trim())) {
                    layoutResult.setVisibility(View.VISIBLE);
                    recyclerViewSearch.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, editable.toString().trim());
                    setData(editable.toString().trim());
                }
            }
        });

        textCancel.setOnClickListener(view -> onBackPressed());

        // 创建动画效果
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        // 设置动画持续时间
        animator.setDuration(2000);
        // 设置动画效果的监听器
        animator.addUpdateListener(animation -> {
            // 获取动画进度值
            float progress = (float) animation.getAnimatedValue();
            // 透明度变化
            imageSearchNoResult.setAlpha(progress);
        });
        // 开始动画效果
        animator.start();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setData(String str) {
        itemSearchList.clear();
        List<Word> wordList = LitePal.where("word like ?", str + "%")
                .select("wordId", "word", "ukPhone")
                .limit(10)
                .find(Word.class);
        if (!wordList.isEmpty()) {
            for (Word word : wordList) {
                List<Translation> translationList = LitePal
                        .where("wordId = ?", word.getWordId() + "")
                        .select("wordType", "cnMeaning").find(Translation.class);
                StringBuilder stringBuilder = new StringBuilder();
                for (Translation translation : translationList) {
                    stringBuilder.append(translation.getWordType())
                            .append(". ")
                            .append(translation.getCnMeaning())
                            .append(" ");
                }
                itemSearchList.add(
                        new ItemSearch(
                                word.getWordId(),
                                word.getWord(),
                                word.getUkPhone(),
                                stringBuilder.toString()
                        )
                );
                Log.d(TAG, "setData: getUkPhone():" + word.getUkPhone());
            }
            layoutResult.setVisibility(View.GONE);
            recyclerViewSearch.setVisibility(View.VISIBLE);
        } else {
            layoutResult.setVisibility(View.VISIBLE);
            recyclerViewSearch.setVisibility(View.GONE);
        }
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

}