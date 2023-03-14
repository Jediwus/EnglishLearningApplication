package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.database.DailyData;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;

public class DailyMottoActivity extends BaseActivity {

    private LinearLayout linearLayout;

    private ImageView imgBackground, imgSound, imgShare, imgExit;

    private TextView textDate, textMonth, textYear, textSentenceEn, textSentenceCh;

    private final int FINISH = 1;

    private AlphaAnimation startAnimation, exitAnimation;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint({"SetTextI18n", "SuspiciousIndentation"})
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == FINISH) {
                List<DailyData> dailyDataList = LitePal.where("dayTime = ?", TimeController.getCurrentDateStamp() + "").find(DailyData.class);
                if (!dailyDataList.isEmpty()) {
                    final DailyData dailyData = dailyDataList.get(0);
                    Glide.with(DailyMottoActivity.this).load(dailyData.getPicVertical()).into(imgBackground);
                    // 设置日期
                    Calendar calendar = Calendar.getInstance();
                    textDate.setText(calendar.get(Calendar.DATE) + "");
                    textYear.setText(calendar.get(Calendar.YEAR) + "");
                    textMonth.setText(getMonthName(calendar));

                    textSentenceEn.setText(dailyData.getDailyEn());
                    textSentenceCh.setText(dailyData.getDailyChs());
                    linearLayout.startAnimation(startAnimation);

                    if (dailyData.getDailySound() != null)
                       imgSound.setOnClickListener(v -> MediaHelper.playInternetSource(dailyData.getDailySound()));

                        imgShare.setOnClickListener(view -> {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, dailyData.getDailyEn() + "\n" + dailyData.getDailyChs());
                            shareIntent = Intent.createChooser(shareIntent, "每日一句");
                            startActivity(shareIntent);
                        });
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_motto);

        // 沉浸式状态栏，字体  设置成浅色
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .init();

        linearLayout = findViewById(R.id.layout_daily_sentence_content);
        imgBackground = findViewById(R.id.img_daily_sentence_show);
        textDate = findViewById(R.id.text_daily_sentence_date);
        textMonth = findViewById(R.id.text_daily_sentence_month);
        textYear = findViewById(R.id.text_daily_sentence_year);
        textSentenceCh = findViewById(R.id.text_sentence_cn);
        textSentenceEn = findViewById(R.id.text_sentence_en);
        imgSound = findViewById(R.id.img_daily_sentence_sound);
        imgShare = findViewById(R.id.img_daily_sentence_share);
        imgExit = findViewById(R.id.img_daily_sentence_exit);

        // 递进动画
        exitAnimation = new AlphaAnimation(1.0f, 0.0f);
        exitAnimation.setDuration(100);
        exitAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                linearLayout.setVisibility(View.GONE);
                imgShare.setVisibility(View.GONE);
                imgExit.setVisibility(View.GONE);
                supportFinishAfterTransition();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startAnimation = new AlphaAnimation(0.0f, 1.0f);
        startAnimation.setDuration(2000);

        // 设置数据显示
        new Thread(() -> {
            prepareDailyData();
            Message message = new Message();
            message.what = FINISH;
            handler.sendMessage(message);
        }).start();

        imgExit.setOnClickListener(view -> linearLayout.startAnimation(exitAnimation));
    }

    /**
     * 设置获得月份缩写
     *
     * @param calendar Calendar
     * @return String
     */
    public static String getMonthName(Calendar calendar) {
        String s = "";
        switch (calendar.get(Calendar.MONTH)) {
            case 0:
                s = "Jan";
                break;
            case 1:
                s = "Feb";
                break;
            case 2:
                s = "Mar";
                break;
            case 3:
                s = "Apr";
                break;
            case 4:
                s = "May";
                break;
            case 5:
                s = "June";
                break;
            case 6:
                s = "July";
                break;
            case 7:
                s = "Aug";
                break;
            case 8:
                s = "Sep";
                break;
            case 9:
                s = "Oct";
                break;
            case 10:
                s = "Nov";
                break;
            case 11:
                s = "Dec";
                break;
        }
        return s;
    }

    @Override
    public void onBackPressed() {
        linearLayout.startAnimation(exitAnimation);
    }
}