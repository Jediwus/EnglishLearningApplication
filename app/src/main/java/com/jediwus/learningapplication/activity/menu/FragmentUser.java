package com.jediwus.learningapplication.activity.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.AboutActivity;
import com.jediwus.learningapplication.activity.AlarmActivity;
import com.jediwus.learningapplication.activity.AuxFunctionActivity;
import com.jediwus.learningapplication.activity.CalendarActivity;
import com.jediwus.learningapplication.activity.PlanActivity;
import com.jediwus.learningapplication.activity.StatisticsActivity;
import com.jediwus.learningapplication.activity.ThirdPartySDKActivity;
import com.jediwus.learningapplication.activity.VocabularyListActivity;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.User;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.util.Arrays;
import java.util.List;

public class FragmentUser extends Fragment implements View.OnClickListener {

    private TextView tv_day;
    private TextView tv_number;
    private TextView tv_coins;
    private AlertDialog dialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    /**
     * 当Fragment的布局视图被创建时调用，该方法可以访问Fragment布局中的视图元素，如Button、TextView等。
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tv_user_name = view.findViewById(R.id.text_user_name);
        ImageView img_portrait = view.findViewById(R.id.img_user_portrait);
        tv_day = view.findViewById(R.id.text_user_days);
        tv_number = view.findViewById(R.id.text_user_words);
        tv_coins = view.findViewById(R.id.text_user_coins);

        LinearLayout layoutAllVocabulary = view.findViewById(R.id.layout_user_word_list);
        layoutAllVocabulary.setOnClickListener(this);

        LinearLayout layoutStatistics = view.findViewById(R.id.layout_user_statistics);
        layoutStatistics.setOnClickListener(this);

        LinearLayout layoutCalendar = view.findViewById(R.id.layout_user_calendar);
        layoutCalendar.setOnClickListener(this);

        LinearLayout layoutPlan = view.findViewById(R.id.layout_user_plan);
        layoutPlan.setOnClickListener(this);

        RelativeLayout layoutPolicy = view.findViewById(R.id.layout_user_policy);
        layoutPolicy.setOnClickListener(this);

        RelativeLayout layoutAbout = view.findViewById(R.id.layout_user_about);
        layoutAbout.setOnClickListener(this);

        RelativeLayout layoutRemind = view.findViewById(R.id.layout_user_reminder_setting);
        layoutRemind.setOnClickListener(this);

        RelativeLayout layoutAux = view.findViewById(R.id.layout_user_aux_fun);
        layoutAux.setOnClickListener(this);


        // 花费金币打卡
        LinearLayout layoutCoins = view.findViewById(R.id.layout_user_coins);
        layoutCoins.setOnClickListener(viewCoins -> {
            final String[] date = TimeController.getStringDate(TimeController.getCurrentDateStamp()).split("-");
            final int coins = Integer.parseInt(tv_coins.getText().toString().trim());
            if (coins >= 100) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
                builder.setTitle("被你发现了！")
                        .setMessage("每日任务获得的金币可用于补签，需要花费100金币进行学习日历的补签吗?")
                        .setPositiveButton("确定", (dialogInterface, i) -> {

                            // 日期限定器
                            CalendarConstraints calendarConstraints = new CalendarConstraints.Builder()
                                    //可选择的日期范围：2023-1-1 ~ 今天
                                    .setValidator(CompositeDateValidator.allOf(Arrays.asList(
                                            DateValidatorPointForward.from(TimeController.getCalendarDateStamp(2019, 9, 1)),
                                            DateValidatorPointBackward.now()
                                    )))
                                    .build();
                            // 返回一个单选日期的 MaterialDatePicker
                            MaterialDatePicker.Builder<Long> materialDatePickerBuilder = MaterialDatePicker.Builder.datePicker();
                            // 设置默认选中时间（今天）
                            materialDatePickerBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
                            // 设置要显示出来的Title
                            materialDatePickerBuilder.setTitleText("选择需要补签的日期");
                            // 设置日期限定器
                            materialDatePickerBuilder.setCalendarConstraints(calendarConstraints);
                            // 肯定按钮
                            materialDatePickerBuilder.setPositiveButtonText("决定好了");
                            // 否定按钮
                            materialDatePickerBuilder.setNegativeButtonText("取消");
                            // build 一个 MaterialDatePicker
                            MaterialDatePicker<Long> picker = materialDatePickerBuilder.build();

                            picker.addOnPositiveButtonClickListener(selection -> {
                                String[] selectedDate = TimeController.getStringDate(selection).split("-");
                                int selectedYear = Integer.parseInt(selectedDate[0]);
                                int selectedMonth = Integer.parseInt(selectedDate[1]);
                                int selectedDay = Integer.parseInt(selectedDate[2]);

                                List<MyDate> myDateList = LitePal.where("year = ? and month = ? and date = ?", selectedYear + "", selectedMonth + "", selectedDay + "")
                                        .find(MyDate.class);
                                if (myDateList.isEmpty()) {
                                    if (selectedYear > Integer.parseInt(date[0])) {
                                        Toast.makeText(MyApplication.getContext(), "你这年份太假了", Toast.LENGTH_SHORT).show();
                                    } else if (selectedMonth > Integer.parseInt(date[1])) {
                                        Toast.makeText(MyApplication.getContext(), "你这月份太假了", Toast.LENGTH_SHORT).show();
                                    } else if (selectedDay >= Integer.parseInt(date[2])) {
                                        Toast.makeText(MyApplication.getContext(), "只可补签往日哦", Toast.LENGTH_SHORT).show();
                                    } else {
                                        MyDate myDate = new MyDate();
                                        myDate.setUserId(DataConfig.getWeChatNumLogged());
                                        myDate.setDate(selectedDay);
                                        myDate.setMonth(selectedMonth);
                                        myDate.setYear(selectedYear);
                                        myDate.setRemark("补签：金币 -100");
                                        myDate.save();
                                        User user = new User();
                                        if (coins > 100) {
                                            user.setUserCoins(coins - 100);
                                        } else {
                                            user.setToDefault("userCoins");
                                        }
                                        user.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");
                                        updateView();
                                        Toast.makeText(MyApplication.getContext(), "已为你补签", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(MyApplication.getContext(), "该日已有记录，请勿重复补签", Toast.LENGTH_SHORT).show();
                                }
                            });

                            picker.addOnNegativeButtonClickListener(viewNegativeButton -> Toast.makeText(MyApplication.getContext(), "补签取消", Toast.LENGTH_SHORT).show());
                            // 显示
                            picker.show(requireActivity().getSupportFragmentManager(), picker.toString());
                        })
                        .setNegativeButton("取消", null);
                dialog = builder.create();
                dialog.show();
            } else {
                Toast.makeText(MyApplication.getContext(), "不足100金币无法补签哦", Toast.LENGTH_SHORT).show();
            }
        });

        // 用户头像、昵称信息设置
        List<User> userList = LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "").find(User.class);
        tv_user_name.setText(userList.get(0).getUserName());
        //加载图片
        Glide.with(MyApplication.getContext())
                .load(R.drawable.portrait_hikari)
                //设置图片圆角属性
                .transform(new CircleCrop())
                .into(img_portrait);
    }

    @Override
    public void onStart() {
        super.onStart();
        // 更新视图信息
        updateView();
    }

    /**
     * 设置用户学习天数单词数等信息
     */
    @SuppressLint("SetTextI18n")
    private void updateView() {
        List<MyDate> myDateList = LitePal
                .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(MyDate.class);
        tv_day.setText(myDateList.size() + "");
        List<User> userList = LitePal
                .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(User.class);
        tv_number.setText(userList.get(0).getUserVocabulary() + "");
        tv_coins.setText(userList.get(0).getUserCoins() + "");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            // 我的词汇
            case R.id.layout_user_word_list:
                intent = new Intent(getActivity(), VocabularyListActivity.class);
                break;
            // 学习数据
            case R.id.layout_user_statistics:
                intent = new Intent(getActivity(), StatisticsActivity.class);
                break;
            // 每日打卡
            case R.id.layout_user_calendar:
                intent = new Intent(getActivity(), CalendarActivity.class);
                break;
            // 学习计划
            case R.id.layout_user_plan:
                intent = new Intent(getActivity(), PlanActivity.class);
                break;
            // 提醒功能
            case R.id.layout_user_reminder_setting:
                intent = new Intent(getActivity(), AlarmActivity.class);
                break;
            // 辅助功能
            case R.id.layout_user_aux_fun:
                intent = new Intent(getActivity(), AuxFunctionActivity.class);
                break;
            // 第三方SDK
            case R.id.layout_user_policy:
                intent = new Intent(getActivity(), ThirdPartySDKActivity.class);
                break;
            // 关于软件
            case R.id.layout_user_about:
                intent = new Intent(getActivity(), AboutActivity.class);
                break;
            default:
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
