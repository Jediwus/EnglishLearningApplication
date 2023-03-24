package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.FileUtil;
import com.jediwus.learningapplication.myUtil.JsonHelper;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LearningPlanActivity extends BaseActivity {

    private static final String TAG = "LearningPlanActivity";

    private int currentBookId;

    private int maxNum;

    private final int FINISH = 1;
    private final int DOWNLOADED = 2;

    private EditText edit_text;

    private TextView text_book, text_word_maxNum;

    private List<UserPreference> userPreferences;

//    private ProgressDialog progressDialog;

    private Thread thread;

    private AlertDialog dialog;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FINISH:
                    // 等待加载框消失
//                    progressDialog.dismiss();
                    dialog.dismiss();
                    // 重置上次学习时间
                    UserPreference userPreference = new UserPreference();
                    userPreference.setLastStartTime(0);
                    userPreference.setCurrentBookId(currentBookId);
                    userPreference.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");
                    // 删除当天打卡记录
                    Calendar calendar = Calendar.getInstance();
                    LitePal.deleteAll(
                            MyDate.class, "year = ? and month = ? and date = ? and userId = ?",
                            calendar.get(Calendar.YEAR) + "",
                            (calendar.get(Calendar.MONTH) + 1) + "",
                            calendar.get(Calendar.DAY_OF_MONTH) + "",
                            DataConfig.getWeChatNumLogged() + ""
                    );
                    ActivityCollector.startOtherActivity(
                            LearningPlanActivity.this,
                            MainActivity.class
                    );
                    break;
                case DOWNLOADED:
//                    progressDialog.setTitle("Tips");
//                    progressDialog.setMessage("正在解压数据包并导入本地数据库，可能会占用您一些时间，请耐心等待");
                    dialog.setTitle("Tips");
                    dialog.setMessage("正在解压数据包并导入本地数据库，可能会占用您一些时间，请耐心等待");
                    break;
                default:
                    break;
            }
        }
    };


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_plan);

        // 从 WordBookAdapter 传来的 intent
        Intent intent = getIntent();

        edit_text = findViewById(R.id.edit_word_num);
        text_book = findViewById(R.id.text_plan_chosen);
        text_word_maxNum = findViewById(R.id.text_max_word_num);
        Button btn_go = findViewById(R.id.btn_go);
        ImageView img_plan_back = findViewById(R.id.img_plan_back);
        img_plan_back.setOnClickListener(view -> {
            List<UserPreference> userPreferenceList = LitePal.where("userId = ?",
                    DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);
            if (userPreferenceList.get(0).getWordNeedReciteNum() != 0) {
                finish();
            } else {
                ActivityCollector.startOtherActivity(LearningPlanActivity.this, ChooseWordBookActivity.class);
            }
        });
        ImageView img_character = findViewById(R.id.img_plan_character);
        Glide.with(this).load(R.drawable.gif_link).into(img_character);

        // 检查是否已经设置每日单词数量
        userPreferences = LitePal.where("userId = ?",
                DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);
        if (userPreferences.get(0).getWordNeedReciteNum() != 0) {
            edit_text.setText(userPreferences.get(0).getWordNeedReciteNum() + "");

        }
        btn_go.setOnClickListener(view -> {

            if (!edit_text.getText().toString().trim().equals("")) {
                if (Integer.parseInt(edit_text.getText().toString().trim()) >= 5
                        && Integer.parseInt(edit_text.getText().toString().trim()) < maxNum) {

                    // 隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);

                    // 用户偏好数据的最终确定
                    UserPreference userPreference = new UserPreference();
                    userPreference.setWordNeedReciteNum(Integer.parseInt(edit_text.getText().toString().trim()));
                    userPreference.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");

                    // 初次设置词书和单词数
                    if (DataConfig.notUpdate == intent.getIntExtra(DataConfig.UPDATE_NAME, 0)) {
                        // 开启等待框
                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(LearningPlanActivity.this);
                        builder.setTitle("Downloading");
                        builder.setMessage("数据包正在玩命下载中...");
                        ProgressBar progressBar = new ProgressBar(LearningPlanActivity.this);
                        builder.setView(progressBar);
                        builder.setCancelable(false);
                        dialog = builder.create();
                        dialog.show();
//                        progressDialog = new ProgressDialog(LearningPlanActivity.this);
//                        progressDialog.setTitle("Downloading");
//                        progressDialog.setMessage("数据包正在玩命下载中...");
//                        progressDialog.setCancelable(false);
//                        progressDialog.show();

                        // 延迟两秒再运行，防止等待框不显示
                        new Handler().postDelayed(() -> {
                            // 开启线程分析数据
                            thread = new Thread(() -> {
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder()
                                            .url(ExternalData.getBookDownLoadAddressById(currentBookId))
                                            .build();
                                    Response response = client.newCall(request).execute();
                                    Message message = new Message();
                                    message.what = DOWNLOADED;
                                    handler.sendMessage(message);
                                    FileUtil.getFileByBytes(Objects.requireNonNull(response.body()).bytes(),
                                            getFilesDir() + "/" + ExternalData.DIR_STORE,
                                            ExternalData.getBookFileNameById(currentBookId));
                                    FileUtil.unZipFile(getFilesDir() + "/" +
                                                    ExternalData.DIR_STORE + "/" +
                                                    ExternalData.getBookFileNameById(currentBookId)
                                            , getFilesDir() + "/" +
                                                    ExternalData.DIR_STORE + "/" +
                                                    ExternalData.DIR_AFTER_ZIP,
                                            false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JsonHelper.analyseDefaultAndSave(
                                        FileUtil.readJsonData(ExternalData.DIR_STORE + "/" +
                                                ExternalData.DIR_AFTER_ZIP + "/" +
                                                ExternalData.getBookFileNameById(currentBookId).replace(".zip", ".json")
                                        )
                                );
                                Message message = new Message();
                                message.what = FINISH;
                                handler.sendMessage(message);

                            });

                            thread.start();

                        }, 1000);

                    } else {
                        if (userPreferences.get(0).getWordNeedReciteNum() != Integer.parseInt(edit_text.getText().toString().trim())) {
                            // 重置上次学习时间
                            UserPreference userPreference1 = new UserPreference();
                            userPreference1.setLastStartTime(-1);
                            userPreference1.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");
                            Toast.makeText(LearningPlanActivity.this,
                                    "" + LitePal.where("userId = ?",
                                                    DataConfig.getWeChatNumLogged() + "")
                                            .find(UserPreference.class).get(0).getLastStartTime(),
                                    Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onCreate: " + LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "").find(UserPreference.class).get(0).getLastStartTime());
                            // 删除当天打卡记录
                            Calendar calendar = Calendar.getInstance();
                            LitePal.deleteAll(
                                    MyDate.class, "year = ? and month = ? and date = ? and userId = ?",
                                    calendar.get(Calendar.YEAR) + "",
                                    (calendar.get(Calendar.MONTH) + 1) + "",
                                    calendar.get(Calendar.DAY_OF_MONTH) + "",
                                    DataConfig.getWeChatNumLogged() + ""
                            );
                            Toast.makeText(LearningPlanActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LearningPlanActivity.this, "设置未果，仍执行原计划！", Toast.LENGTH_SHORT).show();
                        }
                        ActivityCollector.startOtherActivity(LearningPlanActivity.this, MainActivity.class);
                    }
                } else {
                    Toast.makeText(LearningPlanActivity.this, "请输入范围内的数字！", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LearningPlanActivity.this, "需要设置计划才能继续哦", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();

        List<UserPreference> userPreferenceList = LitePal.where("userId = ?",
                DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);
        currentBookId = userPreferenceList.get(0).getCurrentBookId();
        maxNum = ExternalData.getWordsTotalNumbersById(currentBookId);
        // 设置单词总数和书名
        text_word_maxNum.setText(maxNum + "");
        text_book.setText(ExternalData.getBookNameById(currentBookId));
    }


    @Override
    public void onBackPressed() {
        List<UserPreference> userPreferenceList = LitePal.where("userId = ?",
                DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);
        if (userPreferenceList.get(0).getWordNeedReciteNum() != 0) {
            super.onBackPressed();
        } else {
            ActivityCollector.startOtherActivity(LearningPlanActivity.this, ChooseWordBookActivity.class);
        }
    }
}