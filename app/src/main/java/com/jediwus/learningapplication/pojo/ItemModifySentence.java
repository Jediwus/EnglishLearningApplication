package com.jediwus.learningapplication.pojo;

public class ItemModifySentence {

    private String sentenceEn;

    private String sentenceCn;

    public ItemModifySentence(String sentenceEn, String sentenceCn) {
        this.sentenceEn = sentenceEn;
        this.sentenceCn = sentenceCn;
    }

    public String getSentenceEn() {
        return sentenceEn;
    }

    public void setSentenceEn(String sentenceEn) {
        this.sentenceEn = sentenceEn;
    }

    public String getSentenceCn() {
        return sentenceCn;
    }

    public void setSentenceCn(String sentenceCn) {
        this.sentenceCn = sentenceCn;
    }
}
