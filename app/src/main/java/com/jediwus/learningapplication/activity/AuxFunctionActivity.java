package com.jediwus.learningapplication.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.NotifyManagerUtils;
import com.jediwus.learningapplication.service.NotificationService;

import org.litepal.LitePal;

public class AuxFunctionActivity extends BaseActivity {
    private static final String TAG = "AuxFunctionActivity";

    private final String[] range = {
            "词书大纲(乱序)",
            "生词本词汇",
            "已学词汇",
    };

    private MaterialSwitch materialSwitch;
    private LinearLayout layout_detail;
    private RelativeLayout layout_range;
    private boolean isEnabled;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aux_function);

        materialSwitch = findViewById(R.id.switch_notification);
        layout_detail = findViewById(R.id.layout_switch_notification_detail);
        layout_range = findViewById(R.id.layout_notification_range);
        TextView tv_range = findViewById(R.id.text_notification_range);
        ImageView img_home = findViewById(R.id.img_aux_home);
        img_home.setOnClickListener(view -> onBackPressed());

        // 更新视图
//        updateView();

        tv_range.setText(range[DataConfig.getRangeMode()]);

        materialSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked && !isEnabled) {
                // 功能开关打开了，但是通知没开
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AuxFunctionActivity.this);
                builder.setTitle("权限需要")
                        .setMessage("\t\t\t该操作需要您开启应用通知权限，要继续吗?")
                        .setPositiveButton("去设置", (dialogInterface, i) -> {
                            NotifyManagerUtils.openNotificationSettingsForApp(AuxFunctionActivity.this);
                            DataConfig.setIsNotificationOn(true);
                            layout_detail.setVisibility(View.VISIBLE);
                            layout_range.setVisibility(View.VISIBLE);
                            initDefaultMode();
//                            startMyService(DataConfig.getRangeMode());
                            Log.d(TAG, "onCreate: 开启服务——Dialog，并入下面的updateView");
                        })
                        .setNegativeButton("不了", (dialogInterface, i) -> {
                            DataConfig.setIsNotificationOn(false);
                            layout_detail.setVisibility(View.GONE);
                            layout_range.setVisibility(View.GONE);
//                            stopMyService();
                            Log.d(TAG, "onCreate: 关闭服务——Dialog，并入下面的updateView");
                        });
                dialog = builder.create();
                dialog.show();
                // 在这里添加 updateView 以减少调用
                updateView();
            } else if (isChecked) {
                // 功能开关打开了，通知也开了
                DataConfig.setIsNotificationOn(true);
                layout_detail.setVisibility(View.VISIBLE);
                layout_range.setVisibility(View.VISIBLE);
                initDefaultMode();
                startMyService(DataConfig.getRangeMode());
                Log.d(TAG, "onCreate——materialSwitch检测: 开启服务");
            } else {
                // 功能开关关闭
                DataConfig.setIsNotificationOn(false);
                layout_detail.setVisibility(View.GONE);
                layout_range.setVisibility(View.GONE);
                stopMyService();
                Log.d(TAG, "onCreate——materialSwitch检测: 关闭服务");
            }
        });

        layout_range.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AuxFunctionActivity.this);
            builder.setSingleChoiceItems(range, DataConfig.getRangeMode(), (dialogInterface, which) -> {
                        // 延迟500毫秒取消对话框
                        new Handler().postDelayed(() -> {
                            dialogInterface.dismiss();
                            // 更换词书范围
                            if (DataConfig.getRangeMode() != which) {
                                switch (which) {
                                    case NotificationService.ALL_WORD_MODE:
                                        DataConfig.setRangeMode(NotificationService.ALL_WORD_MODE);
                                        startMyService(NotificationService.ALL_WORD_MODE);
                                        Toast.makeText(AuxFunctionActivity.this, "选词范围已更新", Toast.LENGTH_SHORT).show();
                                        tv_range.setText(range[NotificationService.ALL_WORD_MODE]);
                                        break;

                                    case NotificationService.STARED_WORD_MODE:
                                        if (!LitePal.where("isCollected = ?", 1 + "").find(Word.class).isEmpty()) {
                                            DataConfig.setRangeMode(NotificationService.STARED_WORD_MODE);
                                            startMyService(NotificationService.STARED_WORD_MODE);
                                            Toast.makeText(AuxFunctionActivity.this, "选词范围已更新", Toast.LENGTH_SHORT).show();
                                            tv_range.setText(range[NotificationService.STARED_WORD_MODE]);
                                        } else {
                                            Toast.makeText(AuxFunctionActivity.this, "很抱歉！您尚未设置过生词哦", Toast.LENGTH_SHORT).show();
                                        }
                                        break;

                                    case NotificationService.KNOWN_WORD_MODE:
                                        if (!LitePal.where("haveLearned = ?", 1 + "").find(Word.class).isEmpty()) {
                                            DataConfig.setRangeMode(NotificationService.KNOWN_WORD_MODE);
                                            startMyService(NotificationService.KNOWN_WORD_MODE);
                                            Toast.makeText(AuxFunctionActivity.this, "选词范围已更新", Toast.LENGTH_SHORT).show();
                                            tv_range.setText(range[NotificationService.KNOWN_WORD_MODE]);
                                        } else {
                                            Toast.makeText(AuxFunctionActivity.this, "很抱歉！您尚未经历过每日任务哦", Toast.LENGTH_SHORT).show();
                                        }
                                        break;
                                }
                            }
                        }, 500);
                    }).setTitle("选词范围")
                    .show();
        });

    }

    /**
     * 更新视图
     */
    private void updateView() {
        // 检测是否开启系统通知
        NotificationManagerCompat notification = NotificationManagerCompat.from(AuxFunctionActivity.this);
        isEnabled = notification.areNotificationsEnabled();
        if (DataConfig.getIsNotificationOn() && isEnabled) {
            materialSwitch.setChecked(true);
            layout_detail.setVisibility(View.VISIBLE);
            layout_range.setVisibility(View.VISIBLE);
            // 默认模式的单词范围
            initDefaultMode();
            if (!BaseActivity.isServiceOn(MyApplication.getContext(), NotificationService.class.getName())) {
                startMyService(DataConfig.getRangeMode());
                Log.d(TAG, "onStart: 开启服务——updateView");
            }
        } else {
            materialSwitch.setChecked(false);
            layout_detail.setVisibility(View.GONE);
            layout_range.setVisibility(View.GONE);
            stopMyService();
            Log.d(TAG, "onStart: 关闭服务——updateView");
        }
    }

    /**
     * 开启服务
     *
     * @param mode int
     */
    public static void startMyService(int mode) {
        NotificationService.currentMode = mode;
        Intent intent = new Intent(MyApplication.getContext(), NotificationService.class);
        if (BaseActivity.isServiceOn(MyApplication.getContext(), NotificationService.class.getName())) {
            stopMyService();
            Log.d(TAG, "startMyService: 关闭之前的服务以开启新的服务");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MyApplication.getContext().startForegroundService(intent);
        } else {
            MyApplication.getContext().startService(intent);
        }
    }

    /**
     * 关闭服务
     */
    public static void stopMyService() {
        Intent intent = new Intent(MyApplication.getContext(), NotificationService.class);
        MyApplication.getContext().stopService(intent);
    }

    /**
     * 设置默认值——全部词汇
     */
    public static void initDefaultMode() {
        switch (DataConfig.getRangeMode()) {
            case NotificationService.KNOWN_WORD_MODE:
                if (LitePal.where("haveLearned = ?", 1 + "").find(Word.class).isEmpty())
                    DataConfig.setRangeMode(NotificationService.ALL_WORD_MODE);
                break;
            case NotificationService.STARED_WORD_MODE:
                if (LitePal.where("isCollected = ?", 1 + "").find(Word.class).isEmpty())
                    DataConfig.setRangeMode(NotificationService.ALL_WORD_MODE);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //  更新视图
        updateView();
    }
}