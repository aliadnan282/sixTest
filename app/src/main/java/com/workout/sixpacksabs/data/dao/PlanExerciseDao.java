package com.workout.sixpacksabs.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.workout.sixpacksabs.data.entity.DayProgress;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.data.pojo.CompleteExercise;

import java.util.List;

/**
 * Created by AdnanAli on 3/13/2018.
 */
@Dao
public interface PlanExerciseDao {
    /* *//*   @Query("SELECT plan_id,exe_id,day_id,exe_status,exe_reps, (SELECT 1.0 * COUNT(*) / (select count(*) as t from `plan_exercise` where plan_id=1 and day_id=1) *100 AS percentage FROM `plan_exercise` WHERE plan_id=1 and day_id= 1 and exe_status =1 \n" +
               "GROUP BY exe_status) FROM `plan_exercise` WHERE plan_id=:type GROUP BY day_id")
       LiveData<List<PlanExercise>> getSelectedPlanDays(int type);*/
    @Query("SELECT * FROM `plan_exercise` WHERE plan_id=:type GROUP BY day_id")
    LiveData<List<PlanExercise>> getSelectedPlanDays(int type);

    @Query("SELECT * FROM `plan_exercise` WHERE plan_id=:type")
    List<PlanExercise> getPlanExercisesUnobservable(int type);

    @Query("SELECT * FROM `plan_exercise` WHERE plan_id=:type AND day_id=:day")
    LiveData<List<PlanExercise>> getSelectedDayExercise(int type, int day);

    @Query("SELECT * FROM `plan_exercise` WHERE plan_id=:type AND exe_status=1")
    PlanExercise findCompleteDays(int type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlanDays(PlanExercise... planDays);

    @Delete
    void deletePlanDay(PlanExercise planDays);

    @RawQuery(observedEntities = PlanExercise.class)
    LiveData<List<DayProgress>> getCursorData(SupportSQLiteQuery query);

    @Transaction
    @Query("SELECT  *from plan_exercise WHERE plan_id=:plan  AND day_id=:day")
    public List<CompleteExercise> getDayExerciseDetail(int plan, int day);

    @Update
    void updateDay(PlanExercise... planExercises);




/*
*
SELECT 1.0 * COUNT(*) / (select count(*) as t from plan_exercise where plan_id=1 and day_id=1) *100 AS percentage
FROM plan_exercise
 WHERE plan_id=1 and day_id= 1 and exe_status =1
GROUP BY exe_status


* */

}
