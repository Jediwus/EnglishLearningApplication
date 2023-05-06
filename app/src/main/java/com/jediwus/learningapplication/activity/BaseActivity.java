package com.jediwus.learningapplication.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.DailyData;
import com.jediwus.learningapplication.gson.JsonBingPic;
import com.jediwus.learningapplication.gson.JsonDailyQuot;
import com.jediwus.learningapplication.myInterface.PermissionListener;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.HttpHelper;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private PermissionListener mListener;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, getClass().getSimpleName());
        // 根据昼夜模式改变状态栏颜色
        int nightModeFlags = this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // 沉浸式状态栏，黑夜模式设置成浅色
            ImmersionBar.with(this)
                    .statusBarDarkFont(false)
                    .init();
        } else {
            // 沉浸式状态栏，白昼模式设置成深色
            ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .init();
        }
        ActivityCollector.addActivity(this);
        // 防止输入法将布局顶上去
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    /**
     * APP授予权限相关处理
     *
     * @param permissions String[]
     * @param listener    PermissionListener
     */
    public void requestRunPermission(String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionLists = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLists.add(permission);
            }
        }
        if (!permissionLists.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionLists.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        } else {
            //表示全都授权了
            mListener.onGranted();
        }
    }

    /**
     * 权限授予处理的回调方法
     *
     * @param requestCode  int
     * @param permissions  String[]
     * @param grantResults int[]
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    // 存放没授权的权限
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        // 表示已经全部授权
                        mListener.onGranted();
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            case 2:
            default:
                break;
        }
    }

    /**
     * 开启每日数据监测
     */
    public static void prepareDailyData() {
        long currentDate = TimeController.getCurrentDateStamp();
        List<DailyData> dailyDataList = LitePal.where("dayTime = ?", currentDate + "").find(DailyData.class);

        if (dailyDataList.isEmpty()) {
            analyseJsonAndSave();
        } else {
            if (dailyDataList.get(0).getPicVertical() == null ||
                    dailyDataList.get(0).getPicHorizontal() == null ||
                    dailyDataList.get(0).getDailyEn() == null ||
                    dailyDataList.get(0).getDailyChs() == null) {
                analyseJsonAndSave();
            }
        }
    }

    /**
     * 对json数据的分析和存储
     */
    public static void analyseJsonAndSave() {
        String dailyCh;
        String dailyEn;
        String result, picUrl, json;
        // 重置LitePal数据
        LitePal.deleteAll(DailyData.class);
        DailyData dailyData = new DailyData();
        json = HttpHelper.requestResult(ExternalData.IMG_API);
        Log.d(TAG, "每日一图数据" + json);
        Gson gsonPic = new Gson();
        JsonBingPic bingPic = gsonPic.fromJson(json, JsonBingPic.class);
        picUrl = ExternalData.IMG_API_BEFORE + bingPic.getImages().get(0).getUrl();
        if (picUrl.contains("1920x1080")) {
            result = picUrl.replace("1920x1080", "1080x1920");
        } else {
            result = picUrl;
        }
        json = HttpHelper.requestResult(ExternalData.DAILY_SENTENCE_API);
        Gson gsonQuot = new Gson();
        JsonDailyQuot dailyQuot = gsonQuot.fromJson(json, JsonDailyQuot.class);
        dailyCh = dailyQuot.getNote();
        dailyEn = dailyQuot.getContent();
        dailyData.setPicHorizontal(picUrl);
        dailyData.setPicVertical(result);
        dailyData.setDailyEn(dailyEn);
        dailyData.setDailyChs(dailyCh);
        dailyData.setDailySound(dailyQuot.getTts());
        dailyData.setDayTime(TimeController.getCurrentDateStamp() + "");
        dailyData.save();
    }

    /**
     * 界面动画
     */
    public void explosionAnimation() {
        getWindow().setEnterTransition(new Explode().setDuration(300));
        getWindow().setExitTransition(new Explode().setDuration(300));
        getWindow().setReenterTransition(new Explode().setDuration(300));
        getWindow().setReturnTransition(new Explode().setDuration(300));
    }

    /**
     * 检测是否有服务开启
     *
     * @param context   Context
     * @param className String
     * @return boolean
     */
    public static boolean isServiceOn(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
