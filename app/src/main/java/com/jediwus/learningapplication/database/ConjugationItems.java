package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

public class ConjugationItems extends LitePalSupport {

    // 中文短语
    private String cnConjugation;

    // 英语短语
    private String enConjugation;

    // 归属 Conjugation 的 ID
    private int ConjugationId;


    public String getCnConjugation() {
        return cnConjugation;
    }

    public void setCnConjugation(String cnConjugation) {
        this.cnConjugation = cnConjugation;
    }

    public String getEnConjugation() {
        return enConjugation;
    }

    public void setEnConjugation(String enConjugation) {
        this.enConjugation = enConjugation;
    }

    public int getConjugationId() {
        return ConjugationId;
    }

    public void setConjugationId(int conjugationId) {
        ConjugationId = conjugationId;
    }
}
