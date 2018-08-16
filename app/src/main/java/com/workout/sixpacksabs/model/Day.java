package com.workout.sixpacksabs.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Day {
    int dayId;
    @SerializedName("exercise")
    private List<Exercise> exerciseList = new ArrayList<>();

    public int getDayId() {
        return dayId;
    }

    public List<Exercise> getExerciseList() {
        return exerciseList;
    }
}

	