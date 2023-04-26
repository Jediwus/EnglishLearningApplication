package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.menu.FragmentWord;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.List;

public class PlanActivity extends BaseActivity {

    // 修改选择项
    private final String[] editOption = {"更换词书", "修改每日新学数量", "重置词书学习记录"};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        // 界面初始化
        ImageView imgBook = findViewById(R.id.img_show_plan_book);
        TextView tv_bookName = findViewById(R.id.text_show_plan_book_name);
        TextView tv_totalNumber = findViewById(R.id.text_show_plan_total_number);
        TextView tv_dailyNumber = findViewById(R.id.text_plan_numbers_of_daily_words);
        TextView tv_predict = findViewById(R.id.text_plan_predict);
        MaterialButton bt_changePlan = findViewById(R.id.btn_change_plan);

        // 获取用户偏好设置
        List<UserPreference> userPreferenceList =
                LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                        .find(UserPreference.class);
        // 获取词书编号
        int bookId = userPreferenceList.get(0).getCurrentBookId();

        // 加载词书图片
        Glide.with(this).load(ExternalData.getBookPicById(bookId)).into(imgBook);

        // 获取词书配置信息
        int totalNumber = ExternalData.getWordsTotalNumbersById(bookId);
        int dailyNumber = userPreferenceList.get(0).getWordNeedReciteNum();
        tv_bookName.setText(ExternalData.getBookNameById(bookId));
        tv_totalNumber.setText("词汇总量: " + totalNumber);
        tv_dailyNumber.setText("每日任务: " + dailyNumber + "词");

        // 预估学习天数
        int predictDays = totalNumber / dailyNumber + 1;
        tv_predict.setText("预计于" + TimeController.getDayAgoOrAfterString(predictDays) + "初学完所有单词");

        // 修改计划按钮点击事件
        bt_changePlan.setOnClickListener(viewChangePlan -> {
            MaterialAlertDialogBuilder optionsBuilder = new MaterialAlertDialogBuilder(PlanActivity.this);
            optionsBuilder.setTitle("选择修改项");
            optionsBuilder.setSingleChoiceItems(editOption, -1, (dialogInterface, i) -> {
                // 延迟 500 毫秒取消对话框
                new Handler().postDelayed(() -> {
                    dialogInterface.dismiss();
                    FragmentWord.initFlag = 0;
                    switch (i) {
                        case 0:
                            ActivityCollector.startOtherActivity(PlanActivity.this, ChooseWordBookActivity.class);
                            break;
                        case 1:
                            Intent intent = new Intent(PlanActivity.this, LearningPlanActivity.class);
                            intent.putExtra(DataConfig.UPDATE_NAME, DataConfig.isUpdate);
                            startActivity(intent);
                            break;
                        case 2:
                            MaterialAlertDialogBuilder tipBuilder = new MaterialAlertDialogBuilder(PlanActivity.this);
                            tipBuilder.setTitle("精灵贴士")
                                    .setMessage("如若确认，您对该词书单词的掌握情况一律恢复至未学习状态，" +
                                            "但对单词详细数据，如释义词组等所做的修改依旧保留。" +
                                            "通俗来说就是清除存档，关卡重开，适用于词书通关玩家☺")
                                    .setPositiveButton("确定", (dialogInterface1, i1) -> {
                                        Word word = new Word();
                                        word.setToDefault("isCollected");
                                        word.setToDefault("isEasy");
                                        word.setToDefault("justLearned");
                                        word.setToDefault("isNeededToLearn");
                                        word.setToDefault("dateNeededToLearn");
                                        word.setToDefault("dateNeededToReview");
                                        word.setToDefault("haveLearned");
                                        word.setToDefault("examNum");
                                        word.setToDefault("examRightNum");
                                        word.setToDefault("lastMasterTime");
                                        word.setToDefault("lastReviewTime");
                                        word.setToDefault("masterDegree");
                                        word.setToDefault("deepMasterTimes");
                                        UserPreference userPreference = new UserPreference();
                                        userPreference.setToDefault("lastStartTime");
                                        userPreference.updateAll();
                                        word.updateAll();
                                        Toast.makeText(PlanActivity.this, "重置成功", Toast.LENGTH_SHORT).show();
                                        dialogInterface1.dismiss();
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();

                    }
                }, 500);
            }).show();
        });



    }




}