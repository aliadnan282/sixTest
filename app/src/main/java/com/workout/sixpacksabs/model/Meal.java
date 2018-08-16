package com.workout.sixpacksabs.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by AdnanAli on 11/17/2017.
 */


public class Meal {

    @SerializedName("name")
    private String name;
    @SerializedName("recipes")
    private List<RecipeModel> recipeModels = null;

    public Meal() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecipeModel> getRecipeModels() {
        return recipeModels;
    }

    public void setRecipeModels(List<RecipeModel> recipeModels) {
        this.recipeModels = recipeModels;
    }

}
