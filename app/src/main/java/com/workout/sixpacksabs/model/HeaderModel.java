package com.workout.sixpacksabs.model;


public class HeaderModel {

    private int mColorResource;
    private String mTitleResource;
    private int mImageResource;

    public HeaderModel(String title, int image, int color) {
        mTitleResource = title;
        mImageResource = image;
        mColorResource = color;
    }

    public int getmColorResource() {
        return mColorResource;
    }

    public int getmImageResource() {
        return mImageResource;
    }

    public String getTitle() {
        return mTitleResource;
    }
}
