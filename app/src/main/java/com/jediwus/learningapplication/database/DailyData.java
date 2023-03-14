package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

/**
 * 有关每日数据的类
 */
public class DailyData extends LitePalSupport {

    private int id;

    public String getPicVertical() {
        return picVertical;
    }

    public void setPicVertical(String picVertical) {
        this.picVertical = picVertical;
    }

    public String getPicHorizontal() {
        return picHorizontal;
    }

    public void setPicHorizontal(String picHorizontal) {
        this.picHorizontal = picHorizontal;
    }

    private String picVertical;

    private String picHorizontal;

    private String dailyChs;

    private String dailyEn;

    private String dailySound;
    // 更新时间
    private String dayTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDailyChs() {
        return dailyChs;
    }

    public void setDailyChs(String dailyChs) {
        this.dailyChs = dailyChs;
    }

    public String getDailyEn() {
        return dailyEn;
    }

    public void setDailyEn(String dailyEn) {
        this.dailyEn = dailyEn;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getDailySound() {
        return dailySound;
    }

    public void setDailySound(String dailySound) {
        this.dailySound = dailySound;
    }
}
