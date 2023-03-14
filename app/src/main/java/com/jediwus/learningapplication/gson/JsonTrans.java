package com.jediwus.learningapplication.gson;

public class JsonTrans {

    // 翻译
    private String tranCn;

    // 英释
    private String descOther;

    // 词性
    private String pos;

    // 中释
    private String descCn;

    // 其他翻译
    private String tranOther;

    public String getTranCn() {
        return tranCn;
    }

    public void setTranCn(String tranCn) {
        this.tranCn = tranCn;
    }

    public String getDescOther() {
        return descOther;
    }

    public void setDescOther(String descOther) {
        this.descOther = descOther;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getDescCn() {
        return descCn;
    }

    public void setDescCn(String descCn) {
        this.descCn = descCn;
    }

    public String getTranOther() {
        return tranOther;
    }

    public void setTranOther(String tranOther) {
        this.tranOther = tranOther;
    }
}
