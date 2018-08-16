package com.workout.sixpacksabs.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.workout.sixpacksabs.data.entity.Recipe;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM `recipe`")
    LiveData<List<Recipe>> getAllRecipe();

    @Query("SELECT * FROM `recipe`")
    List<Recipe> getAllRecipeUnobservable();

    @Query("SELECT * FROM `recipe` WHERE day_id=:days")
    Recipe getSelectedDayRecipe(int days);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(Recipe... categories);

    @Update
    void updateRecipe(Recipe... recipes);


}
