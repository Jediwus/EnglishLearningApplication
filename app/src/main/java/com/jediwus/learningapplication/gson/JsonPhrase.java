package com.jediwus.learningapplication.gson;

import java.util.List;

public class JsonPhrase {

    // 各种词组
    private List<JsonPhrases> phrases;

    // 描述
    private String desc;


    public List<JsonPhrases> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<JsonPhrases> phrases) {
        this.phrases = phrases;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }



}
