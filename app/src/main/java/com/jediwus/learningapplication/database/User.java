package com.jediwus.learningapplication.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * 用户类.
 */
public class User extends LitePalSupport {
    // 唯一，用户ID
    @Column(unique = true, defaultValue = "0")
    private int userId;

    // 用户头像
    private String userProfile;

    // 用户昵称
    private String userName;

    // 词汇总数
    @Column(defaultValue = "0")
    private int userVocabulary;

    // 获得金币数
    @Column(defaultValue = "0")
    private int userCoins;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserVocabulary() {
        return userVocabulary;
    }

    public void setUserVocabulary(int userVocabulary) {
        this.userVocabulary = userVocabulary;
    }

    public int getUserCoins() {
        return userCoins;
    }

    public void setUserCoins(int userCoins) {
        this.userCoins = userCoins;
    }
}
