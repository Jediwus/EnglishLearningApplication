package com.jediwus.learningapplication.activity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.WordListAdapter;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.pojo.ItemWordList;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class KnownWordsFragment extends Fragment {
    private static final String TAG = "KnownWordsFragment";

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private LinearLayout layoutNotFound;

    private final List<ItemWordList> mItemWordListList = new ArrayList<>();
    private WordListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_fragment_word_list);
        layoutNotFound = view.findViewById(R.id.layout_list_not_found);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(this::refreshRecyclerView);
        // 加载单词列表
        loadWordList();
        // 显示单词列表
        showWordList();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void refreshRecyclerView() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            requireActivity().runOnUiThread(() -> {
                loadWordList();
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            });
        }).start();
    }

    private void loadWordList() {
        // 从数据库或其他数据源加载单词列表
        mItemWordListList.clear();
        List<Word> wordList = LitePal.where("isEasy = ?", 1 + "")
                .select("wordId", "word")
                .find(Word.class);
        for (Word word : wordList) {
            mItemWordListList.add(new ItemWordList(word.getWordId(), word.getWord(), getTranslation(word.getWordId()), false, true));
        }
    }

    private void showWordList() {
        if (mItemWordListList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutNotFound.setVisibility(View.VISIBLE);
        } else {
            layoutNotFound.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        // 创建适配器并设置到 RecyclerView 中
        adapter = new WordListAdapter(mItemWordListList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private String getTranslation(int id) {
        List<Translation> translationList = LitePal.where("wordId = ?", id + "").find(Translation.class);
        return translationList.get(0).getWordType() +
                ". " +
                translationList.get(0).getCnMeaning();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 该碎片onStart");
        loadWordList();
        showWordList();
    }
}
