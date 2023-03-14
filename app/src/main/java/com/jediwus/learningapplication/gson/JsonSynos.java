package com.jediwus.learningapplication.gson;

import java.util.List;

public class JsonSynos {

    // 词性
    private String pos;

    // 词中文释义
    private String tran;

    // 词例句本身
    private List<JsonSynosHwds> hwds;


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

    public List<JsonSynosHwds> getHwds() {
        return hwds;
    }

    public void setHwds(List<JsonSynosHwds> hwds) {
        this.hwds = hwds;
    }
}
