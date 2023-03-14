package com.jediwus.learningapplication.gson;

import java.util.List;

public class JsonSentence {

    // 各种句子
    private List<JsonSentences> sentences;

    // 类描述
    private String desc;

    public List<JsonSentences> getSentences() {
        return sentences;
    }

    public void setSentences(List<JsonSentences> sentences) {
        this.sentences = sentences;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
