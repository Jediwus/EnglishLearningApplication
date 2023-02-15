package com.jediwus.learningapplication.gson;

import com.google.gson.annotations.SerializedName;

public class ToolTips {
    private String loading;

    private String next;

    private String previous;

    @SerializedName("walle")
    private String wallE;

    @SerializedName("walls")
    private String wallS;

    public String getLoading() {
        return loading;
    }

    public void setLoading(String loading) {
        this.loading = loading;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getWallE() {
        return wallE;
    }

    public void setWallE(String wallE) {
        this.wallE = wallE;
    }

    public String getWallS() {
        return wallS;
    }

    public void setWallS(String wallS) {
        this.wallS = wallS;
    }
}
