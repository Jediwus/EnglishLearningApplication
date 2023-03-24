package com.jediwus.learningapplication.pojo;

public class ItemMeaningPicker {

    public static final int DEFAULT = -1;
    public static final int RIGHT = 0;
    public static final int WRONG = 1;

    // ID值，用来与正确的值进行判断
    private int id;

    // 释义内容
    private String wordMeaning;

    // 判断状态:-1代表默认未选；0代表正确；1代表错误
    private int ifRight;

    public ItemMeaningPicker(int id, String wordMean, int ifRight) {
        this.id = id;
        this.wordMeaning = wordMean;
        this.ifRight = ifRight;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }

    public int getIfRight() {
        return ifRight;
    }

    public void setIfRight(int ifRight) {
        this.ifRight = ifRight;
    }
}
