package com.jediwus.learningapplication.pojo;

public class MatchQueue {

    private int wordId;

    private int position;

    public MatchQueue(int wordId, int position) {
        this.wordId = wordId;
        this.position = position;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
