package com.workout.sixpacksabs.data.entity;

public class DayProgress {
    public int day_id;
    public float day_percentage;
    public int exe_status;

    public DayProgress(int day_id, float day_percentage, int exe_status) {
        this.day_id = day_id;
        this.day_percentage = day_percentage;
        this.exe_status = exe_status;
    }

    public int getExe_status() {
        return exe_status;
    }

    public void setExe_status(int exe_status) {
        this.exe_status = exe_status;
    }

    public int getDay_id() {
        return day_id;
    }

    public void setDay_id(int day_id) {
        this.day_id = day_id;
    }

    public float getDay_percentage() {
        return day_percentage;
    }

    public void setDay_percentage(float day_percentage) {
        this.day_percentage = day_percentage;
    }
}