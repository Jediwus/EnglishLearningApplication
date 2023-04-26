package com.jediwus.learningapplication.config;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;

import com.jediwus.learningapplication.service.NotifyLearnService;
import com.jediwus.learningapplication.myUtil.MyApplication;

public class DataConfig {

    // 软件所需权限列表
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

    /*退出登录时，isLogged与SinaNumLogged都需要修改*/

    // 是否已登录
    public static boolean isLogged;
    public static String isLoggedName = "isLogged";

    // 是否为夜间模式
    public static boolean isNight;
    public static String isNightName = "isNight";

    // 是否需要学习提醒
    public static boolean isAlarm;
    public static String isAlarmName = "isAlarm";

    // 学习提醒的时间
    public static String alarmTime;
    public static String alarmTimeName = "alarmTime";

    // 是否需要开启通知栏学习
    public static boolean isNotifyLearn;
    public static String isNotifyLearnName = "isNotifyLearn";

    // 获得通知栏学习的模式
    public static int notifyLearnMode;
    public static String notifyLearnModeName = "notifyLearnMode";

    // 当前已登录的用户ID
    public static int WeChatNumLogged;
    public static String WeChatNumLoggedName = "WeChatNumLogged";

    public static int QQNumLogged;
    public static String QQNumLoggedName = "QQNumLogged";

    // 是否为修改计划
    // 0为否，1为是
    public static final String UPDATE_NAME = "updateLearningPlan";
    public static final int isUpdate = 1;
    public static final int notUpdate = 0;

    // 当前单词速过的数量
    public static int speedNum;
    public static String speedNumName = "speedNum";

    // 当前单词匹配的数量
    public static int matchNum;
    public static String matchNumName = "matchNum";

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

    // 获得当前是否为夜间模式
    public static boolean getIsNight() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isNight = preferences.getBoolean(isNightName, false);
        return isNight;
    }

    // 设置当前是否为夜间模式
    public static void setIsNight(boolean isNight) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isNightName, isNight);
        editor.apply();
    }

    // 获得当前是否需要学习提醒
    public static boolean getIsAlarm() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isAlarm = preferences.getBoolean(isAlarmName, false);
        return isAlarm;
    }

    // 设置当前是否需要学习提醒
    public static void setIsAlarm(boolean isAlarm) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isAlarmName, isAlarm);
        editor.apply();
    }

    // 获得学习提醒的时间
    public static String getAlarmTime() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        alarmTime = preferences.getString(alarmTimeName, "null");
        return alarmTime;
    }

    // 设置学习提醒的时间
    public static void setAlarmTime(String alarmTime) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putString(alarmTimeName, alarmTime);
        editor.apply();
    }

    // 获得是否需要通知栏学习
    public static boolean getIsNotifyLearn() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        isNotifyLearn = preferences.getBoolean(isNotifyLearnName, false);
        return isNotifyLearn;
    }

    // 设置是否需要通知栏学习
    public static void setIsNotifyLearn(boolean isNotifyLearn) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isNotifyLearnName, isNotifyLearn);
        editor.apply();
    }

    // 获得通知栏的学习模式
    public static int getNotifyLearnMode() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        notifyLearnMode = preferences.getInt(notifyLearnModeName, NotifyLearnService.ALL_MODE);
        return notifyLearnMode;
    }

    // 设置通知栏的学习模式
    public static void setNotifyLearnMode(int notifyLearnMode) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(notifyLearnModeName, notifyLearnMode);
        editor.apply();
    }

    // 获得单词速记的数量
    public static int getSpeedNum() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        speedNum = preferences.getInt(speedNumName, 10);
        return speedNum;
    }

    // 设置单词速记的数量
    public static void setSpeedNum(int speedNum) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(speedNumName, speedNum);
        editor.apply();
    }

    // 获得单词匹配的数量
    public static int getMatchNum() {
        SharedPreferences preferences = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE);
        matchNum = preferences.getInt(matchNumName, 5);
        return matchNum;
    }

    // 设置单词匹配的数量
    public static void setMatchNum(int matchNum) {
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences(SharedDataName, Context.MODE_PRIVATE).edit();
        editor.putInt(matchNumName, matchNum);
        editor.apply();
    }
}
