package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

/**
 * 单词解释类.
 */
public class Translation extends LitePalSupport {

    // 词性
    private String wordType;

    // 中文词意
    private String cnMeaning;

    // 英文词意
    private String enMeaning;

    // 归属单词
    private int wordId;


    public String getWordType() {
        return wordType;
    }

    public void setWordType(String wordType) {
        this.wordType = wordType;
    }

    public String getCnMeaning() {
        return cnMeaning;
    }

    public void setCnMeaning(String cnMeaning) {
        this.cnMeaning = cnMeaning;
    }

    public String getEnMeaning() {
        return enMeaning;
    }

    public void setEnMeaning(String enMeaning) {
        this.enMeaning = enMeaning;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
