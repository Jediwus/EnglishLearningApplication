package com.jediwus.learningapplication.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JsonRelWord {

    // 各种同根词
    @SerializedName("rels")
    private List<JsonRels> rels;

    // 描述
    @SerializedName("desc")
    private String desc;


    public List<JsonRels> getRels() {
        return rels;
    }

    public void setRels(List<JsonRels> rels) {
        this.rels = rels;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
