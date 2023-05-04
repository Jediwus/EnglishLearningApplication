package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.MeaningPickerAdapter;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.Sentence;
import com.jediwus.learningapplication.database.StudyTimeData;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.LearningController;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.TimeController;
import com.jediwus.learningapplication.pojo.ItemMeaningPicker;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LearningActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LearningActivity";

    private ImageView imageCheck;
    private TextView textLastWord;
    private TextView textLastWordMean;
    private TextView textLearnNumber;
    private TextView textReviewNumber;
    private TextView textWord;
    private TextView textWordPhone;
    private CardView cardWordTip;
    private TextView textWordTip;
    private LinearLayout layoutBottomLearn;
    private RelativeLayout layoutBottomReview;
    private RecyclerView recyclerView;

    private MeaningPickerAdapter meaningPickerAdapter;

    // 学习时间记录
    private long timeOfStart = -1;

    public static final String STATUS_NAME = "learning_status";
    public static final int STATUS_GENERAL_LEARNING = 1;
    public static final int STATUS_LEARNING_AT_ONCE = 2;

    private int currentStatus;

    public static boolean flagNeedRefresh = true;

    // 上一个单词的id、本体和释义
    public static int lastWordId;
    public static int trueLastWordId;
    public static String lastWord;
    public static String lastWordMeaning;

    // 上一个单词释义选择的错误与否标志
    public static int flagLastWord = -1;

    // 提示例句
    private String tipSentence;

    private final List<ItemMeaningPicker> itemMeaningPickerList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        //-------------------------------- UI初始化 --------------------------------------
        // 主页键
        ImageView imageHome = findViewById(R.id.img_learning_home);
        imageHome.setOnClickListener(this);

        // 顶部卡片
        RelativeLayout layoutTopBar = findViewById(R.id.layout_learning_top_bar);
        layoutTopBar.setOnClickListener(this);
        imageCheck = findViewById(R.id.img_learning_top_icon);
        imageCheck.setVisibility(View.GONE);
        textLastWord = findViewById(R.id.text_learning_top_word);
        textLastWordMean = findViewById(R.id.text_learning_top_mean);

        // 需新学 和 需复习
        textLearnNumber = findViewById(R.id.text_learning_learn_num);
        textReviewNumber = findViewById(R.id.text_learning_review_num);
        // 主要单词 和 发音
        textWord = findViewById(R.id.text_learning_main_word);
        textWordPhone = findViewById(R.id.text_learning_word_phone);
        ImageView imagePhone = findViewById(R.id.img_learning_word_phone);
        imagePhone.setOnClickListener(this);
        // 熟知词设置
        LinearLayout layoutWordDelete = findViewById(R.id.layout_learning_word_delete);
        layoutWordDelete.setOnClickListener(this);
        // 不确定时的提示卡片
        cardWordTip = findViewById(R.id.card_learning_word_tip);
        RelativeLayout relativeLayoutTip = findViewById(R.id.layout_learning_word_tip);
        relativeLayoutTip.setOnClickListener(this);
        textWordTip = findViewById(R.id.text_learning_word_tip);

        // 底部学习框
        layoutBottomLearn = findViewById(R.id.layout_learning_word_distinguish);
        // 认识
        RelativeLayout cardLayoutKnow = findViewById(R.id.card_learning_know);
        cardLayoutKnow.setOnClickListener(this);
        // 不确定
        RelativeLayout cardLayoutUncertain = findViewById(R.id.card_learning_uncertainty);
        cardLayoutUncertain.setOnClickListener(this);
        // 不认识
        RelativeLayout cardLayoutDoNotKnow = findViewById(R.id.card_learning_do_not_know);
        cardLayoutDoNotKnow.setOnClickListener(this);

        // 释义选择框
        recyclerView = findViewById(R.id.recyclerview_word_mean_choice);
        // 底部复习框
        layoutBottomReview = findViewById(R.id.layout_learning_word_detail);
        // 看答案按钮
        Button buttonTip = findViewById(R.id.button_learning_check_the_answer);
        buttonTip.setOnClickListener(this);
        //-------------------------------- UI初始化结束 --------------------------------------

        currentStatus = getIntent().getIntExtra(STATUS_NAME, STATUS_GENERAL_LEARNING);
        timeOfStart = TimeController.getCurrentTimeStamp();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        meaningPickerAdapter = new MeaningPickerAdapter(itemMeaningPickerList);
        meaningPickerAdapter.setOnItemClickListener((parent, view, position, itemWordMeanChoice) -> {
            if (MeaningPickerAdapter.isFirstClick) {
                Log.d(TAG, "onCreate: 选项的ID值为：" + itemWordMeanChoice.getId());
                Log.d(TAG, "onCreate: 当前的正确答案ID为：" + LearningController.currentWordId);
                // 选择了错误选项
                if (itemWordMeanChoice.getId() != LearningController.currentWordId) {
                    MediaHelper.playLocalSource(ExternalData.WRONG_TONE);
                    // 模式判断
                    switch (LearningController.currentMode) {
                        // 新学完及时复习
                        case LearningController.MODE_REVIEW_IN_TIME:
                            LearningController.completeJustLearnedToReview(LearningController.currentWordId, false);
                            break;
                        // 浅度和深度复习
                        case LearningController.MODE_REVIEW_ROUTINE:
                            LearningController.completeWordToReview(LearningController.currentWordId, false);
                            break;
                    }
                    // 设置标记为：错选
                    itemWordMeanChoice.setIfRight(ItemMeaningPicker.WRONG);
                    // 顶部错误显示
                    flagLastWord = ItemMeaningPicker.WRONG;
                    // 更新适配器数据
                    meaningPickerAdapter.notifyDataSetChanged();
                    // 将第一次点击的标志置为否
                    MeaningPickerAdapter.isFirstClick = false;
                    // 选错了会跳转至单词详情页，供用户查看解析
                    new Handler().postDelayed(() -> {
                        WordDetailActivity.wordId = LearningController.currentWordId;
                        Intent intent = new Intent(LearningActivity.this, WordDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_LEARNING);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LearningActivity.this).toBundle());
                        // 页面重置，将第一次点击的标志置为真
                        MeaningPickerAdapter.isFirstClick = true;
                    }, 1000);
                } else {  // 选择正确
                    MediaHelper.playLocalSource(ExternalData.RIGHT_TONE);
                    // 模式判断
                    switch (LearningController.currentMode) {
                        // 新学完及时复习
                        case LearningController.MODE_REVIEW_IN_TIME:
                            LearningController.completeJustLearnedToReview(LearningController.currentWordId, true);
                            break;
                        // 浅度和深度复习
                        case LearningController.MODE_REVIEW_ROUTINE:
                            LearningController.completeWordToReview(LearningController.currentWordId, true);
                            break;
                    }
                    // 设置标记为：正确
                    itemWordMeanChoice.setIfRight(ItemMeaningPicker.RIGHT);
                    flagLastWord = ItemMeaningPicker.RIGHT;
                    // 更新适配器数据
                    meaningPickerAdapter.notifyDataSetChanged();
                    // 将第一次点击的标志置为否
                    MeaningPickerAdapter.isFirstClick = false;
                    new Handler().postDelayed(() -> {
                        // 刷新视图
                        updateView();
                        // 页面重置，将第一次点击的标志置为真
                        MeaningPickerAdapter.isFirstClick = true;
                    }, 1000);
                }
            }
        });
        // 添加适配器
        recyclerView.setAdapter(meaningPickerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 要刷新页面了");
        if (flagNeedRefresh) {
            updateView();
            flagNeedRefresh = false;
        }
    }

    /**
     * 刷新视图
     */
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void updateView() {
        // 清空图标
        imageCheck.setVisibility(View.GONE);
        // 需新学数量
        textLearnNumber.setText(LearningController.wordsNeedToLearnList.size() + "");
        // 需复习数量
        textReviewNumber.setText((LearningController.wordsNeedToReviewList.size() + LearningController.wordsJustLearnedList.size()) + "");
        // 使提示卡片消失
        cardWordTip.setVisibility(View.GONE);
        // 提示句子置空
        tipSentence = "";
        // 学习模式判断
        LearningController.currentMode = LearningController.initLearningProcess();
        switch (LearningController.currentMode) {
            case LearningController.MODE_REVIEW_IN_TIME:
                LearningController.currentWordId = LearningController.getJustLearnedToReview();
                setReviewUIVisible();
                break;
            case LearningController.MODE_REVIEW_ROUTINE:
                LearningController.currentWordId = LearningController.getWordToReview();
                setReviewUIVisible();
                break;
            case LearningController.MODE_NEW_LEARNING:
                LearningController.currentWordId = LearningController.getNewWordToLearn();
                setLearningUIVisible();
                break;
            case LearningController.TODAY_TASK_COMPLETE:
                switch (currentStatus) {
                    case STATUS_GENERAL_LEARNING:
//                        Toast.makeText(this, "今日任务已完成", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MissionCompleteActivity.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LearningActivity.this).toBundle());
                        finish();
                        break;
                    case STATUS_LEARNING_AT_ONCE:
                        Toast.makeText(this, "单词夹学习模式结束", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        break;
                }
                break;
        }

        Log.d(TAG, "updateView: 当前单词的ID为：" + LearningController.currentWordId);

        // 从数据库中查询当前单词
        List<Word> wordList = LitePal.where("wordId = ?", LearningController.currentWordId + "")
                .select("wordId", "word", "ukPhone", "usPhone")
                .find(Word.class);

        if (!wordList.isEmpty()) {
            Word word = wordList.get(0);
            // 填入当前单词本体
            textWord.setText(word.getWord());
            if (word.getUkPhone() != null) {
                // 设置成英音
                textWordPhone.setText(word.getUkPhone());
            } else {
                // 设置成美音
                textWordPhone.setText(word.getUsPhone());
            }
            // 界面刷新，单词也要发音
            if (LearningController.currentMode != LearningController.TODAY_TASK_COMPLETE) {
                new Thread(() -> MediaHelper.play(textWord.getText().toString())).start();
            }

            // 查询出：当前单词的释义，暂时放入 stringBuilderRightChoice
            List<Translation> translationList =
                    LitePal.where("wordId = ?", LearningController.currentWordId + "")
                            .find(Translation.class);
            StringBuilder stringBuilderRightChoice = new StringBuilder();
            if (!translationList.isEmpty()) {
                stringBuilderRightChoice.append(translationList.get(0).getWordType())
                        .append(". ")
                        .append(translationList.get(0).getCnMeaning());
            }

            // 两个复习模式下，匹配释义出正确选项和其余三个错误选项
            if (LearningController.currentMode == LearningController.MODE_REVIEW_IN_TIME ||
                    LearningController.currentMode == LearningController.MODE_REVIEW_ROUTINE) {
                // 先清空选项列表
                itemMeaningPickerList.clear();
                // 查询出：其余三个错误选项
                List<Translation> translationWrongs =
                        LitePal.where("wordId != ?", LearningController.currentWordId + "")
                                .find(Translation.class);
                // 将错误解释list的顺序打乱
                Collections.shuffle(translationWrongs);
                // 若选项视图可见
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    // 加入正确释义
                    itemMeaningPickerList.add(new ItemMeaningPicker(
                            LearningController.currentWordId,
                            stringBuilderRightChoice.toString(),
                            ItemMeaningPicker.DEFAULT));
                    // 加入三个随机错误选项
                    for (int i = 0; i < 3; i++) {
                        itemMeaningPickerList.add(new ItemMeaningPicker(
                                -1,
                                translationWrongs.get(i).getWordType() + ". " + translationWrongs.get(i).getCnMeaning(),
                                ItemMeaningPicker.DEFAULT));
                    }
                    // 打乱四个选项的顺序
                    Collections.shuffle(itemMeaningPickerList);
                    meaningPickerAdapter.notifyDataSetChanged();
                }
            }

            // 查询出：当前单词的例句，放入 tipSentence
            List<Sentence> sentenceList =
                    LitePal.where("wordId = ?", LearningController.currentWordId + "")
                            .find(Sentence.class);
            if (!sentenceList.isEmpty()) {
                tipSentence = sentenceList.get(0).getEnSentence();
            }

            // 顶部卡片防止重复
            if (!Objects.equals(wordList.get(0).getWord(), lastWord)) {
                // 上一个单词的本体和释义
                textLastWord.setText(lastWord);
                textLastWordMean.setText(lastWordMeaning);
                trueLastWordId = lastWordId;
                Log.d(TAG, "updateView: 查看flagLastWord，-1代表默认未选；0代表正确；1代表错误：" + flagLastWord);
                // 图标变化
                if (!lastWord.isEmpty()) {
                    Log.d(TAG, "updateView: 进入图标变化判断语句。");
                    if (flagLastWord == ItemMeaningPicker.WRONG) {
                        imageCheck.setVisibility(View.VISIBLE);
                        Glide.with(LearningActivity.this)
                                .load(R.drawable.icon_cross)
                                .into(imageCheck);
                    } else if (flagLastWord == ItemMeaningPicker.RIGHT) {
                        imageCheck.setVisibility(View.VISIBLE);
                        Glide.with(LearningActivity.this)
                                .load(R.drawable.icon_check)
                                .into(imageCheck);
                    }
                }
                // 准备将当前单词数据设置为“上一个单词”
                lastWordId = wordList.get(0).getWordId();
                lastWord = wordList.get(0).getWord();
                lastWordMeaning = stringBuilderRightChoice.toString();
                flagLastWord = -1;
            } else {
                Log.d(TAG, "updateView: 保持卡片不变");
            }
        } else {
            Toast.makeText(this, "好像出了点问题。。。。", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }

    }

    private void setLearningUIVisible() {
        recyclerView.setVisibility(View.GONE);
        layoutBottomReview.setVisibility(View.GONE);
        layoutBottomLearn.setVisibility(View.VISIBLE);
    }

    private void setReviewUIVisible() {
        recyclerView.setVisibility(View.VISIBLE);
        layoutBottomReview.setVisibility(View.VISIBLE);
        layoutBottomLearn.setVisibility(View.GONE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 主页键点击事件
            case R.id.img_learning_home:
                onBackPressed();
                break;

            // 顶部卡片点击事件
            case R.id.layout_learning_top_bar:
                if (trueLastWordId != -1) {
                    WordDetailActivity.wordId = trueLastWordId;
                    Intent intent = new Intent(LearningActivity.this, WordDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_CHECK);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LearningActivity.this).toBundle());
                }
                break;

            // 播放图标点击事件
            case R.id.img_learning_word_phone:
                MediaHelper.play(textWord.getText().toString());
                break;

            // 熟知词卡片点击事件
            case R.id.layout_learning_word_delete:
                LearningController.removeSelectedWord(LearningController.currentWordId);
                Word word = new Word();
                word.setIsEasy(1);
                word.updateAll("wordId = ?", LearningController.currentWordId + "");
                Toast.makeText(this, "已丢到垃圾桶里面", Toast.LENGTH_SHORT).show();
                updateView();
                break;

            // 提示句子
            case R.id.layout_learning_word_tip:
                if (!TextUtils.isEmpty(tipSentence.trim())) {
                    MediaHelper.play(tipSentence);
                }
                break;

            // 认识卡片点击事件
            case R.id.card_learning_know:
                LearningController.completeNewWordToLearn(LearningController.currentWordId);
                updateView();
                break;

            // 不确定卡片点击事件
            case R.id.card_learning_uncertainty:
                if (!TextUtils.isEmpty(tipSentence.trim())) {
                    textWordTip.setText(tipSentence);
                    cardWordTip.setVisibility(View.VISIBLE);
                    MediaHelper.play(tipSentence);
                } else {
                    Toast.makeText(this, "很抱歉，暂无提示", Toast.LENGTH_SHORT).show();
                }
                break;

            // 不认识卡片点击事件
            case R.id.card_learning_do_not_know:
                WordDetailActivity.wordId = LearningController.currentWordId;
                Intent intent = new Intent(LearningActivity.this, WordDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_LEARNING);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LearningActivity.this).toBundle());
                LearningController.completeNewWordToLearn(LearningController.currentWordId);
                break;

            // 看答案按钮点击事件
            case R.id.button_learning_check_the_answer:
                WordDetailActivity.wordId = LearningController.currentWordId;
                ActivityCollector.startOtherActivity(LearningActivity.this, WordDetailActivity.class);
                break;

            default:
                break;

        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LearningActivity.this, MainActivity.class);
        startActivity(intent);
        MediaHelper.releaseMediaPlayer();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flagNeedRefresh = true;
        long timeOfEnd = TimeController.getCurrentTimeStamp();
        long duration = timeOfEnd - timeOfStart;
        timeOfStart = -1;
        // 保存用户学习时长的数据
        StudyTimeData studyTimeData = new StudyTimeData();
        List<StudyTimeData> studyTimeDataList = LitePal
                .where("date = ?", TimeController.getPastDateWithYear(0))
                .find(StudyTimeData.class);
        if (studyTimeDataList.isEmpty()) {
            studyTimeData.setTime(duration + "");
            studyTimeData.setDate(TimeController.getPastDateWithYear(0));
            studyTimeData.save();
        } else {
            int lastTime = Integer.parseInt(studyTimeDataList.get(0).getTime());
            studyTimeData.setTime((lastTime + duration) + "");
            studyTimeData.updateAll("date = ?", TimeController.getPastDateWithYear(0));
        }
    }
}