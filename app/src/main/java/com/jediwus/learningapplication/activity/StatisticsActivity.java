package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.StudyTimeData;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.TimeController;

import org.litepal.LitePal;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends BaseActivity {

    private static final String TAG = "StatisticsActivity";

    private AlertDialog dialog;

    private final ArrayList<BarEntry> barEntriesLearned = new ArrayList<>();
    private final ArrayList<BarEntry> barEntriesReviewed = new ArrayList<>();
    private final ArrayList<BarEntry> barEntriesLearningTime = new ArrayList<>();
    private final ArrayList<PieEntry> pieEntries = new ArrayList<>();

    private BarChart barChartWord;
    private TextView tv_thisDayLearned;
    private TextView tv_thisDayReviewed;
    private BarChart barChartTime;
    private TextView tv_learnDuration;
    private PieChart pieChart;
    private TextView tv_notHaveLearned;
    private TextView tv_shallowReviewed;
    private TextView tv_deepReviewed;
    private TextView tv_fullyMastered;

    private float wordLearnedToday;
    private float wordReviewedToday;
    private float LearningTimeToday;
    private float notHaveLearned;
    private float shallowReviewed;
    private float deepReviewed;
    private float fullyMastered;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    showBarChartWord();
                    showBarChartTime();
                    showPieChart();
                    tv_thisDayLearned.setText((int) wordLearnedToday + "");
                    tv_thisDayReviewed.setText((int) wordReviewedToday + "");
                    if (LearningTimeToday != 0) {
                        // 构造方法的字符格式这里如果小数不足2位,会以0补足.
                        DecimalFormat decimalFormat = new DecimalFormat(".00");
                        String p = decimalFormat.format(LearningTimeToday);
                        if (p.indexOf(".") == 0) {
                            tv_learnDuration.setText("0" + p);
                        } else {
                            tv_learnDuration.setText(p);
                        }
                    } else {
                        tv_learnDuration.setText("0");
                    }
                    tv_notHaveLearned.setText((int) notHaveLearned + "");
                    tv_shallowReviewed.setText((int) shallowReviewed + "");
                    tv_deepReviewed.setText((int) deepReviewed + "");
                    tv_fullyMastered.setText((int) fullyMastered + "");
                    dialog.dismiss();
                    break;
                case 1:
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 界面初始化
        initializerUi();
        // 柱状图初始化
        initializerBarChart();
        // 饼状图初始化
        initializerPieChart();
        // 开启等待提示框
        showProgressDialog();

        new Thread(() -> {

            // 设置每日单词量的数据
            setDailyWordData();
            // 设置学习时长的数据
            setLearningDurationData();
            // 设置饼状图数据
            setPieChartData();

            Message message = new Message();
            message.what = 0;
            handler.sendMessage(message);

        }).start();

    }

    private void showProgressDialog() {
        // 开启等待框
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(StatisticsActivity.this);
        builder.setTitle("Loading");
        builder.setMessage("数据正在玩命下载中...");
        ProgressBar progressBar = new ProgressBar(StatisticsActivity.this);
        builder.setView(progressBar);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    private void initializerUi() {
        // 每日学习量
        barChartWord = findViewById(R.id.chart_word_number);
        tv_thisDayLearned = findViewById(R.id.text_chart_this_day_learned_number);
        tv_thisDayReviewed = findViewById(R.id.text_chart_this_day_reviewed_number);
        // 学习时长统计
        barChartTime = findViewById(R.id.chart_learning_duration);
        tv_learnDuration = findViewById(R.id.text_chart_this_day_learned_time);
        // 单词占比情况
        pieChart = findViewById(R.id.chart_word_proportion);
        tv_notHaveLearned = findViewById(R.id.text_chart_not_have_learned_number);
        tv_shallowReviewed = findViewById(R.id.text_chart_shallow_reviewed_number);
        tv_deepReviewed = findViewById(R.id.text_chart_deep_reviewed_number);
        tv_fullyMastered = findViewById(R.id.text_chart_fully_mastered_number);
        // 返回主界面
        ImageView imageHome = findViewById(R.id.img_statistics_home);
        imageHome.setOnClickListener(view -> onBackPressed());
    }

    /**
     * 界面初始化
     */
    private void initializerBarChart() {

        barChartWord.setDrawBarShadow(false);
        barChartWord.setDrawValueAboveBar(true);
        barChartWord.getDescription().setEnabled(false);

        barChartTime.setDrawBarShadow(false);
        barChartTime.setDrawValueAboveBar(true);
        barChartTime.getDescription().setEnabled(false);

        Legend barChartWordLegend = barChartWord.getLegend();
        barChartWordLegend.setForm(Legend.LegendForm.CIRCLE);
        barChartWordLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        barChartWordLegend.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));

        Legend barChartTimeLegend = barChartTime.getLegend();
        barChartTimeLegend.setForm(Legend.LegendForm.CIRCLE);
        barChartTimeLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        barChartTimeLegend.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));

        // 设置双指缩放
        barChartWord.setPinchZoom(false);
        barChartTime.setPinchZoom(false);
        // 动画
        barChartWord.animateY(2000);
        barChartTime.animateY(2000);

        XAxis barChartWordXAxis = barChartWord.getXAxis();
        // 是否显示x坐标的数据
        barChartWordXAxis.setDrawLabels(true);
        // 设置x坐标数据的位置
        barChartWordXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 是否显示网格线中与x轴垂直的网格线
        barChartWordXAxis.setDrawGridLines(false);

        XAxis barChartTimeXAxis = barChartTime.getXAxis();
        // 是否显示x坐标的数据
        barChartTimeXAxis.setDrawLabels(true);
        // 设置x坐标数据的位置
        barChartTimeXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 是否显示网格线中与x轴垂直的网格线
        barChartTimeXAxis.setDrawGridLines(false);

        final List<String> xValue = new ArrayList<>();
        // index = 0 的位置的数据是否显示，跟barChart.groupBars中的第一个参数有关。
        xValue.add("zero");
        xValue.add(TimeController.getPastDate(6));
        xValue.add(TimeController.getPastDate(5));
        xValue.add(TimeController.getPastDate(4));
        xValue.add(TimeController.getPastDate(3));
        xValue.add(TimeController.getPastDate(2));
        xValue.add(TimeController.getPastDate(1));
        xValue.add(TimeController.getPastDate(0));
        // 设置单词数 x 轴标签格式化器
        barChartWordXAxis.setValueFormatter(new IndexAxisValueFormatter(xValue));
        // 设置学习时长 X 轴标签格式化器
        barChartTimeXAxis.setValueFormatter(new IndexAxisValueFormatter(xValue));

        // 设置图表周围边框的颜色
        barChartWordXAxis.setAxisLineColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        barChartWordXAxis.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));

        // 设置图表周围边框的颜色
        barChartTimeXAxis.setAxisLineColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        barChartTimeXAxis.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));

        YAxis rightYAxis = barChartWord.getAxisRight();
        rightYAxis.setDrawGridLines(false);
        // 设置右侧的 Y 轴是否显示。包括 Y 轴的那一条线和上面的标签都不显示
        rightYAxis.setEnabled(true);
        // 设置 Y 轴右侧的标签是否显示。只是控制 Y 轴处的标签，不会影响 Y 轴。
        rightYAxis.setDrawLabels(false);
        // 此方法专门控制坐标轴线
        rightYAxis.setDrawAxisLine(false);
        // 设置图表周围边框的颜色
        rightYAxis.setAxisLineColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        rightYAxis.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));

        YAxis rightYAxis2 = barChartTime.getAxisRight();
        rightYAxis2.setDrawGridLines(false);
        // 设置右侧的 Y 轴是否显示。包括 Y 轴的那一条线和上面的标签都不显示
        rightYAxis2.setEnabled(true);
        // 设置 Y 轴右侧的标签是否显示。只是控制 Y 轴处的标签，不会影响 Y 轴。
        rightYAxis2.setDrawLabels(false);
        // 此方法专门控制坐标轴线
        rightYAxis2.setDrawAxisLine(false);
        // 设置图表周围边框的颜色
        rightYAxis2.setAxisLineColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        rightYAxis2.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));


        YAxis leftYAxis = barChartWord.getAxisLeft();
        leftYAxis.setEnabled(true);
        leftYAxis.setDrawLabels(true);
        leftYAxis.setDrawAxisLine(true);
        leftYAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 只有左右 Y 轴标签都设置不显示水平网格线，图形才不会显示网格线
        leftYAxis.setDrawGridLines(false);
        // 设置网格线是在柱子的上层还是下一层（类比 Photoshop 的图层）
        leftYAxis.setDrawGridLinesBehindData(true);
        // 设置最小的间隔，防止出现重复的标签。这个得自己尝试一下就知道了。
        leftYAxis.setGranularity(1f);
        // 设置左轴最小值的数值。如果IndexAxisValueFormatter自定义了字符串的话，那么就是从序号为2的字符串开始取值。
        leftYAxis.setAxisMinimum(0);
        // 左轴的最小值默认占有10dp的高度，如果左轴最小值为0，一般会去除0的那部分高度
        leftYAxis.setSpaceBottom(0);
        // 设置图表周围边框的颜色
        leftYAxis.setAxisLineColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        leftYAxis.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));

        YAxis leftYAxis2 = barChartTime.getAxisLeft();
        leftYAxis2.setEnabled(true);
        leftYAxis2.setDrawLabels(true);
        leftYAxis2.setDrawAxisLine(true);
        leftYAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // 只有左右y轴标签都设置不显示水平网格线，图形才不会显示网格线
        leftYAxis2.setDrawGridLines(false);
        // 设置网格线是在柱子的上层还是下一层（类比 Photoshop 的图层）
        leftYAxis2.setDrawGridLinesBehindData(true);
        // 设置最小的间隔，防止出现重复的标签。这个得自己尝试一下就知道了。
        leftYAxis2.setGranularity(1f);
        // 设置左轴最小值的数值。如果IndexAxisValueFormatter自定义了字符串的话，那么就是从序号为2的字符串开始取值。
        leftYAxis2.setAxisMinimum(0);
        // 左轴的最小值默认占有10dp的高度，如果左轴最小值为0，一般会去除0的那部分高度
        leftYAxis2.setSpaceBottom(0);
        // 设置图表周围边框的颜色
        leftYAxis2.setAxisLineColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 设置要用于标签的文本颜色。使用资源中的颜色时，请确保使用 getResources()).getColor(…)
        leftYAxis2.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
    }

    private void initializerPieChart() {
        // 是否使用百分比显示，但是我还是没操作出来。
        pieChart.setUsePercentValues(false);
        pieChart.setDragDecelerationEnabled(false);
        pieChart.getDescription().setEnabled(false);
        // 设置 pie chart 图表点击 Item 高亮是否可用
        pieChart.setHighlightPerTapEnabled(true);
        // 动画
        pieChart.animateX(2000);

        // 设置 entry 中的描述 label 是否画进饼状图中
        pieChart.setDrawEntryLabels(true);
        // 设置该 label 文字是的颜色
        pieChart.setEntryLabelColor(getResources().getColor(R.color.colorOnSurfaceVariant, MyApplication.getContext().getTheme()));
        // 设置该 label 文字的字体大小
        pieChart.setEntryLabelTextSize(10f);

        // 设置圆孔的显隐，也就是内圆
        pieChart.setDrawHoleEnabled(true);
        // 设置内圆的半径。外圆的半径好像是不能设置的，改变控件的宽度和高度，半径会自适应。
        pieChart.setHoleRadius(28f);
        // 设置内圆的颜色
        pieChart.setHoleColor(getResources().getColor(R.color.colorCardWordDetail, MyApplication.getContext().getTheme()));
        // 设置是否显示文字
        pieChart.setDrawCenterText(true);

        List<UserPreference> userPreferenceList =
                LitePal.where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                        .find(UserPreference.class);
        // 设置饼状图中心的文字
        pieChart.setCenterText(ExternalData.getTypeById(userPreferenceList.get(0).getCurrentBookId()));
        // 设置文字的消息
        pieChart.setCenterTextSize(10f);
        // 设置文字的颜色
        pieChart.setCenterTextColor(getResources().getColor(R.color.colorPrimary, MyApplication.getContext().getTheme()));
        // 设置内圆和外圆的一个交叉园的半径，这样会凸显内外部的空间
        pieChart.setTransparentCircleRadius(30f);
        // 设置透明圆的颜色
        pieChart.setTransparentCircleColor(getResources().getColor(R.color.colorOutline, MyApplication.getContext().getTheme()));
        // 设置透明圆的透明度
        pieChart.setTransparentCircleAlpha(180);

        // 获取图例
        Legend legend = pieChart.getLegend();
        // 是否显示
        legend.setEnabled(true);
        // 对齐
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        // 对齐
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        // 设置图例的图形样式,默认为圆形
        legend.setForm(Legend.LegendForm.DEFAULT);
        //设置图例的排列走向: vertical 相当于分行
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        // 设置图例的大小
        legend.setFormSize(6f);
        // 设置图注的字体大小
        legend.setTextSize(7f);
        // 设置图例到图注的距离
        legend.setFormToTextSpace(3f);
        // 设置图例是绘制在图表内部还是外部
        legend.setDrawInside(true);
        // 设置图列换行(注意使用影响性能,仅适用legend位于图表下面)，我也不知道怎么用的
        legend.setWordWrapEnabled(false);
        legend.setTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
    }

    /**
     * 设置每日单词量的数据
     */
    private void setDailyWordData() {
        barEntriesLearned.add(new BarEntry(1, getWordLearnedData(TimeController.getPastDateWithYear(6))));
        barEntriesLearned.add(new BarEntry(2, getWordLearnedData(TimeController.getPastDateWithYear(5))));
        barEntriesLearned.add(new BarEntry(3, getWordLearnedData(TimeController.getPastDateWithYear(4))));
        barEntriesLearned.add(new BarEntry(4, getWordLearnedData(TimeController.getPastDateWithYear(3))));
        barEntriesLearned.add(new BarEntry(5, getWordLearnedData(TimeController.getPastDateWithYear(2))));
        barEntriesLearned.add(new BarEntry(6, getWordLearnedData(TimeController.getPastDateWithYear(1))));
        wordLearnedToday = getWordLearnedData(TimeController.getPastDateWithYear(0));
        barEntriesLearned.add(new BarEntry(7, wordLearnedToday));

        barEntriesReviewed.add(new BarEntry(1, getWordReviewedData(TimeController.getPastDateWithYear(6))));
        barEntriesReviewed.add(new BarEntry(2, getWordReviewedData(TimeController.getPastDateWithYear(5))));
        barEntriesReviewed.add(new BarEntry(3, getWordReviewedData(TimeController.getPastDateWithYear(4))));
        barEntriesReviewed.add(new BarEntry(4, getWordReviewedData(TimeController.getPastDateWithYear(3))));
        barEntriesReviewed.add(new BarEntry(5, getWordReviewedData(TimeController.getPastDateWithYear(2))));
        barEntriesReviewed.add(new BarEntry(6, getWordReviewedData(TimeController.getPastDateWithYear(1))));
        wordReviewedToday = getWordReviewedData(TimeController.getPastDateWithYear(0));
        barEntriesReviewed.add(new BarEntry(7, wordReviewedToday));
    }

    /**
     * 获取 学习单词数
     *
     * @param dayTime String
     * @return int 学习单词数
     */
    private int getWordLearnedData(String dayTime) {
        String[] days = dayTime.split("-");
        List<MyDate> myDateList =
                LitePal.where("year = ? and month = ? and date = ?", days[0], days[1], days[2])
                        .find(MyDate.class);
        if (myDateList.isEmpty()) {
            Log.d(TAG, "getWordLearnedData: 未找到学习单词数，返回0");
            return 0;
        } else {
            return myDateList.get(0).getWordLearnNumber();
        }
    }

    /**
     * 获取 复习单词数
     *
     * @param dayTime String
     * @return int 复习单词数
     */
    private int getWordReviewedData(String dayTime) {
        String[] days = dayTime.split("-");
        List<MyDate> myDateList =
                LitePal.where("year = ? and month = ? and date = ?", days[0], days[1], days[2])
                        .find(MyDate.class);
        if (myDateList.isEmpty()) {
            Log.d(TAG, "getWordLearnedData: 未找到复习单词数，返回0");
            return 0;
        } else {
            return myDateList.get(0).getWordReviewNumber();
        }
    }

    /**
     * 设置学习时长的数据
     */
    private void setLearningDurationData() {
        barEntriesLearningTime.add(new BarEntry(1, getLearningTimeData(TimeController.getPastDateWithYear(6))));
        barEntriesLearningTime.add(new BarEntry(2, getLearningTimeData(TimeController.getPastDateWithYear(5))));
        barEntriesLearningTime.add(new BarEntry(3, getLearningTimeData(TimeController.getPastDateWithYear(4))));
        barEntriesLearningTime.add(new BarEntry(4, getLearningTimeData(TimeController.getPastDateWithYear(3))));
        barEntriesLearningTime.add(new BarEntry(5, getLearningTimeData(TimeController.getPastDateWithYear(2))));
        barEntriesLearningTime.add(new BarEntry(6, getLearningTimeData(TimeController.getPastDateWithYear(1))));
        LearningTimeToday = getLearningTimeData(TimeController.getPastDateWithYear(0));
        barEntriesLearningTime.add(new BarEntry(7, LearningTimeToday));
    }

    /**
     * 获取 学习时长
     *
     * @param dayTime String
     * @return float 学习时长
     */
    private float getLearningTimeData(String dayTime) {
        List<StudyTimeData> studyTimeDataList =
                LitePal.where("date = ?", dayTime)
                        .find(StudyTimeData.class);
        if (studyTimeDataList.isEmpty()) {
            Log.d(TAG, "getWordLearnedData: 未找到学习时长，返回0");
            return 0;
        } else {
            Log.d(TAG, "getWordLearnedData: 学习时长为" + studyTimeDataList.get(0).getTime());
            return Float.parseFloat((double) Long.parseLong(studyTimeDataList.get(0).getTime()) / (1000 * 60) + "");
        }
    }

    /**
     * 设置饼状图数据
     */
    private void setPieChartData() {
        // 未学过条件：haveLearned为0（没有在每日任务中学习过），还要排除熟知词
        List<Word> notHaveLearnedWordList = LitePal
                .where("haveLearned = ? and isEasy = ?", 0 + "", 0 + "").select("wordId")
                .find(Word.class);
        notHaveLearned = notHaveLearnedWordList.size();
        if (!notHaveLearnedWordList.isEmpty()) {
            pieEntries.add(new PieEntry(notHaveLearnedWordList.size(), "未学"));
        }
        // 浅度复习过条件：掌握度位小于10且在每日任务中学习过
        List<Word> shallowReviewedWordList = LitePal
                .where("masterDegree < ? and haveLearned = ?", 10 + "", 1 + "")
                .select("wordId")
                .find(Word.class);
        shallowReviewed = shallowReviewedWordList.size();
        if (!shallowReviewedWordList.isEmpty()) {
            pieEntries.add(new PieEntry(shallowReviewedWordList.size(), "浅度"));
        }
        // 深度复习过条件：掌握度位为10(满)且深度掌握次数小于3
        List<Word> deepReviewedWordList = LitePal
                .where("masterDegree = ? and deepMasterTimes < ?", 10 + "", 3 + "")
                .select("wordId")
                .find(Word.class);
        deepReviewed = deepReviewedWordList.size();
        if (!deepReviewedWordList.isEmpty()) {
            pieEntries.add(new PieEntry(deepReviewedWordList.size(), "深度"));
        }
        // 已掌握条件：掌握度位为10(满)且深度掌握次数为3(满)，或为熟知词
        List<Word> fullyMasteredWordList = LitePal
                .where("masterDegree = ? and deepMasterTimes = ? or isEasy = ?", 10 + "", 3 + "", 1 + "")
                .select("wordId")
                .find(Word.class);
        fullyMastered = fullyMasteredWordList.size();
        if (!fullyMasteredWordList.isEmpty()) {
            pieEntries.add(new PieEntry(fullyMasteredWordList.size(), "已掌握"));
        }
    }

    /**
     * 显示每日单词条形统计图
     */
    private void showBarChartWord() {
        BarDataSet barDataSetLearned = new BarDataSet(barEntriesLearned, "该日学习量");
        barDataSetLearned.setColor(getResources().getColor(R.color.colorPrimary, MyApplication.getContext().getTheme()));
        BarDataSet barDataSetReviewed = new BarDataSet(barEntriesReviewed, "该日复习量");
        barDataSetReviewed.setColor(getResources().getColor(R.color.colorPrimaryContainer, MyApplication.getContext().getTheme()));

        ArrayList<IBarDataSet> iBarDataSets = new ArrayList<>();
        iBarDataSets.add(barDataSetLearned);
        iBarDataSets.add(barDataSetReviewed);

        BarData barData = new BarData(iBarDataSets);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });
        // 是否显示柱子的数值
        barData.setDrawValues(true);
        // 柱子上面标注的数值的字体大小
        barData.setValueTextSize(10f);
        // 设置标注的数值的颜色
        barData.setValueTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 每个柱子的宽度
        barData.setBarWidth(0.3f);
        barChartWord.setData(barData);
        barChartWord.invalidate();
        // 如果不设置组直接的距离的话，那么两个柱子会公用一个空间，即发生重叠；另外，设置了各种距离之后，X轴方向会自动调整距离，以保持“两端对齐”
        barChartWord.groupBars(0.45f, 0.32f, 0.05f);
    }

    /**
     * 显示每日学习时长条形统计图
     */
    private void showBarChartTime() {
        BarDataSet barDataSetTime = new BarDataSet(barEntriesLearningTime, "该日学习时长/min");
        barDataSetTime.setColor(getResources().getColor(R.color.md_theme_dark_onPrimaryContainer, MyApplication.getContext().getTheme()));

        ArrayList<IBarDataSet> iBarDataTimes = new ArrayList<>();
        iBarDataTimes.add(barDataSetTime);

        BarData barData = new BarData(iBarDataTimes);
        // 是否显示柱子的数值
        barData.setDrawValues(true);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // 构造方法的字符格式这里如果小数不足2位,会以0补
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                String p = decimalFormat.format(value);
                if (p.indexOf(".") == 0) {
                    return "0" + p;
                } else {
                    return p;
                }
            }
        });
        // 柱子上面标注的数值的字体大小
        barData.setValueTextSize(10f);
        // 设置标注的数值的颜色
        barData.setValueTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        // 每个柱子的宽度
        barData.setBarWidth(0.5f);
        barChartTime.setData(barData);
        barChartTime.invalidate();
    }

    /**
     * 显示各属性单词比例饼状图
     */
    private void showPieChart() {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(
                getResources().getColor(R.color.colorPrimaryContainer, MyApplication.getContext().getTheme()),
                getResources().getColor(R.color.colorSecondaryContainer, MyApplication.getContext().getTheme()),
                getResources().getColor(R.color.colorTertiaryContainer, MyApplication.getContext().getTheme()),
                getResources().getColor(R.color.colorErrorContainer, MyApplication.getContext().getTheme())
        );
        // 设置每块饼之间的空隙
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(10f);//点击某个饼时拉长的宽度

        PieData pieData = new PieData(pieDataSet);
        pieData.setDrawValues(true);
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

}