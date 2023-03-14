package com.jediwus.learningapplication.database;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class SynonymItems extends LitePalSupport {

    // 归属 Synonym 的 ID
    private int SynonymId;


    // 同近单词或词组
    private String itemWords;


    public String getItemWords() {
        return itemWords;
    }

    public void setItemWords(String itemWords) {
        this.itemWords = itemWords;
    }

    public int getSynonymId() {
        return SynonymId;
    }

    public void setSynonymId(int synonymId) {
        SynonymId = synonymId;
    }

}
