package com.jediwus.learningapplication.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.User;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.tencent.tauth.Tencent;

import org.litepal.LitePal;

import java.util.List;

public class LoginActivity extends BaseActivity {

    private final int SUCCESS = 1;
    private final int FAILED = 2;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FAILED:
                    Toast.makeText(LoginActivity.this, "登录失败，请检查网络状态", Toast.LENGTH_SHORT).show();
                    break;
                case SUCCESS:
                    ActivityCollector.startOtherActivity(LoginActivity.this, ChooseWordBookActivity.class);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LinearLayout layout = findViewById(R.id.linear_login);
        MaterialButton btn_login_qq = findViewById(R.id.btn_login_qq);
        MaterialButton btn_login_local = findViewById(R.id.btn_login_local);
        // 渐变动画
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(2000);
        layout.startAnimation(animation);

        btn_login_local.setOnClickListener(view -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this);
            builder.setTitle("安全提醒")
                    .setMessage("本软件将收集用户ID、昵称、头像三个必要信息，仅作为标识，不会泄露您的个人隐私，请放心使用！")
                    .setPositiveButton("继续", (dialogInterface, i) -> initLoginData())
                    .setNegativeButton("取消", null)
                    .setCancelable(false)
                    .show();
        });

        btn_login_qq.setOnClickListener(view -> {

        });

    }

    private void initLoginData() {
        int id = 1;
        String name = "骑士-小光";
        String img = "";
        List<User> users = LitePal.where("userId = ?", id + "").find(User.class);
        if (users.isEmpty()) {
            User user = new User();
            user.setUserName(name);
            user.setUserProfile(img);
            user.setUserId(id);
            // 测试
            user.setUserCoins(0);
            user.setUserVocabulary(0);
            user.save();
        }
        // 查询在用户偏好设置表中，是否存在该用户，若没有，则新建数据
        List<UserPreference> userPreferences = LitePal.where("userId = ?", id + "").find(UserPreference.class);
        if (userPreferences.isEmpty()) {
            UserPreference userPreference = new UserPreference();
            userPreference.setUserId(id);
            userPreference.setCurrentBookId(-1);
            userPreference.save();
        }
        // 默认已登录并设置已登录的ID
        DataConfig.setIsLogged(true);
        DataConfig.setWeChatNumLogged(id);
        Message message = new Message();
        message.what = SUCCESS;
        handler.sendMessage(message);

    }

    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LoginActivity.this);
        builder.setTitle("精灵的提醒")
                .setMessage("\n您确定要退出吗?")
                .setPositiveButton("确定", (dialogInterface, i) -> ActivityCollector.finishAll())
                .setNegativeButton("取消", null)
                .show();
    }
}