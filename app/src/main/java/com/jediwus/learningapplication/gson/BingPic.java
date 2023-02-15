package com.jediwus.learningapplication.gson;

import java.util.List;

public class BingPic {

    private List<Images> images;

    private ToolTips tooltips;

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }

    public ToolTips getTooltips() {
        return tooltips;
    }

    public void setTooltips(ToolTips tooltips) {
        this.tooltips = tooltips;
    }
}
