package com.jediwus.learningapplication.myUtil;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 时间控制工具类
 */
public class TimeController {

    private static final String TAG = "TimeController";

    public static final String FORMAT_TIME = "yyyy-MM-dd";

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(FORMAT_TIME);

    public static long todayDate;

    /*---------------------------------------日期类----------------------------------------*/

    /**
     * 得到当前日期戳(不带时间，只有日期)
     *
     * @return long
     */
    public static long getCurrentDateStamp() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDate = cal.get(Calendar.DATE);
        long time = 0;
        try {
            time = Objects.requireNonNull(simpleDateFormat
                    .parse(currentYear + "-" + currentMonth + "-" + currentDate)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getCurrentDateStamp: " + time);
        return time;
    }

    /**
     * 根据指定日期戳解析成日期形式（yyyy-MM-dd）
     *
     * @param timeStamp long
     * @return String
     */
    public static String getStringDate(long timeStamp) {
        return simpleDateFormat.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
    }

    /**
     * 根据指定日期戳解析成日期形式（yyyy-MM-dd HH:mm:ss）
     *
     * @param timeStamp long
     * @return String
     */
    public static String getStringDateDetail(long timeStamp) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(Long.parseLong(String.valueOf(timeStamp))));
    }

    /**
     * 得到当前日期的指定间隔后的日期
     *
     * @param time        int
     * @param intervalDay int
     * @return long
     */
    public static long getDateByDays(long time, int intervalDay) {
        // 转换成Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.add(Calendar.DATE, intervalDay);
        return calendar.getTimeInMillis();
    }

    /**
     * 计算两日期天数之差
     *
     * @param time1 long
     * @param time2 long
     * @return int
     * @throws ParseException e
     */
    public static int daysInternal(long time1, long time2) throws ParseException {
        Date date1 = simpleDateFormat.parse(getStringDate(time1));
        Date date2 = simpleDateFormat.parse(getStringDate(time2));
        assert date2 != null;
        assert date1 != null;
        return (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
    }

    /*---------------------------------------时间类----------------------------------------*/

    /**
     * 得到当前时间戳（有日期与时间）
     *
     * @return long
     */
    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 判断两个时间戳是否为同一天
     *
     * @param time1 long
     * @param time2 long
     * @return boolean
     */
    public static boolean isTheSameDay(long time1, long time2) {
        return getStringDate(time1).equals(getStringDate(time2));
    }

    /**
     * 返回过去第几天的日期
     *
     * @param past int
     * @return String
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("MM-dd");
        return format.format(today);
    }

    /**
     * 返回过去第几天的日期（含年份）
     *
     * @param past int
     * @return String
     */
    public static String getPastDateWithYear(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(today);
    }

    /**
     * 获取 n 天以后的日期
     *
     * @param num int
     * @return String
     */
    public static String getDayAgoOrAfterString(int num) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        calendar.add(Calendar.DATE, num);
        return simpleDateFormat.format(calendar.getTime());
    }
}
