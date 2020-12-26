package com.certified.jobfinder.model;

import android.graphics.drawable.Drawable;

public class SliderItem {

    private int animation;
    private String title;
    private String description;
    private int image;

    public SliderItem(int animation, String title, String description, int image) {
        this.animation = animation;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public int getAnimation() {
        return animation;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return image;
    }
}
