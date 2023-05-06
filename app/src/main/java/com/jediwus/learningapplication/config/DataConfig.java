package com.jediwus.learningapplication.config;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;

import com.jediwus.learningapplication.service.NotificationService;
import com.jediwus.learningapplication.myUtil.MyApplication;

public class DataConfig {

    /**
     * 从词库中抽取的单词数目,默认50
     */
    public static final int EXTRACT_WORDS_NUM = 50;

    /**
     * 游戏回合时间限制：困难
     */
    public static final int ACTION_TIME_A = 5;

    /**
     * 游戏回合时间限制：普通
     */
    public static final int ACTION_TIME_B = 8;

    /**
     * 游戏回合时间限制：简单
     */
    public static final int ACTION_TIME_C = 10;

    /**
     * 软件所需权限列表
     */
    public static String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    // SharedDataName
    public static String SharedDataName = "dataConfig";

    // 是否第一次运行或者是否获得了应有的权限
    public static boolean isFirst;
    public static String isFirstName = "isFirst";

    // 是否为修改计划
    // 0为否，1为是
    public static final String UPDATE_NAME = "updateLearningPlan";
    public static final int isUpdate = 1;
    public static final int notUpdate = 0;

    // 当前单词速过的数量
    public static int quickNumber;
    public static String tagQuickNumber = "tagQuickNumber";
    public static final int defaultQuickNumber = 10;

    // 当前单词匹配的数量
    public static int matchingNumber;
    public static String tagMatchingNumber = "tagMatchingNumber";
    public static final int defaultMatchingNumber = 8;

    /*退出登录时, isLogged 与 QQNumLoggedName 都需要修改*/

    // 是否已登录
    public static boolean isLogged;
    public static String isLoggedName = "isLogged";

    // 当前已登录的用户ID
    public static int WeChatNumLogged;
    public static String WeChatNumLoggedName = "WeChatNumLogged";

    public static int QQNumLogged;
    public static String QQNumLoggedName = "QQNumLogged";

    // 通知横幅
    public static boolean isNotificationOn;
    public static String isNotificationOnName = "isNotificationOn";

    // 通知横幅的单词范围
    public static int rangeMode;
    public static String rangeModeName = "rangeModeName";

    // 类闹钟的提醒
    public static boolean isAlarmOn;
    public static String isAlarmOnName = "isAlarm";

    // 提醒的时间
    public static String alarmTime;
    public static String alarmTimeName = "alarmTime";

    // 获取isFirst的值
    public static boolean getIsFirst() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isFirst = preferences.getBoolean(isFirstName, true);
        return isFirst;
    }

    // 设置isFirst的值
    public static void setIsFirst(boolean isFirst) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isFirstName, isFirst);
        editor.apply();
    }

    // 得到isLogged值
    public static boolean getIsLogged() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isLogged = preferences.getBoolean(isLoggedName, false);
        return isLogged;
    }

    // 设置isLogged值
    public static void setIsLogged(boolean isLogged) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isLoggedName, isLogged);
        editor.apply();
    }

    // 获得WeChatNumLogged值
    public static int getWeChatNumLogged() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        WeChatNumLogged = preferences.getInt(WeChatNumLoggedName, 0);
        return WeChatNumLogged;
    }

    // 设置WeChatNumLogged值
    public static void setWeChatNumLogged(int sinaNumLogged) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(WeChatNumLoggedName, sinaNumLogged);
        editor.apply();
    }

    // 获得QQNumLogged值
    public static int getQNumLogged() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        QQNumLogged = preferences.getInt(QQNumLoggedName, 0);
        return QQNumLogged;
    }

    // 设置QQNumLogged值
    public static void setQNumLogged(int QQNumLogged) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(QQNumLoggedName, QQNumLogged);
        editor.apply();
    }

    /**
     * 获得听力速记的数量
     */
    public static int getQuickNumber() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        quickNumber = preferences.getInt(tagQuickNumber, defaultQuickNumber);
        return quickNumber;
    }

    /**
     * 设置听力速记的数量
     */
    public static void setQuickNumber(int quickNumber) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(tagQuickNumber, quickNumber);
        editor.apply();
    }

    /**
     * 获得单词匹配的数量
     */
    public static int getMatchingNumber() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        matchingNumber = preferences.getInt(tagMatchingNumber, defaultMatchingNumber);
        return matchingNumber;
    }

    /**
     * 设置单词匹配的数量
     */
    public static void setMatchingNumber(int matchingNumber) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(tagMatchingNumber, matchingNumber);
        editor.apply();
    }

    /**
     * 获得是否开启了横幅通知服务的
     */
    public static boolean getIsNotificationOn() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isNotificationOn = preferences.getBoolean(isNotificationOnName, false);
        return isNotificationOn;
    }

    /**
     * 设置开启或关闭横幅通知服务的状态
     */
    public static void setIsNotificationOn(boolean isNotifyLearn) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isNotificationOnName, isNotifyLearn);
        editor.apply();
    }

    /**
     * 获得通知横幅单词的范围模式
     */
    public static int getRangeMode() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        rangeMode = preferences.getInt(rangeModeName, NotificationService.ALL_WORD_MODE);
        return rangeMode;
    }

    /**
     * 设置通知横幅单词的范围模式
     */
    public static void setRangeMode(int rangeMode) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(rangeModeName, rangeMode);
        editor.apply();
    }

    // 获得当前是否需要学习提醒
    public static boolean getIsAlarmOn() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isAlarmOn = preferences.getBoolean(isAlarmOnName, false);
        return isAlarmOn;
    }

    // 设置当前是否需要学习提醒
    public static void setIsAlarmOn(boolean isAlarmOn) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isAlarmOnName, isAlarmOn);
        editor.apply();
    }

    // 获得学习提醒的时间
    public static String getAlarmTime() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        alarmTime = preferences.getString(alarmTimeName, "");
        return alarmTime;
    }

    // 设置学习提醒的时间
    public static void setAlarmTime(String alarmTime) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putString(alarmTimeName, alarmTime);
        editor.apply();
    }

}
