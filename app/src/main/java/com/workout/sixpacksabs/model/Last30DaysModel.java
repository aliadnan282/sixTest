package com.workout.sixpacksabs.model;

public class Last30DaysModel {
    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    boolean isCompleted;

    public Last30DaysModel(int id, boolean imageId) {
        this.id = id;
        this.isCompleted = imageId;
    }

    public Last30DaysModel() {
    }

}
