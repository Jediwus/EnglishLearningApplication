package com.jediwus.learningapplication.gson;

import java.util.List;

public class JsonContentWordContent {
    // 美式发音
    private String usphone;

    // 英式发音
    private String ukphone;

    // 图片描述
    private String picture;

    // 例句
    private JsonSentence sentence;

    // 词组短语
    private JsonPhrase phrase;

    // 记忆方法
    private JsonRemMethod remMethod;

    // 解释
    private List<JsonTrans> trans;

    // 同根
    private JsonRelWord relWord;

    // 同近
    private JsonSyno syno;

    public String getUsphone() {
        return usphone;
    }

    public void setUsphone(String usphone) {
        this.usphone = usphone;
    }

    public String getUkphone() {
        return ukphone;
    }

    public void setUkphone(String ukphone) {
        this.ukphone = ukphone;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public JsonSentence getSentence() {
        return sentence;
    }

    public void setSentence(JsonSentence sentence) {
        this.sentence = sentence;
    }

    public JsonPhrase getPhrase() {
        return phrase;
    }

    public void setPhrase(JsonPhrase phrase) {
        this.phrase = phrase;
    }

    public JsonRemMethod getRemMethod() {
        return remMethod;
    }

    public void setRemMethod(JsonRemMethod remMethod) {
        this.remMethod = remMethod;
    }

    public List<JsonTrans> getTrans() {
        return trans;
    }

    public void setTrans(List<JsonTrans> trans) {
        this.trans = trans;
    }

    public JsonRelWord getRelWord() {
        return relWord;
    }

    public void setRelWord(JsonRelWord relWord) {
        this.relWord = relWord;
    }

    public JsonSyno getSyno() {
        return syno;
    }

    public void setSyno(JsonSyno syno) {
        this.syno = syno;
    }


}
