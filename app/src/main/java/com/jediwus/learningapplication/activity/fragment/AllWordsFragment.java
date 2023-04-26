package com.jediwus.learningapplication.activity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

// 实现全部词汇列表
public class AllWordsFragment extends Fragment {

    private static final String TAG = "AllWordsFragment";

    private int currentItemNum = 0;

    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeRefresh;

    private final List<ItemWordList> mItemWordListList = new ArrayList<>();

    private WordListAdapter adapter;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_fragment_word_list);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                assert layoutManager != null;
                int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (firstCompletelyVisibleItemPosition == 0) {
                    Log.d(TAG, "onScrolled: 现在在顶部");
                }
                int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                    Log.d(TAG, "onScrolled: 可见单词数量增加");
                    new Thread(() -> {
                        loadWordList();
                        Message message = new Message();
                        message.what = 0;
                        handler.sendMessage(message);
                    }).start();
                }
            }
        });
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
            requireActivity().runOnUiThread(() -> swipeRefresh.setRefreshing(false));
        }).start();
    }

    private void loadWordList() {
        // 从数据库或其他数据源加载单词列表
        int addItemNum = 100;
        Log.d(TAG, "loadWordList: 现在的currentItemNum为 " + currentItemNum);
        List<Word> wordList = LitePal.select("wordId", "word")
                .limit(addItemNum)
                .offset(currentItemNum + addItemNum)
                .find(Word.class);
        currentItemNum += addItemNum;
        for (Word word : wordList) {
            mItemWordListList.add(new ItemWordList(word.getWordId(), word.getWord(), getTranslation(word.getWordId()), false, true));
        }
    }

    private void showWordList() {
        // 创建适配器并设置到RecyclerView中
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

}
