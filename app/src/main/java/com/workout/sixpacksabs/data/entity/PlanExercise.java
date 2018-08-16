package com.workout.sixpacksabs.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

/**
 * Created by AdnanAli on 3/13/2018.
 */
@Entity(tableName = "plan_exercise", primaryKeys = {"plan_id", "day_id", "exe_id"})
public class PlanExercise {
    @ColumnInfo(name = "plan_id")
    int planId;
    @ColumnInfo(name = "day_id")
    int dayId;
    @ColumnInfo(name = "exe_id")
    int exeId;
    @ColumnInfo(name = "exe_name")
    String exeName;
    @ColumnInfo(name = "exe_status")
    boolean isComplete = false;
    @ColumnInfo(name = "exe_reps")
    private int exerciseReps;
    @Ignore
    @ColumnInfo(name = "exe_percentage")
    private double exercisePercentage;

    public PlanExercise(int planId, int dayId, int exeId, String exeName, int reps, boolean isComplete) {
        this.planId = planId;
        this.dayId = dayId;
        this.exeId = exeId;
        this.exeName = exeName;
        this.exerciseReps = reps;
        this.isComplete = isComplete;
    }

    public PlanExercise() {
    }

    public String getExeName() {
        return exeName;
    }

    public void setExeName(String exeName) {
        this.exeName = exeName;
    }

    public double getExercisePercentage() {
        return exercisePercentage;
    }

    public void setExercisePercentage(double exercisePercentage) {
        this.exercisePercentage = exercisePercentage;
    }

    public int getExerciseReps() {
        return exerciseReps;
    }

    public void setExerciseReps(int exerciseReps) {
        this.exerciseReps = exerciseReps;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    public int getExeId() {
        return exeId;
    }

    public void setExeId(int exeId) {
        this.exeId = exeId;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

}
