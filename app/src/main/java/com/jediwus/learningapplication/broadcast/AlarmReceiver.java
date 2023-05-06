package com.jediwus.learningapplication.broadcast;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.AlarmActivity;
import com.jediwus.learningapplication.activity.MainActivity;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.myUtil.MyApplication;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_ALARM = "intent_alarm";
    public static final String BOOT_COMPLETED = "boot_completed";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ACTION_ALARM:
                // 创建一个意图，用于点击通知时打开应用程序的MainActivity
                Intent intentMain = new Intent(context, MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMain, 0);

                NotificationManager manager = (NotificationManager) MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);

                // 创建通知渠道（仅适用于Android 8.0及以上版本）
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 在系统的通知管理器中注册通知渠道
                    manager.createNotificationChannel(new NotificationChannel(ExternalData.channelIdAlarm, ExternalData.channelNameAlarm, NotificationManager.IMPORTANCE_HIGH));
                }

                Notification notification = new NotificationCompat.Builder(MyApplication.getContext(), ExternalData.channelIdAlarm)
                        .setContentTitle("一则来词星的消息")
                        .setContentText("是时候完成你的每日任务啦！")
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getContext().getResources(), R.mipmap.ic_launcher))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .build();

                // 显示通知
                manager.notify(2, notification);
                break;
            case BOOT_COMPLETED:
                int hour = Integer.parseInt(DataConfig.getAlarmTime().split("-")[0]);
                int minute = Integer.parseInt(DataConfig.getAlarmTime().split("-")[1]);
                AlarmActivity.startAlarm(hour, minute, false);

                break;
        }
    }

}
