package com.jediwus.learningapplication.gson;

public class JsonContentWord {

    // 单词本体
    private String wordHead;

    // 单词在书中的序号
    private String wordId;

    // 单词内容详情
    private JsonContentWordContent content;

    public String getWordHead() {
        return wordHead;
    }

    public void setWordHead(String wordHead) {
        this.wordHead = wordHead;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String wordId) {
        this.wordId = wordId;
    }

    public JsonContentWordContent getContent() {
        return content;
    }

    public void setContent(JsonContentWordContent content) {
        this.content = content;
    }
}
