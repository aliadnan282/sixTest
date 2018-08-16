package com.workout.sixpacksabs.view.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.databinding.FragmentRecipeListBinding;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.view.adapter.RecipeAdapter;
import com.workout.sixpacksabs.viewmodel.RecipeViewModel;

import java.util.List;

/**
 * Created by AdnanAli on 3/22/2018.
 */

public class RecipeFragment extends Fragment {

    private static final String TAG = RecipeFragment.class.getName();
    RecipeAdapter recipeAdapter;
    FragmentRecipeListBinding fragmentRecipeListBinding;
    RecipeViewModel recipeViewModel;
    public RecipeFragment() {
    }

    public static RecipeFragment newInstance() {
        RecipeFragment fragment = new RecipeFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRecipeListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_list, container, false);
       // AdsManager.getInstance().showFacebookBannerAd(fragmentRecipeListBinding.adContainer);
        fragmentRecipeListBinding.rvRecipe.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(getContext());
        fragmentRecipeListBinding.rvRecipe.setNestedScrollingEnabled(false);
        fragmentRecipeListBinding.rvRecipe.setAdapter(recipeAdapter);
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        recipeViewModel.getRecipe().observe(this, planDays -> recipeAdapter.setRecipeDays(planDays));
        Log.d(TAG, "onCreateView: ");
        return fragmentRecipeListBinding.getRoot();
    }
}
