package com.jediwus.learningapplication.activity.menu;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.LoadingGameActivity;
import com.jediwus.learningapplication.activity.MatchingActivity;
import com.jediwus.learningapplication.activity.OcrActivity;
import com.jediwus.learningapplication.activity.QuickActivity;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.NumberController;
import com.jediwus.learningapplication.pojo.ItemMatching;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

public class FragmentReview extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentReview";

    private AlertDialog dialog;

    private final int FINISH = 0;
    private final int ERROR = 1;
    private final int LOAD_MATCHING_DATA = 2;
    private final int LOAD_QUICK_DATA = 3;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FINISH:
                    dialog.dismiss();
                    Intent intentOCR = new Intent(getActivity(), OcrActivity.class);
                    startActivity(intentOCR);
                    break;
                case LOAD_QUICK_DATA:
                    new Handler().postDelayed(() -> {
                        dialog.dismiss();
                        Intent intentQuick = new Intent(MyApplication.getContext(), QuickActivity.class);
                        startActivity(intentQuick, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }, 500);
                    break;
                case LOAD_MATCHING_DATA:
                    new Handler().postDelayed(() -> {
                        dialog.dismiss();
                        Intent intentMatching = new Intent(MyApplication.getContext(), MatchingActivity.class);
                        startActivity(intentMatching, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    }, 500);
                    break;
                case ERROR:
                    dialog.dismiss();
                    Toast.makeText(MyApplication.getContext(), "似乎哪里出错了，请重试", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    /**
     * 当Fragment的布局视图被创建时调用，该方法可以访问Fragment布局中的视图元素，如Button、TextView等。
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RelativeLayout layout_quick = view.findViewById(R.id.layout_review_quick);
        layout_quick.setOnClickListener(this);
        RelativeLayout layout_match = view.findViewById(R.id.layout_review_match);
        layout_match.setOnClickListener(this);
        RelativeLayout layout_OCR = view.findViewById(R.id.layout_review_camera);
        layout_OCR.setOnClickListener(this);
        RelativeLayout layout_game = view.findViewById(R.id.layout_review_game);
        layout_game.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 点击单词速记事件的处理
            case R.id.layout_review_quick:
                showProgressDialog();
                new Thread(() -> {
                    // 加载单词速记的数据
                    loadQuickData(DataConfig.getQuickNumber());
                    Message message = new Message();
                    message.what = LOAD_QUICK_DATA;
                    handler.sendMessage(message);
                }).start();
                break;
            // 点击单词消消乐事件的处理
            case R.id.layout_review_match:
                showProgressDialog();
                new Thread(() -> {
                    // 加载消消乐的单词数据
                    loadMatchingData();
                    Message message = new Message();
                    message.what = LOAD_MATCHING_DATA;
                    handler.sendMessage(message);
                }).start();
                break;
            // 点击拍照事件的处理
            case R.id.layout_review_camera:
                Intent intentOcr = new Intent(getActivity(), OcrActivity.class);
                startActivity(intentOcr);
                break;

            // 点击游戏事件的处理
            case R.id.layout_review_game:
                new Handler().postDelayed(() -> {
                    Intent intentGame = new Intent(MyApplication.getContext(), LoadingGameActivity.class);
                    intentGame.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentGame, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }, 350);
                break;

            default:
                break;
        }
    }

    /**
     * 加载消消乐的单词数据
     */
    private void loadMatchingData() {
        // 开始前，清空 MatchingActivity.wordList
        if (!MatchingActivity.wordList.isEmpty()) {
            MatchingActivity.wordList.clear();
        }
        // 开始前，清空 MatchingActivity.itemMatchingList
        if (!MatchingActivity.itemMatchingList.isEmpty()) {
            MatchingActivity.itemMatchingList.clear();
        }
        // 获取 匹配单词数量
        int mNumber = DataConfig.getMatchingNumber();
        List<Word> wordList = LitePal.select("wordId", "word").find(Word.class);
        // 随机生成含 mNumber 个元素的 单词ID 数组

        int[] randomArray = NumberController.getRandomNumberArray(0, wordList.size() - 1, mNumber);
        // 断言 randomArray 不为空
        assert randomArray != null;
        for (int i = 0; i < mNumber; i++) {
            // 完成 单词本体 的添加
            MatchingActivity.itemMatchingList.add(
                    new ItemMatching(
                            wordList.get(randomArray[i]).getWordId(),
                            wordList.get(randomArray[i]).getWord(),
                            false));
            List<Word> words = LitePal.where("wordId = ?", wordList.get(randomArray[i]).getWordId() + "")
                    .select("wordId", "word")
                    .find(Word.class);
            MatchingActivity.wordList.add(words.get(0));
            Log.d(TAG, "loadMatchingData: 单词本体为 " + wordList.get(randomArray[i]).getWord());

            // 接下来完成 单词释义 的添加
            List<Translation> translationList = LitePal.where("wordId = ?", wordList.get(randomArray[i]).getWordId() + "").find(Translation.class);
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < translationList.size(); j++) {
                if (j != (translationList.size() - 1)) {
                    builder.append(translationList.get(j).getWordType())
                            .append(". ")
                            .append(translationList.get(j).getCnMeaning())
                            .append("\n");
                } else {
                    builder.append(translationList.get(j).getWordType())
                            .append(". ")
                            .append(translationList.get(j).getCnMeaning());
                }
            }
            MatchingActivity.itemMatchingList.add(
                    new ItemMatching(
                            wordList.get(randomArray[i]).getWordId(),
                            builder.toString(),
                            false));
        }
        // 打乱匹配序列的顺序
        Collections.shuffle(MatchingActivity.itemMatchingList);
        Collections.shuffle(MatchingActivity.wordList);
    }

    /**
     * 加载单词速记的数据
     *
     * @param wordNum int
     */
    private void loadQuickData(int wordNum) {
        if (!QuickActivity.wordList.isEmpty())
            QuickActivity.wordList.clear();
        // 准备单词数据
        List<Word> words = LitePal.select("wordId", "word", "ukPhone").find(Word.class);
        // 随机匹配单词ID
        int[] randomId = NumberController.getRandomNumberArray(0, words.size() - 1, wordNum);
        for (int i = 0; i < wordNum; i++) {
            // 添加数据，断言 randomArray 不为空
            if (randomId != null) {
                QuickActivity.wordList.add(words.get(randomId[i]));
            }
        }
    }

    private void showProgressDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle("请稍候");
        builder.setMessage("单词资源加载中...");
        ProgressBar progressBar = new ProgressBar(requireActivity());
        builder.setView(progressBar);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }
}
