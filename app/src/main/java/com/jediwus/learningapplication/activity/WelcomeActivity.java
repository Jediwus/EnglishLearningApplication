package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.DailyData;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.model.PermissionListener;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.MyPopupWindow;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.List;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WelcomeActivity";
    // 壁纸
    private ImageView imgBackground;
    // 每日一句文字
    private TextView textWelCome;
    // 弹出视图
    private MyPopupWindow welcomeWindow;
    // 缩放动画
    private ScaleAnimation animation;

    private final int MESSAGE_TYPE_1 = 1;

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
                case 2:
                    // 处理类型为2的消息
                    break;
                // 可以根据实际需求添加更多的case分支
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置权限弹出框
        welcomeWindow = new MyPopupWindow(this);
        welcomeWindow.setContentView(R.layout.item_welcome_introduction);

        setContentView(R.layout.activity_welcome);
        // 防止重复
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            ActivityCollector.removeActivity(this);
            finish();
            return;
        }

        textWelCome = findViewById(R.id.text_welcome);
        imgBackground = findViewById(R.id.img_welcome_bg);
        // 弹出-同意按钮
        CardView cardAgree = welcomeWindow.findViewById(R.id.card_agree);
        cardAgree.setOnClickListener(this);
        // 弹出-不同意按钮
        TextView textNotAgree = welcomeWindow.findViewById(R.id.text_disagree);
        textNotAgree.setOnClickListener(this);
        animationConfig();

        new Thread(() -> {
            prepareDailyData();
            Message message = new Message();
            message.what = MESSAGE_TYPE_1;
            handler.sendMessage(message);
            //BaiduHelper.getAssessToken();
        }).start();

        // 如果是第一次运行，设置权限提示框的动画
        if (DataConfig.getIsFirst()) {
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
        animation.setDuration(3000);
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
                // 登录检测
                if (DataConfig.getIsLogged()) {  // 已登录，进入首页/选择词书

                    List<UserPreference> userPreferences = LitePal.where("userId = ?",
                            DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);

                    Log.d(TAG, "onAnimationEnd: 用户所选词书为：" + userPreferences.get(0).getCurrentBookId());

                    if (userPreferences.get(0).getCurrentBookId() == -1) {
                        // 跳转到选择词书活动 ChooseWordBookActivity
                        Intent intent = new Intent(WelcomeActivity.this, ChooseWordBookActivity.class);
                        startActivity(intent);
                    } else if (userPreferences.get(0).getCurrentBookId() != -1 &&
                            userPreferences.get(0).getWordNeedReciteNum() == 0) {
                        // 跳转到更改选择计划活动 LearningPlanActivity
                        Intent intent = new Intent(WelcomeActivity.this, LearningPlanActivity.class);
                        startActivity(intent);
                    } else {
                        // 后台更新登录时间
                        new Thread(() -> updateServerData()).start();
                        // 跳转到 MainActivity
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {    // 未登录，进入登录页活动 LoginActivity
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

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
                Toast.makeText(this, "无法获取权限，退出应用", Toast.LENGTH_SHORT).show();
                ActivityCollector.finishAll();
                break;
        }
    }

    private void requestPermission() {
        // 设置 PermissionListener 监听
        requestRunPermission(DataConfig.permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                welcomeWindow.dismiss();
                // 设置APP已不是第一次运行
                DataConfig.setIsFirst(false);
                // 延迟一会儿，动画再继续
                new Handler().postDelayed(() -> imgBackground.startAnimation(animation), 500);
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                if (!deniedPermission.isEmpty()) {
                    Toast.makeText(WelcomeActivity.this, "访问权限被拒，为您退出应用", Toast.LENGTH_SHORT).show();
                    ActivityCollector.finishAll();
                }
            }
        });
    }


}