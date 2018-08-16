package com.workout.sixpacksabs.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Plan {
    @SerializedName("day")
    private List<Day> day = new ArrayList<>();

    public int getPlanId() {
        return planId;
    }

    int planId;

    public List<Day> getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "ClassPojo [day = " + day + "]";
    }
}