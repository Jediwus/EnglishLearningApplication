package com.jediwus.learningapplication.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.broadcast.NotificationReceiver;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.MyApplication;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";
    // 仨模式
    public static final int ALL_WORD_MODE = 0;
    public static final int STARED_WORD_MODE = 1;
    public static final int KNOWN_WORD_MODE = 2;
    /**
     * 当前模式，默认为 -1
     */
    public static int currentMode = -1;
    /**
     * 注：该 List 的 Word 里只含 word 和 wordId
     */
    public static List<Word> wordList = new ArrayList<>();
    /**
     * wordList 的数组下标
     */
    public static int index = 0;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化单词列表
        initWordList();

        // 如遇到版本需要，首先创建通知渠道
        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        // 只在Android 8.O之上需要渠道，这里的第一个参数要和下面的channelId一样，8.0以下没有渠道这一说法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 通知重要程度设置
            // IMPORTANCE_NONE 关闭通知
            // IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
            // IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
            // IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
            // IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
            NotificationChannel channel = new NotificationChannel(ExternalData.channelIdNtf,
                    ExternalData.channelNameNtf,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false); // 是否在桌面icon右上角展示小红点
            channel.enableVibration(false);
            channel.setSound(null, null);
            channel.setVibrationPattern(new long[]{0});
            notificationManager.createNotificationChannel(channel);
        }
        // 创建通知
        Notification notification = createNotification();
        // 这里真正启用通知的前台服务
        startForeground(1, notification);

    }

    /**
     * 刷新新前台服务的显示
     */
    public static void renewTheNotification() {
        NotificationManager notificationManager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        Notification notification = createNotification();
        notificationManager.notify(1, notification);
    }

    /**
     * Create notification.
     * 创建通知
     *
     * @return the notification
     */
    public static Notification createNotification() {
        // 在 LitePal 中查询指定 wordId 的 Word 对象，并仅选择 wordId、word 和 isCollected 这三个字段，
        // 并获取第一个结果（假设查询结果不为空），用 word 变量存储
        Word word = LitePal.where("wordId = ?", getCurrentWord().getWordId() + "")
                .select("wordId", "word", "isCollected")
                .find(Word.class)
                .get(0);

        // 在 LitePal 中查询当前单词的解释列表，并获取一个包含所有解释字符串的 StringBuilder
        List<Translation> translationList = LitePal.where("wordId = ?", word.getWordId() + "")
                .find(Translation.class);
        StringBuilder stringBuilder = new StringBuilder();
        for (Translation translation : translationList) {
            stringBuilder.append(translation.getWordType())
                    .append(". ")
                    .append(translation.getCnMeaning())
                    .append(" ");
        }

        // 利用远程视图开发小部件，创建一个 RemoteViews 对象
        RemoteViews remoteViews = new RemoteViews(MyApplication.getContext().getPackageName(), R.layout.widget_notification);

        // 设置横幅单词和意思，使用其 setText 方法为其内部的两个 TextView 设置文本内容
        remoteViews.setTextViewText(R.id.text_notification_word, word.getWord());
        remoteViews.setTextViewText(R.id.text_notification_meaning, stringBuilder.toString());

        // 设置意图和动作，创建一个 Intent 对象，并设置其 Action 为 NotificationReceiver.ACTION_NEXT，
        // 并将其作为参数传递给 PendingIntent 对象 nextPI
        Intent intentNext = new Intent(MyApplication.getContext(), NotificationReceiver.class);
        intentNext.setAction(NotificationReceiver.ACTION_NEXT);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent nextPI = PendingIntent.getBroadcast(MyApplication.getContext(), -1, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

        // 创建一个 Intent 对象，并设置其 Action 为 NotificationReceiver.ACTION_AUDIO，
        // 并将其作为参数传递给 PendingIntent 对象 audioPI
        Intent intentAudio = new Intent(MyApplication.getContext(), NotificationReceiver.class);
        intentAudio.setAction(NotificationReceiver.ACTION_AUDIO);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent audioPI = PendingIntent.getBroadcast(MyApplication.getContext(), -1, intentAudio, PendingIntent.FLAG_UPDATE_CURRENT);

        // 如果当前模式不是 STARED_WORD_MODE，则创建一个 Intent 对象，并设置其 Action 为 NotificationReceiver.ACTION_STAR，
        // 并将其作为参数传递给 PendingIntent 对象 starPI，然后将其设置为 RemoteViews 对象的 ImageView 的点击事件
        if (currentMode != STARED_WORD_MODE) {
            remoteViews.setViewVisibility(R.id.img_notification_star, View.VISIBLE);
            Intent intentStar = new Intent(MyApplication.getContext(), NotificationReceiver.class);
            intentStar.setAction(NotificationReceiver.ACTION_STAR);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent starPI = PendingIntent.getBroadcast(MyApplication.getContext(), -1, intentStar, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.img_notification_star, starPI);
        } else {
            // 如果当前模式是 STARED_WORD_MODE，则隐藏 RemoteViews 对象的 星标
            remoteViews.setViewVisibility(R.id.img_notification_star, View.GONE);
        }

        // 将 audioPI 和 nextPI 分别设置为 RemoteViews 对象的两个 ImageView 的点击事件
        remoteViews.setOnClickPendingIntent(R.id.img_notification_voice, audioPI);
        remoteViews.setOnClickPendingIntent(R.id.img_notification_next, nextPI);

        if (word.getIsCollected() == 1) {
            remoteViews.setImageViewResource(R.id.img_notification_star, R.drawable.icon_star_selected);
            Log.d(TAG, "----> startOrUpdateNotification: 收藏单词");
            Log.d(TAG, "");
        } else {
            remoteViews.setImageViewResource(R.id.img_notification_star, R.drawable.icon_star);
            Log.d(TAG, "----> startOrUpdateNotification: 未收藏单词");
        }

        return new NotificationCompat.Builder(MyApplication.getContext(), ExternalData.channelIdNtf)
                .setCustomBigContentView(remoteViews)
                .setCustomContentView(remoteViews)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.ic_launcher))
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVibrate(new long[]{0})
                .setSound(null)
                .build();
    }

    /**
     * 获取当前 wordList 的下标为 currentIndex 的元素
     *
     * @return Word
     */
    public static Word getCurrentWord() {
        return wordList.get(index);
    }

    /**
     * 播放单词发音
     */
    public static void playWordPronunciation() {
        MediaHelper.play(getCurrentWord().getWord());
    }

    /**
     * 更新生词图标及状态
     */
    public static void setStarStatus() {
        Word word = LitePal.where("wordId = ?", getCurrentWord().getWordId() + "")
                .find(Word.class).get(0);
        if (word.getIsCollected() == 0) {
            Log.d(TAG, "setStarStatus: " + word.getWord());
            Word newWord = new Word();
            newWord.setIsCollected(1);
            newWord.updateAll("wordId = ?", word.getWordId() + "");
        } else {
            Word newWord = new Word();
            newWord.setToDefault("isCollected");
            newWord.updateAll("wordId = ?", word.getWordId() + "");
        }
    }

    /**
     * 初始化单词列表
     */
    private void initWordList() {
        wordList.clear();
        switch (currentMode) {
            // 乱序大纲
            case ALL_WORD_MODE:
                wordList = LitePal.select("wordId", "word")
                        .find(Word.class);
                Collections.shuffle(wordList);
                break;
            // 学过的词
            case KNOWN_WORD_MODE:
                wordList = LitePal.where("haveLearned = ?", 1 + "")
                        .select("wordId", "word")
                        .find(Word.class);
                Collections.shuffle(wordList);
                break;
            // 生词
            case STARED_WORD_MODE:
                wordList = LitePal.where("isCollected = ?", 1 + "")
                        .select("wordId", "word")
                        .find(Word.class);
                Collections.shuffle(wordList);
                break;
            default:
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "----> onStartCommand: 横幅通知前台服务");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "----> onDestroy: 横幅通知前台服务");
    }
}
