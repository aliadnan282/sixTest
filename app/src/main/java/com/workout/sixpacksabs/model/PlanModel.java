package com.workout.sixpacksabs.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PlanModel {
    @SerializedName("plan")
    private List<Plan> plan = new ArrayList<>();

    public List<Plan> getPlan() {
        return plan;
    }

    public void setPlan(List<Plan> plan) {
        this.plan = plan;
    }

    @Override
    public String toString() {
        return "ClassPojo [plan = " + plan + "]";
    }
}
