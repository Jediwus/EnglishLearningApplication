package com.jediwus.learningapplication.myUtil;

import android.content.Context;
import android.content.Intent;

public class NotifyManagerUtils {

    /**
     * 打开通知权限
     *
     * @param context Context
     */
    public static void openNotificationSettingsForApp(Context context) {
        // Links to this app's notification settings.
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        // for Android 8 and above
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        context.startActivity(intent);
    }

}
