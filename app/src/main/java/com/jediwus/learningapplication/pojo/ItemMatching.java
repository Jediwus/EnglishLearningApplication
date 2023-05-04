package com.jediwus.learningapplication.pojo;

public class ItemMatching {

    private int wordId;

    private String content;

    private boolean isSelected;

    public ItemMatching(int itemId, String itemContent, boolean isSelected) {
        this.wordId = itemId;
        this.content = itemContent;
        this.isSelected = isSelected;
    }

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
