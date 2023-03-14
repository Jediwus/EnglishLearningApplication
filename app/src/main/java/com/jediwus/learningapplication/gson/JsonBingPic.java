package com.jediwus.learningapplication.gson;

import java.util.List;

public class JsonBingPic {

    private List<JsonImages> images;

    private JsonToolTips tooltips;

    public List<JsonImages> getImages() {
        return images;
    }

    public void setImages(List<JsonImages> images) {
        this.images = images;
    }

    public JsonToolTips getTooltips() {
        return tooltips;
    }

    public void setTooltips(JsonToolTips tooltips) {
        this.tooltips = tooltips;
    }
}
