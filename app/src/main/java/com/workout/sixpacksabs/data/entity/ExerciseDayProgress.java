package com.workout.sixpacksabs.data.entity;

public class ExerciseDayProgress {

    public String latest_date;
    public int day_counter;

    public ExerciseDayProgress(String latest_date, int day_counter) {
        this.latest_date = latest_date;
        this.day_counter = day_counter;
    }

    public String getLatest_date() {
        return latest_date;
    }

    public void setLatest_date(String latest_date) {
        this.latest_date = latest_date;
    }

    public int getDay_counter() {
        return day_counter;
    }

    public void setDay_counter(int day_counter) {
        this.day_counter = day_counter;
    }

}