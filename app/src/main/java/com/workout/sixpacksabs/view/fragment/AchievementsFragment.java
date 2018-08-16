package com.workout.sixpacksabs.view.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.databinding.FragmentRecipeListBinding;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.view.adapter.AchievementsAdapter;
import com.workout.sixpacksabs.viewmodel.AchievementViewModel;

import java.util.List;


public class AchievementsFragment extends Fragment {

    private static final String TAG = AchievementsFragment.class.getName();
    AchievementsAdapter achievementsAdapter;
    FragmentRecipeListBinding fragmentRecipeListBinding;
    AchievementViewModel achievementViewModel;
    List<Achievement> achievementList;

    public AchievementsFragment() {
    }

    public static AchievementsFragment newInstance() {
        AchievementsFragment fragment = new AchievementsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRecipeListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
       // AdsManager.getInstance().showFacebookBannerAd(fragmentRecipeListBinding.adContainer);

        fragmentRecipeListBinding.rvRecipe.setLayoutManager(new GridLayoutManager(getContext(), 3));
        achievementsAdapter = new AchievementsAdapter(getContext());
        fragmentRecipeListBinding.rvRecipe.setNestedScrollingEnabled(false);
        fragmentRecipeListBinding.rvRecipe.setAdapter(achievementsAdapter);
        achievementViewModel = ViewModelProviders.of(this).get(AchievementViewModel.class);
        achievementViewModel.getAllAchievementsObserve().observe(this, achievements -> achievementsAdapter.setAchievements(achievements));
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

     /*   new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                achievementList = achievementViewModel.getAllAchievements();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                achievementsAdapter.setAchievements(achievementList);

            }
        }.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());
*/
        Log.d(TAG, "onCreateView: ");
        return fragmentRecipeListBinding.getRoot();
    }
}
