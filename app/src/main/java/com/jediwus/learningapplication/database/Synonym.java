package com.jediwus.learningapplication.database;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class Synonym extends LitePalSupport {

//    vt. 使用；[计]存取；接近
//    make use of
//    fashion
//    employ
//    border
//    exercise

    // Synonym 的主键
    private int id;

    // 词性
    private String pos;

    // 中文短语
    private String tran;

    // 归属单词
    private int wordId;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getTran() {
        return tran;
    }

    public void setTran(String tran) {
        this.tran = tran;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
