package com.jediwus.learningapplication.gson;

import com.google.gson.annotations.SerializedName;

public class JsonWord {
    // 单词在词书中排序的序号
    @SerializedName("wordRank")
    private int wordRank;

    // 单词的名称
    @SerializedName("headWord")
    private String headWord;

    // 单词的内容
    @SerializedName("content")
    private JsonContent content;

    // 单词归属词书的ID
    private String bookId;

    public int getWordRank() {
        return wordRank;
    }

    public void setWordRank(int wordRank) {
        this.wordRank = wordRank;
    }

    public String getHeadWord() {
        return headWord;
    }

    public void setHeadWord(String headWord) {
        this.headWord = headWord;
    }

    public JsonContent getContent() {
        return content;
    }

    public void setContent(JsonContent content) {
        this.content = content;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
