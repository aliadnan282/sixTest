package com.workout.sixpacksabs.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.workout.sixpacksabs.data.entity.UserWeight;

import java.util.List;

/*
 * Created by AdnanAli on 3/12/2018.

 */

@Dao
public interface UserWeightDao {
    @Query("SELECT * FROM `user_weight`")
    LiveData<List<UserWeight>> getUserWeight();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserUser(UserWeight... userWeights);


}
