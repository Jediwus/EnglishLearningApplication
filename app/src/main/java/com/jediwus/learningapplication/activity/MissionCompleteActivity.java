package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.User;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.LearningController;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MissionCompleteActivity extends BaseActivity {

    private TextInputEditText editMemo;

    private List<UserPreference> userPreferenceList;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_complete);

        windowExplode();

        TextView textWordNumber = findViewById(R.id.text_mission_complete_word_num);
        TextView textDay = findViewById(R.id.text_mission_complete_days);
        editMemo = findViewById(R.id.edit_text_mission_complete_feelings);
        MaterialButton btnOk = findViewById(R.id.btn_mission_complete_ok);

        userPreferenceList = LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);

        int wordNumber = userPreferenceList.get(0).getWordNeedReciteNum() + LearningController.wordNeedReciteNumber;
        textWordNumber.setText(wordNumber + "");

        List<MyDate> myDateList = LitePal.findAll(MyDate.class);
        textDay.setText((myDateList.size() + 1) + "");

        btnOk.setOnClickListener(view -> onBackPressed());

    }

    private void saveTodayData() {
        Calendar calendar = Calendar.getInstance();
        // 查询该用户今天的日期数据
        List<MyDate> myDates = LitePal
                .where("year = ? and month = ? and date = ? and userId = ?",
                        calendar.get(Calendar.YEAR) + "",
                        (calendar.get(Calendar.MONTH) + 1) + "",
                        calendar.get(Calendar.DATE) + "",
                        DataConfig.getWeChatNumLogged() + "")
                .find(MyDate.class);
        if (myDates.isEmpty()) {
            // 若无今日的数据，新创建今日数据
            createTodayData();
        } else {
            // 删掉已有的今日数据，检测今日数据有多少行受影响
            int result = LitePal
                    .deleteAll("year = ? and month = ? and date = ? and userId = ?",
                            calendar.get(Calendar.YEAR) + "",
                            (calendar.get(Calendar.MONTH) + 1) + "",
                            calendar.get(Calendar.DATE) + "",
                            DataConfig.getWeChatNumLogged() + "");
            // 若有行数受影响
            if (result != 0) {
                // 新创建今日数据
                createTodayData();
            }
        }
    }

    /**
     * 新创建今日数据
     */
    private void createTodayData() {
        String[] str = TimeController.getStringDate(TimeController.todayDate).split("-");
        MyDate myDate = new MyDate();
        myDate.setWordLearnNumber(userPreferenceList.get(0).getWordNeedReciteNum());
        myDate.setWordReviewNumber(LearningController.wordNeedReciteNumber);
        myDate.setYear(Integer.parseInt(str[0]));
        myDate.setMonth(Integer.parseInt(str[1]));
        myDate.setDate(Integer.parseInt(str[2]));
        myDate.setUserId(DataConfig.getWeChatNumLogged());
        if (!Objects.requireNonNull(editMemo.getText()).toString().trim().isEmpty()) {
            myDate.setRemark(editMemo.getText().toString());
        }
        myDate.save();
        // 10金币的获得和用户词汇量修改
        List<User> userList = LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "").find(User.class);
        User user = new User();
        user.setUserCoins(userList.get(0).getUserCoins() + 10);
        user.setUserVocabulary(userList.get(0).getUserVocabulary() + userPreferenceList.get(0).getWordNeedReciteNum());
        user.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");
    }

    @Override
    public void onBackPressed() {
        saveTodayData();
        ActivityCollector.startOtherActivity(MissionCompleteActivity.this, MainActivity.class);
    }

}