package com.jediwus.learningapplication.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonRels {

    // 词性
    @SerializedName("pos")
    private String pos;

    // 同根词列
    @SerializedName("words")
    private List<JsonRelsWords> words;


    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public List<JsonRelsWords> getWords() {
        return words;
    }

    public void setWords(List<JsonRelsWords> words) {
        this.words = words;
    }
}
