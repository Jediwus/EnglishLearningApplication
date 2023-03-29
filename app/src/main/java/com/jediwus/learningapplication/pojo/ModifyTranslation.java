package com.jediwus.learningapplication.pojo;

public class ModifyTranslation {

    private String translationCn;

    private String translationEn;


    public ModifyTranslation() {
    }

    public ModifyTranslation(String translationCn, String translationEn) {
        this.translationCn = translationCn;
        this.translationEn = translationEn;
    }

    public String getTranslationCn() {
        return translationCn;
    }

    public void setTranslationCn(String translationCn) {
        this.translationCn = translationCn;
    }

    public String getTranslationEn() {
        return translationEn;
    }

    public void setTranslationEn(String translationEn) {
        this.translationEn = translationEn;
    }
}
