package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

/**
 * 学习时间的数据
 * The type Study time data.
 */
public class StudyTimeData extends LitePalSupport {
    // 学习日期
    private String date;
    // 学习时长
    private String time;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
