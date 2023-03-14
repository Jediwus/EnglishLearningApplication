package com.jediwus.learningapplication.gson;

import com.google.gson.annotations.SerializedName;

public class JsonRelsWords {

    // 词本身
    @SerializedName("hwd")
    private String hwd;

    // 词中文释义
    @SerializedName("tran")
    private String tran;

    public String getHwd() {
        return hwd;
    }

    public void setHwd(String hwd) {
        this.hwd = hwd;
    }

    public String getTran() {
        return tran;
    }

    public void setTran(String tran) {
        this.tran = tran;
    }
}
