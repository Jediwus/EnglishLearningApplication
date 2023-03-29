package com.jediwus.learningapplication.pojo;

public class ItemModifyPhrase {

    private String phraseEn;

    private String phraseCn;


    public ItemModifyPhrase(String phraseEn, String phraseCn) {
        this.phraseEn = phraseEn;
        this.phraseCn = phraseCn;
    }

    public String getPhraseEn() {
        return phraseEn;
    }

    public void setPhraseEn(String phraseEn) {
        this.phraseEn = phraseEn;
    }

    public String getPhraseCn() {
        return phraseCn;
    }

    public void setPhraseCn(String phraseCn) {
        this.phraseCn = phraseCn;
    }
}
