package com.jediwus.learningapplication.gson;

import com.google.gson.annotations.SerializedName;

public class Images {

    private int bot;

    private String copyright;

    @SerializedName("copyrightlink")
    private String copyRightLink;

    private int drk;

    @SerializedName("enddate")
    private String endDate;

    @SerializedName("fullstartdate")
    private String fullStartDate;

    private String hsh;

    private String quiz;

    @SerializedName("startdate")
    private String startDate;

    private String title;

    private int top;

    private String url;

    @SerializedName("urlbase")
    private String urlBase;

    private boolean wp;


    public int getBot() {
        return bot;
    }

    public void setBot(int bot) {
        this.bot = bot;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyRightLink() {
        return copyRightLink;
    }

    public void setCopyRightLink(String copyRightLink) {
        this.copyRightLink = copyRightLink;
    }

    public int getDrk() {
        return drk;
    }

    public void setDrk(int drk) {
        this.drk = drk;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFullStartDate() {
        return fullStartDate;
    }

    public void setFullStartDate(String fullStartDate) {
        this.fullStartDate = fullStartDate;
    }

    public String getHsh() {
        return hsh;
    }

    public void setHsh(String hsh) {
        this.hsh = hsh;
    }

    public String getQuiz() {
        return quiz;
    }

    public void setQuiz(String quiz) {
        this.quiz = quiz;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }

    public boolean isWp() {
        return wp;
    }

    public void setWp(boolean wp) {
        this.wp = wp;
    }
}
