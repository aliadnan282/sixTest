package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.databinding.ActivityRecipeBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.JsonReadUtils;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.model.DayRecipe;
import com.workout.sixpacksabs.model.RecipeModel;
import com.workout.sixpacksabs.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RecipeDetailActivity extends AppCompatActivity {

    private static final int BREAKFAST = 0;
    private static final int SNACK = 1;
    private static final int LUNCH = 2;
    private static final int DINNER = 3;


    int daysRecipePosition = 1;
    Recipe recipeEntity;
    DayRecipe daysRecipeModel;


    AppPreference appPreference;
    GsonBuilder builder = new GsonBuilder();
    Gson mGson = builder.create();
    List<DayRecipe> dayRecipeList = new ArrayList<>();
    RecipeViewModel recipeViewModel;
    ActivityRecipeBinding activityRecipeBinding;
    private FetchCategoryData fetchCategoryData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRecipeBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe);
        AdsManager.getInstance().showFacebookBannerAd(activityRecipeBinding.adContainer);
        recipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        setSupportActionBar(activityRecipeBinding.toolbar);
        appPreference = AppPreference.getInstance(this);
        if (getIntent().getExtras() != null) {
            daysRecipePosition = getIntent().getIntExtra("day", 1);
        }
        // collapsingToolbarLayout.setStatusBarScrimColor(getResources().getColor(android.R.color.transparent));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.recipe_detail_format));
        fetchCategoryData = new FetchCategoryData();
        fetchCategoryData.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());
        activityRecipeBinding.doneFloatAction.setOnClickListener(view -> onFloatClicked());

    }

    public void onFloatClicked() {

        new AsyncTask<Void, Void, Recipe>() {
            @Override
            protected Recipe doInBackground(Void... voids) {
                Recipe recipe = recipeEntity;
                if (recipeEntity.isComplete()) {
                    recipeEntity.setComplete(false);
                } else {
                    recipeEntity.setComplete(true);
                }
                recipeViewModel.updateRecipe(recipeEntity);
                return recipe;
            }

            @Override
            protected void onPostExecute(Recipe recipe) {
                super.onPostExecute(recipe);
                if (recipe.isComplete()) {
                    unSelectFloatButton();
                } else {
                    selectFloatButton();
                }
                onBackPressed();
            }
        }.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());

    }

    private void unSelectFloatButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityRecipeBinding.doneFloatAction.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
        } else {
            activityRecipeBinding.doneFloatAction.setImageResource(R.drawable.ic_tick_undone);

        }
    }

    private void selectFloatButton() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activityRecipeBinding.doneFloatAction.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        } else {
            activityRecipeBinding.doneFloatAction.setImageResource(R.drawable.ic_tick_done);
        }
    }


    private void fillUI() {
        daysRecipeModel = dayRecipeList.get(0);

        StringBuilder stringBuilderBreakfast = new StringBuilder();
        for (RecipeModel recipeModel : daysRecipeModel.getMeals().get(BREAKFAST).getRecipeModels()) {
            stringBuilderBreakfast.append((daysRecipeModel.getMeals().get(BREAKFAST).getRecipeModels().indexOf(recipeModel) + 1) + ") ");
            stringBuilderBreakfast.append(recipeModel.getName());
            stringBuilderBreakfast.append("\n\n");
        }
        activityRecipeBinding.includeRecipe.tvBreakfastDetial.setText(stringBuilderBreakfast.toString());

        StringBuilder stringBuilderSnack = new StringBuilder();
        for (RecipeModel recipeModel : daysRecipeModel.getMeals().get(SNACK).getRecipeModels()) {
            stringBuilderSnack.append((daysRecipeModel.getMeals().get(SNACK).getRecipeModels().indexOf(recipeModel) + 1) + ") ");
            stringBuilderSnack.append(recipeModel.getName());
            stringBuilderSnack.append("\n\n");
        }
        activityRecipeBinding.includeRecipe.tvSnackDetial.setText(stringBuilderSnack.toString());

        StringBuilder stringBuilderLunch = new StringBuilder();
        for (RecipeModel recipeModel : daysRecipeModel.getMeals().get(LUNCH).getRecipeModels()) {
            stringBuilderLunch.append((daysRecipeModel.getMeals().get(LUNCH).getRecipeModels().indexOf(recipeModel) + 1) + ") ");
            stringBuilderLunch.append(recipeModel.getName());
            stringBuilderLunch.append("\n\n");
        }
        activityRecipeBinding.includeRecipe.tvLunchDetial.setText(stringBuilderLunch.toString());

        StringBuilder stringBuilderdinner = new StringBuilder();
        for (RecipeModel recipeModel : daysRecipeModel.getMeals().get(DINNER).getRecipeModels()) {
            stringBuilderdinner.append((daysRecipeModel.getMeals().get(DINNER).getRecipeModels().indexOf(recipeModel) + 1) + ") ");
            stringBuilderdinner.append(recipeModel.getName());
            stringBuilderdinner.append("\n\n");
        }
        activityRecipeBinding.includeRecipe.tvDinnerDetial.setText(stringBuilderdinner.toString());

        if (recipeEntity.isComplete())
            selectFloatButton();
        else
            unSelectFloatButton();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dayRecipeList = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AdsManager.getInstance().showInterstitialAd();
        this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }

    public class FetchCategoryData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                recipeEntity = recipeViewModel.getSelectedDaysRecipe(daysRecipePosition);
                dayRecipeList = Arrays.asList(mGson.fromJson(JsonReadUtils.loadJSONFromAsset("workout_json/recipes_en.json"), DayRecipe[].class));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            //---------------------------END Reading Exercises Data  ----------------------------------
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fillUI();
        }
    }
}
