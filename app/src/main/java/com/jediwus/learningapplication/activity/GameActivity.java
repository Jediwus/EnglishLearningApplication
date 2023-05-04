package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.MeaningPickerAdapter;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.pojo.GameQueue;
import com.jediwus.learningapplication.pojo.ItemMeaningPicker;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameActivity extends BaseActivity {
    private static final String TAG = "GameActivity";
    /**
     * 游戏选项队列
     */
    private final List<ItemMeaningPicker> itemMeaningPickerList = new ArrayList<>();
    /**
     * 由随机方法从词库中挑选出的 50 个单词
     */
    public static List<GameQueue> gameQueueList = new ArrayList<>();
    /**
     * 所有在游戏中出现过的单词的 ID
     */
    public static ArrayList<Integer> gameWordIdList = new ArrayList<>();
    /**
     * 当前显示的单词
     */
    private GameQueue currentGameWord;
    /**
     * 当前显示的单词在队列中的位置下标，从 0 开始
     */
    private int currentWordIndex = 0;
    /**
     * 魔王的进度
     */
    private int pgMagus = 0;
    /**
     * 魔王的正常速度
     */
    private final int offsetMagus = 2;
    /**
     * 克罗洛的进度
     */
    private int pgChrono = 800;
    /**
     * 克罗洛的正常速度
     */
    private final int offsetChrono = 1;
    /**
     * 规定每回合行动时间
     */
    private static int actionTimePerRound;
    /**
     * 接受自定义传值
     */
    public static int actionTimeTemp;

    private ImageView img_chrono;
    private ImageView img_magus;
    private TextView tv_word;
    private MeaningPickerAdapter meaningPickerAdapter;
    private int progressWidth;

    private Handler mHandler1;
    private Handler mHandler2;

    private Runnable mRunnable1;
    private Runnable mRunnable2;

    private boolean isGameOver = false;
    private ProgressBar progressBar;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        actionTimeTemp = DataConfig.ACTION_TIME_A;
        actionTimePerRound = actionTimeTemp;

        // 魔王——图片
        img_magus = findViewById(R.id.img_game_magus);
        Glide.with(GameActivity.this).load(R.drawable.gif_magus).into(img_magus);

        // 克罗洛——图片
        img_chrono = findViewById(R.id.img_game_chrono);
        Glide.with(GameActivity.this).load(R.drawable.gif_chrono).into(img_chrono);

        // 魔王部下奥兹——图片
        ImageView img_ozzie = findViewById(R.id.img_game_ozzie);
        Glide.with(GameActivity.this).load(R.drawable.gif_ozzie).into(img_ozzie);

        // 玛鲁——图片
        ImageView img_marl = findViewById(R.id.img_game_marl);
        Glide.with(GameActivity.this).load(R.drawable.gif_marle).into(img_marl);

        // 游戏进度——进度条
        progressBar = findViewById(R.id.progress_game);

        // 回合时间剩余——文本
        TextView tv_time = findViewById(R.id.text_game_timer);

        // 单词——文本
        tv_word = findViewById(R.id.text_game_word);

        // 选择栏——滑动组件
        RecyclerView recyclerView = findViewById(R.id.recycler_game_bottom);

        // 播放背景音乐
        MediaHelper.playLocalSourceLoop(R.raw.battle);

        // 瀑布流布局效果，配合 RecyclerView 使用
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        meaningPickerAdapter = new MeaningPickerAdapter(itemMeaningPickerList);
        recyclerView.setAdapter(meaningPickerAdapter);

        // 初始化游戏单词数据
        initGameWordResource();

        // 进度条设置
        progressBar.setProgress(pgMagus);
        progressBar.setSecondaryProgress(pgChrono);
        progressBar.setMax(10000);
        progressBar.post(() -> progressWidth = progressBar.getWidth());

        // 对答案选项的点击处理
        meaningPickerAdapter.setOnItemClickListener((parent, view, position, itemMeaningPicker) -> {
            if (MeaningPickerAdapter.isFirstClick) {
                // 选择了错误的选项
                if (itemMeaningPicker.getId() != currentGameWord.getId()) {
                    wrongAnswer();
                    itemMeaningPicker.setIfRight(ItemMeaningPicker.WRONG);
                } else {
                    // 答对了
                    rightAnswer();
                    itemMeaningPicker.setIfRight(ItemMeaningPicker.RIGHT);
                }
                // 通知更改数据集
                meaningPickerAdapter.notifyDataSetChanged();
                // 本轮答完，将第一次点击的标志置为假
                MeaningPickerAdapter.isFirstClick = false;
                if (!isGameOver) {
                    new Handler().postDelayed(() -> {
                        // 新一轮开始前，将第一次点击的标志置为真
                        MeaningPickerAdapter.isFirstClick = true;
                        // 新一轮开始,初始化游戏单词数据
                        initGameWordResource();
                    }, 250);
                }
            }
        });

        // 对进度条相关显示的线程处理
        mHandler1 = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mRunnable1 = new Runnable() {
                    @Override
                    public void run() {
                        if (!isGameOver) {
                            // 每隔 10ms 循环线程
                            mHandler1.postDelayed(this, 10);

                            // 设置魔王和克罗洛的进度条
                            pgMagus += offsetMagus;
                            pgChrono += offsetChrono;
                            progressBar.setProgress(pgMagus);
                            progressBar.setSecondaryProgress(pgChrono);

                            // 设置魔王和克罗洛的图标跟进
                            img_magus.setTranslationX((float) pgMagus / progressBar.getMax() * progressWidth);
                            img_chrono.setTranslationX((float) pgChrono / progressBar.getMax() * progressWidth);

                            // 克罗洛被魔王赶上了，游戏失败
                            if (pgChrono <= pgMagus) {
                                // 退出条件满足，终止 mRunnable1
                                stopTime1();
                                // 游戏结束标志设为真
                                isGameOver = true;
                                Log.d(TAG, "run: 输了");
                                MediaHelper.releaseMediaPlayer();
                                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                                intent.putExtra(GameOverActivity.LOSE_OR_WIN, GameOverActivity.GAME_LOSE);
                                startActivity(intent);
                                finish();
                            } else if (pgChrono >= progressBar.getMax()) {
                                // 克罗洛先抵达魔王城，干掉了奥兹，救回了好友玛鲁
                                // 退出条件满足，终止 mRunnable1
                                stopTime1();
                                // 游戏结束标志设为真
                                isGameOver = true;
                                Log.d(TAG, "run: 赢了");
                                MediaHelper.releaseMediaPlayer();
                                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                                intent.putExtra(GameOverActivity.LOSE_OR_WIN, GameOverActivity.GAME_WIN);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                };
                // 延迟10ms后开始循环
                mHandler1.postDelayed(mRunnable1, 10);
            }
        }).start();

        mHandler2 = new Handler();
        mRunnable2 = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                tv_time.setText((actionTimePerRound--) + "s");
                // 每隔一秒循环变化
                mHandler2.postDelayed(this, 1000);
                // 游戏结束，停止 mHandler 对 mRunnable 的回调，结束无限循环
                if (isGameOver) {
                    stopTime2();
                }
                if (actionTimePerRound == -1) {
                    // 表明尚未作答
                    noAnswer();
                    actionTimePerRound = actionTimeTemp;
                    // 新一轮开始,初始化游戏单词数据
                    initGameWordResource();
                }
            }
        };
        // 10ms后开始循环
        mHandler2.postDelayed(mRunnable2, 10);
    }

    /**
     * 初始化游戏单词数据
     */
    @SuppressLint("NotifyDataSetChanged")
    private void initGameWordResource() {
        currentGameWord = gameQueueList.get(currentWordIndex);
        tv_word.setText(currentGameWord.getWordName());
        // 清空选项列表
        if (!itemMeaningPickerList.isEmpty()) {
            itemMeaningPickerList.clear();
        }
        // 该单词出现过一次就将其 ID 计入 gameWordIdList
        gameWordIdList.add(currentGameWord.getId());
        // 加入选项队列，剩下三个空位另外安排错误选项
        itemMeaningPickerList.add(new ItemMeaningPicker(
                currentGameWord.getId(),
                currentGameWord.getWordMeaning(),
                ItemMeaningPicker.DEFAULT));


        // 从整个单词列表中查询，影响速度
        // 从整个单词列表中查询，得出其余三个错误选项，排除当前的正确单词 id
        List<Translation> translationWrongs =
                LitePal.where("wordId != ?", currentGameWord.getId() + "")
                        .find(Translation.class);
        // 将错误解释list的顺序打乱
        Collections.shuffle(translationWrongs);
        // 加入三个随机错误选项
        for (int i = 0; i < 3; i++) {
            itemMeaningPickerList.add(new ItemMeaningPicker(
                    -1,
                    translationWrongs.get(i).getWordType() + ". " + translationWrongs.get(i).getCnMeaning(),
                    ItemMeaningPicker.DEFAULT));
        }


//        // 从 0~49 中获取 3 个数字放入数组，排除当前的正确单词
//        int[] randomExceptList = NumberController.getRandomExceptList(0, gameQueueList.size() - 1, 3, currentWordIndex);
//        if (randomExceptList != null) {
//            // 从游戏单词列表（50个元素）中查询，得出其余三个错误选项
//            for (int i = 0; i < 3; i++) {
//                itemMeaningPickerList.add(new ItemMeaningPicker(
//                        gameQueueList.get(randomExceptList[i]).getId(),
//                        gameQueueList.get(randomExceptList[i]).getWordMeaning(),
//                        ItemMeaningPicker.DEFAULT));
//            }
//        } else {
//            Log.d(TAG, "initGameWordResource: randomExceptList 为空，没赋到值");
//        }
        // 打乱四个选项的顺序
        Collections.shuffle(itemMeaningPickerList);
        meaningPickerAdapter.notifyDataSetChanged();

    }

    /**
     * 回答正确
     */
    private void rightAnswer() {
        // 设置奖励进度，克罗洛加进度
        int offsetChronoWhenRight = 200;
        if (pgChrono + offsetChronoWhenRight >= progressBar.getMax()) {
            pgChrono = progressBar.getMax();
        } else {
            pgChrono += offsetChronoWhenRight;
        }
        // 回合剩余时间重置，因为上一回合答对了，下一回合开始
        actionTimePerRound = actionTimeTemp;
        // gameWordIdList 下标加一
        currentWordIndex++;
    }

    /**
     * 回答错误
     */
    private void wrongAnswer() {
        // 设置惩罚进度，魔王加进度
        int offsetMagusWhenWrong = 70;
        pgMagus += offsetMagusWhenWrong;
        // 回合剩余时间重置，因为上一回合答错了，下一回合开始
        actionTimePerRound = actionTimeTemp;
        // gameWordIdList 下标加一
        currentWordIndex++;
    }

    /**
     * 尚未作答
     */
    private void noAnswer() {
        // 设置惩罚进度，魔王加进度
        int offsetMagusWhenForgo = 30;
        pgMagus += offsetMagusWhenForgo;
        // gameWordIdList 下标加一
        currentWordIndex++;
    }

    /**
     * 停止 mHandler 对 mRunnable 的回调，结束无限循环
     */
    private void stopTime1() {
        mHandler1.removeCallbacks(mRunnable1);
    }

    /**
     * 停止 mHandler 对 mRunnable 的回调，结束无限循环
     */
    private void stopTime2() {
        mHandler2.removeCallbacks(mRunnable2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MediaHelper.releaseMediaPlayer();
        isGameOver = true;
        // 关闭 mHandler 的 mRunnable 回调
        stopTime1();
        stopTime2();
        gameWordIdList.clear();
        gameQueueList.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameQueueList.clear();
        Log.d(TAG, "onDestroy: 我没了");
    }

}