package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.NumberController;

public class AboutActivity extends BaseActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tv_name = findViewById(R.id.text_about_app_name);
        TextView tv_version = findViewById(R.id.text_about_version);
        TextView tv_content = findViewById(R.id.text_about_content);
        ImageView imageHome = findViewById(R.id.about_img_home);
        imageHome.setOnClickListener(view -> onBackPressed());

        tv_name.setText(getAppName(MyApplication.getContext()));

        tv_version.setText("版本: " + getAppVersionName(AboutActivity.this) +
                "(" + getAppVersionCode(AboutActivity.this) + ")");

        tv_content.setText(ExternalData.sayings[NumberController.getRandomNumber(0, ExternalData.sayings.length - 1)]);
    }

    public static String getAppName(Context context) {
        if (context == null) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        return String.valueOf(packageManager.getApplicationLabel(context.getApplicationInfo()));
    }


    public static String getAppVersionName(Context context) {
        String versionName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versioncode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode + "";
    }
}