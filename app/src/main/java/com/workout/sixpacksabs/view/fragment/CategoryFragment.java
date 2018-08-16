package com.workout.sixpacksabs.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.ExerciseDayProgress;
import com.workout.sixpacksabs.databinding.FragmentCategoryListBinding;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.view.adapter.CategoryAdapter;
import com.workout.sixpacksabs.viewmodel.CategoryViewModel;

import java.util.List;

/**
 * Created by AdnanAli on 3/22/2018.
 */

public class CategoryFragment extends Fragment {

    private static final String TAG = CategoryFragment.class.getName();
    CategoryAdapter categoryAdapter;
    FragmentCategoryListBinding fragmentCategoryListBinding;
    CategoryViewModel categoryViewModel;
    AchievementDataSetting achievementDataSetting;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance() {
        CategoryFragment fragment = new CategoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentCategoryListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_list, container, false);
        // Recyclerview Initializtion
        fragmentCategoryListBinding.rvCategory.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryAdapter = new CategoryAdapter(getContext());
        fragmentCategoryListBinding.rvCategory.setNestedScrollingEnabled(false);
        fragmentCategoryListBinding.rvCategory.setAdapter(categoryAdapter);

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
        return fragmentCategoryListBinding.getRoot();
    }

    /*  @Override
      public void setUserVisibleHint(boolean isVisibleToUser) {
          super.setUserVisibleHint(isVisibleToUser);
          if (isVisibleToUser) {
              Log.v(TAG, "run now");
              Animation animationFadeIn = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);
              startAnimation(animationFadeIn);
              animationFadeIn.setFillAfter(true);
          }
          else {  }
      }*/
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
