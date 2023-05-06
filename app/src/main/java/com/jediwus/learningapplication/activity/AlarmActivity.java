package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.google.android.material.timepicker.MaterialTimePicker;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.broadcast.AlarmReceiver;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.NotifyManagerUtils;

import java.util.Calendar;
import java.util.Objects;

public class AlarmActivity extends BaseActivity {

    private MaterialSwitch materialSwitch;
    private RelativeLayout layout_settings;
    private LinearLayout layout_detail;
    private TextView tv_time;
    private AlertDialog dialog;
    private boolean isEnabled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        materialSwitch = findViewById(R.id.switch_alarm);
        layout_settings = findViewById(R.id.layout_alarm_settings);
        tv_time = findViewById(R.id.text_alarm_settings_time);
        layout_detail = findViewById(R.id.layout_alarm_settings_detail);
        ImageView img_home = findViewById(R.id.img_alarm_home);
        img_home.setOnClickListener(view -> onBackPressed());

        materialSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked && !isEnabled) {
                // 功能开关打开了，但是通知没开
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AlarmActivity.this);
                builder.setTitle("权限需要")
                        .setMessage("\t\t\t该操作需要您开启应用通知权限，要继续吗?")
                        .setPositiveButton("去设置", (dialogInterface, i) -> {
                            NotifyManagerUtils.openNotificationSettingsForApp(AlarmActivity.this);
                            DataConfig.setIsAlarmOn(true);
                        })
                        .setNegativeButton("不了", (dialogInterface, i) -> DataConfig.setIsAlarmOn(false));
                dialog = builder.create();
                dialog.show();
            } else if (isChecked) {
                // 功能开关打开了，通知也开了
                DataConfig.setIsAlarmOn(true);
            } else {
                // 功能开关关闭
                DataConfig.setIsAlarmOn(false);
                // 停用闹钟
                stopAlarm();
            }
            // 在这里添加 updateView 以减少调用
            updateView();
        });

        layout_settings.setOnClickListener(view -> {
            int hour;
            int minute;
            final Calendar calendar = Calendar.getInstance();
            if (!Objects.equals(DataConfig.getAlarmTime(), "")) {
                // 获取设定时间
                hour = Integer.parseInt(DataConfig.getAlarmTime().split("-")[0]);
                minute = Integer.parseInt(DataConfig.getAlarmTime().split("-")[1]);
            } else {
                // 获取当前时间
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);
            }

            MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                    .setTitleText("选择发起提醒的时间")
                    .setHour(hour)
                    .setMinute(minute)
                    .setPositiveButtonText("决定好了")
                    .setNegativeButtonText("取消")
                    .build();


            materialTimePicker.addOnPositiveButtonClickListener(viewTimePicker -> {
                startAlarm(materialTimePicker.getHour(), materialTimePicker.getMinute(), true);
                updateView();
            });

            materialTimePicker.show(getSupportFragmentManager(), "timePicker");
        });

    }

    /**
     * 更新视图
     */
    @SuppressLint("SetTextI18n")
    private void updateView() {
        // 检测是否开启系统通知
        NotificationManagerCompat notification = NotificationManagerCompat.from(AlarmActivity.this);
        isEnabled = notification.areNotificationsEnabled();
        // 设置的时间文本设置
        if (!Objects.equals(DataConfig.getAlarmTime(), "")) {
            int hour = Integer.parseInt(DataConfig.getAlarmTime().split("-")[0]);
            int minute = Integer.parseInt(DataConfig.getAlarmTime().split("-")[1]);
            if (minute < 10) {
                tv_time.setText("每天 " + hour + ":0" + minute);
            } else {
                tv_time.setText("每天 " + hour + ":" + minute);
            }
        } else {
            tv_time.setText("暂未设置");
        }
        // 扩展的显隐
        if (DataConfig.getIsAlarmOn() && isEnabled) {
            materialSwitch.setChecked(true);
            layout_settings.setVisibility(View.VISIBLE);
            layout_detail.setVisibility(View.VISIBLE);
        } else {
            materialSwitch.setChecked(false);
            layout_detail.setVisibility(View.GONE);
            layout_settings.setVisibility(View.GONE);
        }
    }

    /**
     * 开启闹钟
     *
     * @param hour    int 小时
     * @param minute  int 分钟
     * @param showTip boolean 显示Toast
     */
    public static void startAlarm(int hour, int minute, boolean showTip) {
        // 意图设置 AlarmReceiver
        Intent intent = new Intent(MyApplication.getContext(), AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_ALARM);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(ALARM_SERVICE);

        // Set the alarm to start at 设置的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // ！！！！！！！！将信息写入配置中 ！！！！！！！！！！
        DataConfig.setAlarmTime(hour + "-" + minute);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        // 以这种方式启用接收器后，即使用户重启设备，它也会保持启用状态。也就是说，即使在设备重新启动后，
        // 以编程方式启用接收器也会覆盖清单设置。接收器将保持启用状态，直到您的应用将其停用。
        ComponentName receiver = new ComponentName(MyApplication.getContext(), AlarmReceiver.class);
        PackageManager pm = MyApplication.getContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        // Toast 处理
        if (showTip) {
            Toast.makeText(MyApplication.getContext(), "将于每日 " + hour + " 时 " + minute + " 分发送通知", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 关闭闹钟
     */
    public void stopAlarm() {
        // 意图设置 AlarmReceiver
        Intent intent = new Intent(MyApplication.getContext(), AlarmReceiver.class);
        intent.setAction(AlarmReceiver.ACTION_ALARM);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, intent, PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(ALARM_SERVICE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        ComponentName receiver = new ComponentName(MyApplication.getContext(), AlarmReceiver.class);
        PackageManager pm = MyApplication.getContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        // ！！！！！！！！将设定时间信息写入配置中 ！！！！！！！！！！
        DataConfig.setAlarmTime("");
//        WorkManager.getInstance(AlarmActivity.this).cancelAllWorkByTag("simple");
        Toast.makeText(MyApplication.getContext(), "闹钟功能已关闭", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 更新视图
        updateView();
    }


}