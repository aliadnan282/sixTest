package com.workout.sixpacksabs.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AdnanAli on 11/17/2017.
 */

public class RecipeModel {
    @SerializedName("type")
    private Integer type;

    public String getName() {
        return name;
    }

    @SerializedName("name")
    private String name;

}