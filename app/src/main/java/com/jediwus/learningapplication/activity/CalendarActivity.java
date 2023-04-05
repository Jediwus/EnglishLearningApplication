package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.myUtil.TimeController;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.litepal.LitePal;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends BaseActivity {

    private TextView tv_date;
    private TextView tv_dateInterval;
    private TextView tv_wordNum;
    private TextView tv_memo;
    private TextView tv_sign;
    private LinearLayout layoutMemo;
    private LinearLayout layoutWordNum;
    private CardView cardInfo;
    private ImageView imgSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        MaterialCalendarView materialCalendarView = findViewById(R.id.calendar);
        tv_date = findViewById(R.id.text_calendar_date);
        tv_dateInterval = findViewById(R.id.text_calendar_date_interval);
        tv_wordNum = findViewById(R.id.text_calendar_word_num);
        tv_memo = findViewById(R.id.text_calendar_memo);
        tv_sign = findViewById(R.id.text_calendar_sign);
        layoutMemo = findViewById(R.id.layout_calendar_memo);
        layoutWordNum = findViewById(R.id.layout_calendar_word_num);
        cardInfo = findViewById(R.id.card_calendar_info);
        imgSign = findViewById(R.id.img_calendar_sign);
        ImageView imageHome = findViewById(R.id.calendar_img_home);
        imageHome.setOnClickListener(view -> onBackPressed());

        List<MyDate> myDateList = LitePal
                .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(MyDate.class);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDate = calendar.get(Calendar.DAY_OF_MONTH);

        // 默认选择今天
        materialCalendarView.setDateSelected(CalendarDay.from(currentYear, currentMonth, currentDate), true);
        materialCalendarView.setWeekDayLabels(new String[]{"一", "二", "三", "四", "五", "六", "日"});
        materialCalendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        // 完成任务的装饰
        materialCalendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                for (MyDate myDate : myDateList) {
                    if (day.getDay() == myDate.getDate() &&
                            day.getMonth() == myDate.getMonth() &&
                            day.getYear() == myDate.getYear()) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(
                        10,
                        ContextCompat.getColor(CalendarActivity.this, R.color.colorPrimary)));
            }
        });
        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> updateData(date));

        updateData(CalendarDay.from(currentYear, currentMonth, currentDate));

    }

    /**
     * 更新日期的方法
     *
     * @param date CalendarDay
     */
    @SuppressLint("SetTextI18n")
    private void updateData(CalendarDay date) {
        List<MyDate> myDateList = LitePal
                .where("date = ? and month = ? and year = ? and userId = ?",
                        date.getDay() + "",
                        date.getMonth() + "",
                        date.getYear() + "",
                        DataConfig.getWeChatNumLogged() + "")
                .find(MyDate.class);
        tv_date.setText(date.getYear() + "年" + date.getMonth() + "月" + date.getDay() + "日");

        // 获取所选日期的Calendar对象
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(date.getYear(), date.getMonth() - 1, date.getDay());

        // 获取当前日期的Calendar对象
        Calendar today = Calendar.getInstance();

        // 计算所选日期与当前日期之间的差值（以天为单位）
        int diff = 0;
        try {
            diff = TimeController.daysInternal(
                    TimeController.getCalendarDateStamp(today),
                    TimeController.getCalendarDateStamp(selectedDate)
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 将差值转换为可读字符串
        String diffString;
        if (diff == 0) {
            diffString = "今天";
        } else if (diff > 0) {
            diffString = diff + "天后";
        } else {
            diffString = Math.abs(diff) + "天前";
        }

        // 将差值字符串显示在textView中
        tv_dateInterval.setText(diffString);

//        tv_date.setText(myDateList.get(0).getYear() + "年" + myDateList.get(0).getMonth() + "月" + myDateList.get(0).getDate() + "日");
        if (myDateList.isEmpty()) {
            layoutWordNum.setVisibility(View.GONE);
            layoutMemo.setVisibility(View.GONE);
            if (diff > 0) {
                Glide.with(CalendarActivity.this)
                        .load(R.drawable.icon_help)
                        .into(imgSign);
                tv_sign.setText("充满未知的未来之日...");
            } else {
                Glide.with(CalendarActivity.this)
                        .load(R.drawable.icon_cross)
                        .into(imgSign);
                tv_sign.setText(R.string.you_are_absent);
            }
            tv_sign.setTextColor(getColor(R.color.colorOnSurface));
            cardInfo.setCardBackgroundColor(getColor(R.color.colorCardWordDetail));
        } else {
            layoutWordNum.setVisibility(View.VISIBLE);
            layoutMemo.setVisibility(View.VISIBLE);
            Glide.with(CalendarActivity.this)
                    .load(R.drawable.icon_done)
                    .into(imgSign);
            tv_sign.setText("此日之事，此日已毕！干得漂亮！");
            tv_sign.setTextColor(getColor(R.color.colorPrimary));
            cardInfo.setCardBackgroundColor(getColor(R.color.colorSecondaryContainer));
            tv_wordNum.setText((myDateList.get(0).getWordLearnNumber() + myDateList.get(0).getWordReviewNumber()) + "");
            if (myDateList.get(0).getRemark() != null) {
                layoutMemo.setVisibility(View.VISIBLE);
                tv_memo.setText(myDateList.get(0).getRemark());
            } else {
                layoutMemo.setVisibility(View.GONE);
            }
        }

    }

}