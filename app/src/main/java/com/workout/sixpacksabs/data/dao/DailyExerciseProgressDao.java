package com.workout.sixpacksabs.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

@Dao
public interface DailyExerciseProgressDao {
    @Query("SELECT * FROM daily_exercise_progress")
    LiveData<List<DailyExerciseProgress>> getAllDayProgress();

    @Query("SELECT * FROM daily_exercise_progress order by day_id desc limit 30")
    LiveData<List<DailyExerciseProgress>> getLast30DayProgress();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDayExerciseProgess(DailyExerciseProgress... dailyExerciseProgresses);


}
