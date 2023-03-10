package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.ConfigData;
import com.jediwus.learningapplication.database.DailyData;
import com.jediwus.learningapplication.model.PermissionListener;
import com.jediwus.learningapplication.util.ActivityCollector;
import com.jediwus.learningapplication.util.MyPopupWindow;
import com.jediwus.learningapplication.util.TimeController;

import org.litepal.LitePal;

import java.util.List;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WelcomeActivity";
    // 壁纸
    private ImageView imgBackground;
    // 每日一句卡片
    private CardView cardWelCome;
    // 每日一句文字
    private TextView textWelCome;
    // 弹出-同意按钮
    private CardView cardAgree;
    // 弹出-不同意按钮
    private TextView textNotAgree;
    // 弹出视图
    private MyPopupWindow welcomeWindow;
    // 缩放动画
    private ScaleAnimation animation;

    private final int MESSAGE_TYPE_1 = 1;

    private final int MESSAGE_TYPE_2 = 2;

    // Android中通常使用Handler来进行不同线程间的通讯以及消息的异步处理
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MESSAGE_TYPE_1:
                    // 处理类型为1的消息
                    List<DailyData> dailyDataList = LitePal.where("dayTime = ?", TimeController.getCurrentDateStamp() + "").find(DailyData.class);
                    if (!dailyDataList.isEmpty()) {
                        DailyData dailyData = dailyDataList.get(0);
                        textWelCome.setText(dailyData.getDailyEn());
                        Glide.with(WelcomeActivity.this).load(dailyData.getPicVertical()).into(imgBackground);
                    }
                case MESSAGE_TYPE_2:
                    // 处理类型为2的消息
                    break;
                // 可以根据实际需求添加更多的case分支
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        // 防止重复
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            ActivityCollector.removeActivity(this);
            finish();
            return;
        }
        // 设置权限弹出框
        welcomeWindow = new MyPopupWindow(this);
        cardWelCome = findViewById(R.id.card_welcome);
        textWelCome = findViewById(R.id.text_welcome);
        imgBackground = findViewById(R.id.img_welcome_bg);
        cardAgree = welcomeWindow.findViewById(R.id.card_agree);
        cardAgree.setOnClickListener(this);
        textNotAgree = welcomeWindow.findViewById(R.id.text_disagree);
        textNotAgree.setOnClickListener(this);
        animationConfig();
        // 设置透明度
        cardWelCome.getBackground().setAlpha(200);

        new Thread(() -> {
            prepareDailyData();
            Message message = new Message();
            message.what = MESSAGE_TYPE_1;
            handler.sendMessage(message);
            //BaiduHelper.getAssessToken();
        }).start();

        // 如果是第一次运行，设置权限提示框的动画
        if (ConfigData.getIsFirst()) {
            welcomeWindow.setClipChildren(false)
                    .setBlurBackgroundEnable(true)
                    .setOutSideDismiss(false)
                    .showPopupWindow();
        } else {
            new Handler().postDelayed(() -> imgBackground.startAnimation(animation), 500);

            // 学习提醒的开启

            // 通知栏权限获取
        }
        MainActivity.lastFragment = 0;
        MainActivity.needRefresh = true;
    }

    // 缩放动画配置
    private void animationConfig() {
        // 从原图大小逐渐拉近，即放大
        animation = new ScaleAnimation(1, 1.2f, 1, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, 1, 0.5f);
        // 设置持续时间
        animation.setDuration(4000);
        // 设置动画结束之后的状态是否是动画的最终状态
        animation.setFillAfter(true);
        // 设置循环次数
        animation.setRepeatCount(0);
        // 设置动画监听器
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 暂不设置
            }

            // 设置动画结束后事件
            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束后进入首页

                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // 暂不设置
            }
        });
    }

    private void updateServerData() {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_agree:
                // 获取运行权限
                requestPermission();
                break;
            case R.id.text_disagree:
                Toast.makeText(this, "非常抱歉，即将为您退出应用", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
        }
    }

    private void requestPermission() {
        // 设置 PermissionListener 监听
        requestRunPermission(ConfigData.permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                welcomeWindow.dismiss();
                // 设置APP已不是第一次运行
                ConfigData.setIsFirst(false);
                // 延迟一会儿，动画再继续
                new Handler().postDelayed(() -> imgBackground.startAnimation(animation), 500);
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                if (!deniedPermission.isEmpty()) {
                    Toast.makeText(WelcomeActivity.this, "访问权限被拒，应用即将退出", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                }
            }
        });
    }


}