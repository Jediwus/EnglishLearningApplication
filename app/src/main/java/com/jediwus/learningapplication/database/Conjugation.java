package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

public class Conjugation extends LitePalSupport {

//    n.
//    accessibility    易接近；可亲；可以得到
//    accession    增加；就职；到达

    // Conjugation 的主键
    private int id;

    // 词性
    private String pos;

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

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }
}
