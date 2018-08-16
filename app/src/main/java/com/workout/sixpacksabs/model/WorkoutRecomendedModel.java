package com.workout.sixpacksabs.model;

import android.support.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adnanali on 07/03/2017.
 */
@Keep
public class WorkoutRecomendedModel {

    @SerializedName("recomended_exercise")
    List<String> recomendedExerciseList;
    @SerializedName("recomended_food")
    List<String> recomendedFoodsList;

    public List<String> getRecomendedExerciseList() {
        return recomendedExerciseList;
    }

    public void setRecomendedExerciseList(List<String> recomendedExerciseList) {
        this.recomendedExerciseList = recomendedExerciseList;
    }

    public List<String> getRecomendedFoodsList() {
        return recomendedFoodsList;
    }

}
