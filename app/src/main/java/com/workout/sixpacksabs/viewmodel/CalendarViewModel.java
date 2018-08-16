package com.workout.sixpacksabs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.data.entity.UserWeight;
import com.workout.sixpacksabs.data.repository.SixPackRepository;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

public class CalendarViewModel extends AndroidViewModel {


    private SixPackRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        repository = SixPackRepository.getInstance();
    }

    public LiveData<List<DailyExerciseProgress>> getLast30DaysProgress() {
        return repository.getLast30DaysProgress();
    }

    public LiveData<List<UserWeight>> getUserWeight() {
        return repository.getUserWeight();
    }

    public void insertUserWeight(UserWeight userWeight) {
        repository.insertUserWeight(userWeight);
    }
}
