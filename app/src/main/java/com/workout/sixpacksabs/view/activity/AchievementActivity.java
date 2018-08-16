package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.databinding.ActivityAchievementBinding;
import com.workout.sixpacksabs.view.adapter.AchievementsAdapter;
import com.workout.sixpacksabs.viewmodel.AchievementViewModel;

import java.util.List;

public class AchievementActivity extends AppCompatActivity {
    AchievementViewModel achievementViewModel;
    List<Achievement> achievementList;
    AchievementsAdapter achievementsAdapter;

    ActivityAchievementBinding activityAchievementBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAchievementBinding = DataBindingUtil.setContentView(this, R.layout.activity_achievement);
        activityAchievementBinding.rvRecipe.setLayoutManager(new GridLayoutManager(this, 3));
        achievementsAdapter = new AchievementsAdapter(this);
        activityAchievementBinding.rvRecipe.setNestedScrollingEnabled(false);
        activityAchievementBinding.rvRecipe.setAdapter(achievementsAdapter);
        achievementViewModel = ViewModelProviders.of(this).get(AchievementViewModel.class);
        achievementViewModel.getAllAchievementsObserve().observe(this, achievements -> achievementsAdapter.setAchievements(achievements));
    }
}
