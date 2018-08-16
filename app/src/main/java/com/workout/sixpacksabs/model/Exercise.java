package com.workout.sixpacksabs.model;

import com.google.gson.annotations.SerializedName;

public class Exercise {
    @SerializedName("exe_reps")
    private int exerciseReps;
    @SerializedName("exe_status")
    private boolean exerciseStatus;
    @SerializedName("exe_id")
    private int exerciseId;
    @SerializedName("exe_name")
    private String exerciseName;

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getExerciseReps() {
        return exerciseReps;
    }

    public void setExerciseReps(int exerciseReps) {
        this.exerciseReps = exerciseReps;
    }

    public boolean getExerciseStatus() {
        return exerciseStatus;
    }

    public void setExerciseStatus(boolean exerciseStatus) {
        this.exerciseStatus = exerciseStatus;
    }

    public int ExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseReps='" + exerciseReps + '\'' +
                ", exerciseStatus='" + exerciseStatus + '\'' +
                ", exerciseId='" + exerciseId + '\'' +
                ", exerciseName='" + exerciseName + '\'' +
                '}';
    }
}