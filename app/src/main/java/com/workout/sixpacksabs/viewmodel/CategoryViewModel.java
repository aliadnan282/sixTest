package com.workout.sixpacksabs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.ExerciseDayProgress;
import com.workout.sixpacksabs.data.repository.SixPackRepository;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

public class CategoryViewModel extends AndroidViewModel {


    private SixPackRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Category>> allCategories;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        repository = SixPackRepository.getInstance();
    }

    public LiveData<List<Category>> getAllCategories() {
        allCategories = repository.getAllCategories();
        return allCategories;
    }

    public List<Achievement> getAllAchievements() {
        return repository.getAllAchievements();
    }

    public void insertAchievement(Achievement achievement) {
        repository.insertAchievement(achievement);
    }

    public LiveData<List<ExerciseDayProgress>> getDailyExerciseProgress() {
        return repository.getExerciseDayProgress();
    }

    public void updateAchievement(Achievement achievement) {
        repository.updateAchievement(achievement);
    }
}
