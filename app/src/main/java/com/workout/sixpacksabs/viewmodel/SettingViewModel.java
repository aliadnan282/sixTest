package com.workout.sixpacksabs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.data.repository.SixPackRepository;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

public class SettingViewModel extends AndroidViewModel {


    private SixPackRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Achievement>> allCategories;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        repository = SixPackRepository.getInstance();
    }

    public List<Recipe> getAllRecipeUnobservable() {
        return repository.getAllRecipeUnobservable();
    }

    public void updateRecipe(Recipe recipe) {
        repository.updateRecipe(recipe);
    }

    public void updatePlanExercise(PlanExercise planExercise) {
        repository.updateDay(planExercise);
    }

    public List<PlanExercise> getPlanExercisesUnobservable(int plan) {
        return repository.getPlanExercisesUnobservable(plan);
    }

}
