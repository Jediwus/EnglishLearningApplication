package com.jediwus.learningapplication.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jediwus.learningapplication.database.DailyData;
import com.jediwus.learningapplication.config.PersistentData;
import com.jediwus.learningapplication.gson.BingPic;
import com.jediwus.learningapplication.gson.DailyQuot;
import com.jediwus.learningapplication.model.PermissionListener;
import com.jediwus.learningapplication.util.ActivityCollector;
import com.jediwus.learningapplication.util.HttpHelper;
import com.jediwus.learningapplication.util.TimeController;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private PermissionListener mListener;

    private static final int PERMISSION_REQUESTCODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, getClass().getSimpleName());
        // 沉浸式状态栏
        ImmersionBar.with(this)
                    .init();
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
                    permissionLists.toArray(new String[permissionLists.size()]), PERMISSION_REQUESTCODE);
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
            case PERMISSION_REQUESTCODE:
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
                        // 说明都授权了
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
        json = HttpHelper.requestResult(PersistentData.IMG_API);
        Log.d(TAG, "每日一图数据" + json);
        Gson gsonPic = new Gson();
        BingPic bingPic = gsonPic.fromJson(json, BingPic.class);
        picUrl = PersistentData.IMG_API_BEFORE + bingPic.getImages().get(0).getUrl();
        Log.d(TAG, "获取水平图片url: " + picUrl);
        if (picUrl.contains("1920x1080")) {
            result = picUrl.replace("1920x1080", "1080x1920");
        } else {
            result = picUrl;
        }
        Log.d(TAG, "获取垂直图片url: " + result);
        json = HttpHelper.requestResult(PersistentData.DAILY_SENTENCE_API);
        Gson gsonQuot = new Gson();
        DailyQuot dailyQuot = gsonQuot.fromJson(json, DailyQuot.class);
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

    public void windowFade() {
        getWindow().setEnterTransition(new Fade().setDuration(500));
        getWindow().setExitTransition(new Fade().setDuration(500));
        getWindow().setReenterTransition(new Fade().setDuration(500));
        getWindow().setReturnTransition(new Fade().setDuration(500));
    }

    public void windowSlide(int position) {
        getWindow().setEnterTransition(new Slide(position).setDuration(300));
        getWindow().setExitTransition(new Slide(position).setDuration(300));
        getWindow().setReenterTransition(new Slide(position).setDuration(300));
        getWindow().setReturnTransition(new Slide(position).setDuration(300));
    }

    public void windowExplode() {
        getWindow().setEnterTransition(new Explode().setDuration(300));
        getWindow().setExitTransition(new Explode().setDuration(300));
        getWindow().setReenterTransition(new Explode().setDuration(300));
        getWindow().setReturnTransition(new Explode().setDuration(300));
    }

    public static boolean isServiceExisted(Context context, String className) {

        return true;
    }

}
