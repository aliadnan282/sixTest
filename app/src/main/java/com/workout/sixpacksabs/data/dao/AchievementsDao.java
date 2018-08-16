package com.workout.sixpacksabs.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.workout.sixpacksabs.data.entity.Achievement;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

@Dao
public interface AchievementsDao {
    @Query("SELECT * FROM achievement")
    List<Achievement> getAllAchievements();

    @Query("SELECT * FROM achievement")
    LiveData<List<Achievement>> getAllAchievementsObserve();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchievements(Achievement... achievements);

    @Update
    void updateDay(Achievement... achievements);


}
