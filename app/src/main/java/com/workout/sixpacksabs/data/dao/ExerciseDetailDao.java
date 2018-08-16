package com.workout.sixpacksabs.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.ExerciseDetail;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

@Dao
public interface ExerciseDetailDao {
    @Query("SELECT * FROM exercise_detail WHERE exe_id=:id")
    LiveData<List<ExerciseDetail>> getExerciseDetail(int id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategory(ExerciseDetail... categories);

}
