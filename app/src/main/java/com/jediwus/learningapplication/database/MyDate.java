package com.jediwus.learningapplication.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 我的日期类.
 */
public class MyDate extends LitePalSupport {

    @Column(unique = true)
    private int id;

    // 年
    private int year;

    // 月
    private int month;

    // 日
    private int date;

    // 此日新学单词数量
    private int wordLearnNumber;

    // 此日复习单词数量
    private int wordReviewNumber;

    // 学习心得与感受
    private String remark;

    // 归属用户
    private int userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getWordLearnNumber() {
        return wordLearnNumber;
    }

    public void setWordLearnNumber(int wordLearnNumber) {
        this.wordLearnNumber = wordLearnNumber;
    }

    public int getWordReviewNumber() {
        return wordReviewNumber;
    }

    public void setWordReviewNumber(int wordReviewNumber) {
        this.wordReviewNumber = wordReviewNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
