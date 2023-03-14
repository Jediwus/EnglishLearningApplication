package com.jediwus.learningapplication.gson;

import java.util.List;

public class JsonSyno {

    // 描述
    private String desc;

    // 各种同近义词
    private List<JsonSynos> synos;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<JsonSynos> getSynos() {
        return synos;
    }

    public void setSynos(List<JsonSynos> synos) {
        this.synos = synos;
    }

}
