package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

/**
 * 短语词组类.
 */
public class Phrase extends LitePalSupport {

    // 中文短语
    private String cnPhrase;

    // 英语短语
    private String enPhrase;

    // 归属单词
    private int wordId;


    public String getCnPhrase() {
        return cnPhrase;
    }

    public void setCnPhrase(String cnPhrase) {
        this.cnPhrase = cnPhrase;
    }

    public String getEnPhrase() {
        return enPhrase;
    }

    public void setEnPhrase(String enPhrase) {
        this.enPhrase = enPhrase;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
