package com.jediwus.learningapplication.pojo;

public class GameQueue {

    private int id;

    private String wordName;

    private String wordMeaning;


    public GameQueue(int id, String wordName, String wordMeaning) {
        this.id = id;
        this.wordName = wordName;
        this.wordMeaning = wordMeaning;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWordName() {
        return wordName;
    }

    public void setWordName(String wordName) {
        this.wordName = wordName;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }
}
