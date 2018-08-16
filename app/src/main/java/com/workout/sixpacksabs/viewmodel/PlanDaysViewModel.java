package com.workout.sixpacksabs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.data.entity.DayProgress;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.data.pojo.CompleteExercise;
import com.workout.sixpacksabs.data.repository.SixPackRepository;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

public class PlanDaysViewModel extends AndroidViewModel {


    public LiveData<List<DayProgress>> daysProgress;
    private SixPackRepository repository;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<PlanExercise>> selectedPlanDays;

    public PlanDaysViewModel(@NonNull Application application) {
        super(application);
        repository = SixPackRepository.getInstance();

    }

    public LiveData<List<PlanExercise>> getSelectedPlanDays(int plan) {
        selectedPlanDays = repository.getSelectedPlanDays(plan);
        return selectedPlanDays;
    }


    public LiveData<List<DayProgress>> getDayProgress(int plan, int status) {
        daysProgress = repository.getCursorData(plan, status);
        return daysProgress;
    }

    public List<CompleteExercise> getDayExerciseDetail(int plan, int day) {

        return repository.getDayExerciseDetail(plan, day);
    }

    public void updateDay(PlanExercise planExercise) {
        repository.updateDay(planExercise);
    }

    public void insertDailyExerciseProgress(DailyExerciseProgress dailyExerciseProgress) {
        repository.insertDailyExerciseProgress(dailyExerciseProgress);
    }
}
