package com.jediwus.learningapplication.database;

import org.litepal.crud.LitePalSupport;

public class FavoritesLinkWord extends LitePalSupport {

    private int wordId;

    private int favoritesId;

    public int getWordId() {
        return wordId;
    }

    public void setWordId(int wordId) {
        this.wordId = wordId;
    }

    public int getFavoritesId() {
        return favoritesId;
    }

    public void setFavoritesId(int favoritesId) {
        this.favoritesId = favoritesId;
    }
}
