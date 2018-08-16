package com.workout.sixpacksabs.data.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.Context;

import com.workout.sixpacksabs.data.dao.AchievementsDao;
import com.workout.sixpacksabs.data.dao.CategoryDao;
import com.workout.sixpacksabs.data.dao.DailyExerciseProgressDao;
import com.workout.sixpacksabs.data.dao.PlanExerciseDao;
import com.workout.sixpacksabs.data.dao.RecipeDao;
import com.workout.sixpacksabs.data.dao.UserWeightDao;
import com.workout.sixpacksabs.data.database.AppDatabase;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.data.entity.DayProgress;
import com.workout.sixpacksabs.data.entity.ExerciseDayProgress;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.data.entity.UserWeight;
import com.workout.sixpacksabs.data.pojo.CompleteExercise;
import com.workout.sixpacksabs.helper.AppConstant;

import java.util.List;

/**
 * Created by AdnanAli on 3/12/2018.
 */

public class SixPackRepository {

    static SixPackRepository instance;
    private UserWeightDao userWeightDao;
    private DailyExerciseProgressDao dailyExerciseProgressDao;
    private CategoryDao categoryDao;
    private RecipeDao recipeDao;
    private PlanExerciseDao planExerciseDao;
    private AchievementsDao achievementsDao;
    private LiveData<List<Category>> allCategories;
    private LiveData<List<PlanExercise>> selectedPlanDays;

    private SixPackRepository(Context application) {
        AppDatabase db = AppDatabase.getInstance(application);
        categoryDao = db.categoryDao();
        planExerciseDao = db.planExerciseDao();
        recipeDao = db.recipeDao();
        achievementsDao = db.achievementsDao();
        userWeightDao = db.userWeightDao();
        dailyExerciseProgressDao = db.dailyExerciseProgressDao();
    }

    public static SixPackRepository getInstance() {
        if (instance == null)
            instance = new SixPackRepository(AppConstant.mContext);

        return instance;
    }

    public LiveData<List<PlanExercise>> getSelectedPlanDays(int plan) {
        selectedPlanDays = planExerciseDao.getSelectedPlanDays(plan);
        return selectedPlanDays;
    }

    public LiveData<List<PlanExercise>> getSelectedDayExercises(int plan, int day) {
        return planExerciseDao.getSelectedDayExercise(plan, day);
    }

    public List<PlanExercise> getPlanExercisesUnobservable(int plan) {
        return planExerciseDao.getPlanExercisesUnobservable(plan);
    }

    public LiveData<List<PlanExercise>> getPlan(int plan, int day) {
        return planExerciseDao.getSelectedDayExercise(plan, day);
    }

    public List<CompleteExercise> getDayExerciseDetail(int plan, int day) {
        return planExerciseDao.getDayExerciseDetail(plan, day);
    }

    public LiveData<List<PlanExercise>> getSelectedDayExercisesJson(int plan, int day) {
        return planExerciseDao.getSelectedDayExercise(plan, day);
    }


    public LiveData<List<Category>> getAllCategories() {
        allCategories = categoryDao.getAllCategory();
        return allCategories;
    }

    public Recipe getSelectedDaysRecipe(int day) {
        return recipeDao.getSelectedDayRecipe(day);
    }

    public LiveData<List<Recipe>> getAllRecipe() {
        return recipeDao.getAllRecipe();
    }

    public List<Recipe> getAllRecipeUnobservable() {
        return recipeDao.getAllRecipeUnobservable();
    }

    public LiveData<List<DayProgress>> getCursorData(int plan, int status) {

        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery("SELECT  A.day_id, (100-(1.0*count(*)/ B.total* 100)) AS day_percentage,A.exe_status FROM  plan_exercise AS A, plan_day_total_v AS B " +
                "WHERE A.exe_status=? AND A.plan_id = B.plan_id AND  A.day_id = B.day_id AND A.plan_id =?" +
                " GROUP BY A.plan_id, A.day_id, A.exe_status", new Object[]{status, plan});
        return planExerciseDao.getCursorData(simpleSQLiteQuery);
    }

    public LiveData<List<ExerciseDayProgress>> getExerciseDayProgress() {

        SimpleSQLiteQuery simpleSQLiteQuery = new SimpleSQLiteQuery("SELECT today_date, COUNT(*) as day_counter FROM (SELECT MAX (a.today_date) AS Latest FROM daily_exercise_progress a WHERE date(today_date,'-1 day')NOT IN (SELECT today_date FROM daily_exercise_progress)) AS B JOIN daily_exercise_progress c ON c.today_date >= b.Latest GROUP BY b.Latest");

        return categoryDao.getExerciseDayProgress(simpleSQLiteQuery);
    }

    public List<Achievement> getAllAchievements() {
        return achievementsDao.getAllAchievements();
    }

    public LiveData<List<Achievement>> getAllAchievementsObserve() {
        return achievementsDao.getAllAchievementsObserve();
    }

    public void updateRecipe(Recipe recipe) {
        recipeDao.updateRecipe(recipe);
    }

    public void updateDay(PlanExercise planExercise) {
        planExerciseDao.updateDay(planExercise);
    }

    public LiveData<List<UserWeight>> getUserWeight() {
        return userWeightDao.getUserWeight();
    }

    public LiveData<List<DailyExerciseProgress>> getLast30DaysProgress() {
        return dailyExerciseProgressDao.getLast30DayProgress();
    }

    public void insertUserWeight(UserWeight userWeight) {
        userWeightDao.inserUser(userWeight);
    }


    public void insertDailyExerciseProgress(DailyExerciseProgress dailyExerciseProgress) {
        dailyExerciseProgressDao.insertDayExerciseProgess(dailyExerciseProgress);
    }

    public void insertAchievement(Achievement achievement) {
        achievementsDao.insertAchievements(achievement);
    }

    public void updateAchievement(Achievement achievement) {
        achievementsDao.updateDay(achievement);
    }
}




