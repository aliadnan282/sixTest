package com.workout.sixpacksabs.model;

public class ProgressItem {

    private int color;
    private float progressItemPercentage;
    private String title;

    public ProgressItem(float progressItemPercentage, int color, String title) {
        this.color = color;
        this.progressItemPercentage = progressItemPercentage;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getProgressItemPercentage() {
        return progressItemPercentage;
    }

    public void setProgressItemPercentage(float progressItemPercentage) {
        this.progressItemPercentage = progressItemPercentage;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

}
