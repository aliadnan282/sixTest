package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.ExerciseDayProgress;
import com.workout.sixpacksabs.databinding.ActivityWorkoutBinding;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.view.adapter.CategoryAdapter;
import com.workout.sixpacksabs.viewmodel.CategoryViewModel;

import java.util.List;

public class WorkoutActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutActivity";
    ActivityWorkoutBinding activityWorkoutBinding;
    CategoryViewModel categoryViewModel;
    AchievementDataSetting achievementDataSetting;
    CategoryAdapter categoryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWorkoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_workout);
        activityWorkoutBinding.rvCategory.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this);
        activityWorkoutBinding.rvCategory.setNestedScrollingEnabled(false);
        activityWorkoutBinding.rvCategory.setAdapter(categoryAdapter);

        // Get a new or existing ViewModel from the ViewModelProvider.
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        categoryViewModel.getAllCategories().observe(this, categories -> {
            //To show ads in recyclerview append 1 extra model
            categories.add(new Category());
            categoryAdapter.setCategories(categories);
        });

        categoryViewModel.getDailyExerciseProgress().observe(this, dailyExerciseProgresses -> {

            if (achievementDataSetting == null || achievementDataSetting.isCancelled()) {
                achievementDataSetting = new AchievementDataSetting();
                achievementDataSetting.executeOnExecutor(SixPackThreadPoolExecutor.getInstance(), dailyExerciseProgresses);
            }
        });
    }

    private class AchievementDataSetting extends AsyncTask<List<ExerciseDayProgress>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<ExerciseDayProgress>... lists) {
            if (lists[0].size() != 0) {
                List<Achievement> achievementList = categoryViewModel.getAllAchievements();
                for (Achievement achievement : achievementList) {
                    if (achievement.getAchievementsDays() == lists[0].get(0).getDay_counter() && !achievement.isComplete()) {
                        AnalyticsManager.getInstance().sendAnalytics("achievement_completed", "achievement_level");
                        achievement.setComplete(true);
                        categoryViewModel.updateAchievement(achievement);
                        Log.d(TAG, "Achievement day= " + achievement.getAchievementsDays() + "\t\t" + achievement.isComplete());
                    }
                }
            }
            return null;
        }

    }
}
