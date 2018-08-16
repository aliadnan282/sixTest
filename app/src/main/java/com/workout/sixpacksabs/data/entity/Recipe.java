package com.workout.sixpacksabs.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by AdnanAli on 3/12/2018.
 */
@Entity(tableName = "recipe")
public class Recipe {
    @PrimaryKey
    @ColumnInfo(name = "day_id")
    int dayId;

    @ColumnInfo(name = "meal_status")
    boolean isComplete = false;
    @ColumnInfo(name = "meal_image")
    String imageUrl;

    public Recipe(int i, boolean b) {
        this.dayId = i;
        this.isComplete = b;
    }

    public Recipe() {
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

}
