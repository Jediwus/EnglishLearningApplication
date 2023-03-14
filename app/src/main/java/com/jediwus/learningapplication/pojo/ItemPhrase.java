package com.jediwus.learningapplication.pojo;

public class ItemPhrase {

    private String Chinese;

    private String English;


    public ItemPhrase(String chinese, String english) {
        Chinese = chinese;
        English = english;
    }


    public String getChinese() {
        return Chinese;
    }

    public void setChinese(String chinese) {
        Chinese = chinese;
    }

    public String getEnglish() {
        return English;
    }

    public void setEnglish(String english) {
        English = english;
    }
}
