package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

/**
 * 例句类.
 */
public class Sentence extends LitePalSupport {

    // 英文句子
    private String enSentence;

    // 中文句子
    private String cnSentence;

    // 归属单词
    private int wordId;


    public String getEnSentence() {
        return enSentence;
    }

    public void setEnSentence(String enSentence) {
        this.enSentence = enSentence;
    }

    public String getCnSentence() {
        return cnSentence;
    }

    public void setCnSentence(String cnSentence) {
        this.cnSentence = cnSentence;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
