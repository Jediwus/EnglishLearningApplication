package com.jediwus.learningapplication.pojo;

public class ItemFavorites {

    private int favoritesId;

    private int wordNumbers;

    private String favoritesName;

    private String favoritesRemark;

    public ItemFavorites(int favoritesId, int wordNumbers, String favoritesName, String favoritesRemark) {
        this.favoritesId = favoritesId;
        this.wordNumbers = wordNumbers;
        this.favoritesName = favoritesName;
        this.favoritesRemark = favoritesRemark;
    }

    public int getFavoritesId() {
        return favoritesId;
    }

    public void setFavoritesId(int favoritesId) {
        this.favoritesId = favoritesId;
    }

    public int getWordNumbers() {
        return wordNumbers;
    }

    public void setWordNumbers(int wordNumbers) {
        this.wordNumbers = wordNumbers;
    }

    public String getFavoritesName() {
        return favoritesName;
    }

    public void setFavoritesName(String favoritesName) {
        this.favoritesName = favoritesName;
    }

    public String getFavoritesRemark() {
        return favoritesRemark;
    }

    public void setFavoritesRemark(String favoritesRemark) {
        this.favoritesRemark = favoritesRemark;
    }
}
