package com.jediwus.learningapplication.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 用户偏好设置类.
 */
public class UserPreference extends LitePalSupport {

    @Column(unique = true)
    private int id;

    // 用户ID
    private int userId;

    // 当前正在使用的书目.为-1则代表未创建书目，是新用户
    @Column(defaultValue = "-1")
    private int currentBookId;

    // 每日需要背单词的数量，如果为0即未设置单词量
    @Column(defaultValue = "0")
    private int wordNeedReciteNum;

    // 上次开始背单词的时间（点了背单词的按钮的那一刻），重新选书时需重置该值
    @Column(defaultValue = "0")
    private long lastStartTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrentBookId() {
        return currentBookId;
    }

    public void setCurrentBookId(int currentBookId) {
        this.currentBookId = currentBookId;
    }

    public int getWordNeedReciteNum() {
        return wordNeedReciteNum;
    }

    public void setWordNeedReciteNum(int wordNeedReciteNum) {
        this.wordNeedReciteNum = wordNeedReciteNum;
    }

    public long getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(long lastStartTime) {
        this.lastStartTime = lastStartTime;
    }
}
