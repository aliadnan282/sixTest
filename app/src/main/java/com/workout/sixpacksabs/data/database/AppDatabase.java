package com.workout.sixpacksabs.data.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.workout.sixpacksabs.data.dao.AchievementsDao;
import com.workout.sixpacksabs.data.dao.CategoryDao;
import com.workout.sixpacksabs.data.dao.DailyExerciseProgressDao;
import com.workout.sixpacksabs.data.dao.ExerciseDetailDao;
import com.workout.sixpacksabs.data.dao.PlanExerciseDao;
import com.workout.sixpacksabs.data.dao.RecipeDao;
import com.workout.sixpacksabs.data.dao.UserWeightDao;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.data.entity.ExerciseDetail;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.data.entity.UserWeight;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.helper.JsonManager;
import com.workout.sixpacksabs.model.Day;
import com.workout.sixpacksabs.model.Exercise;
import com.workout.sixpacksabs.model.Plan;
import com.workout.sixpacksabs.model.PlanModel;

import java.util.List;

import static com.workout.sixpacksabs.helper.AppConstant.IS_FIRST_RUN;

/**
 * Created by AdnanAli on 3/12/2018.
 */

@Database(entities = {Category.class, PlanExercise.class, Recipe.class, ExerciseDetail.class, UserWeight.class, Achievement.class, DailyExerciseProgress.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "six_pack";
    static AppPreference appPreference;
    private static AppDatabase INSTANCE;
    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     */
    private static Callback sRoomDatabaseCallback = new Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            if (!appPreference.getBoolean(IS_FIRST_RUN)) {
                db.execSQL("CREATE VIEW IF NOT EXISTS plan_day_total_v\n" +
                        "AS \n" +
                        "SELECT\n" +
                        "plan_id  , day_id ,  count(*) as total\n" +
                        "from plan_exercise\n" +
                        "group by plan_id  , day_id");
                // If you want to keep the data through app restarts,
                // comment out the following line.
                new PopulateDbAsync(INSTANCE).execute();
            }
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            appPreference = AppPreference.getInstance(context);
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .addCallback(sRoomDatabaseCallback)
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract CategoryDao categoryDao();

    public abstract PlanExerciseDao planExerciseDao();

    public abstract RecipeDao recipeDao();

    public abstract UserWeightDao userWeightDao();

    public abstract AchievementsDao achievementsDao();

    public abstract ExerciseDetailDao exerciseDetailDao();

    public abstract DailyExerciseProgressDao dailyExerciseProgressDao();

    /**
     * Populate the database in the background.
     * If you want to start with more words, just add them.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private static final int TOTAL_PLAN_DAYS = 21;
        private static final int TOTAL_PLAN = 9;
        RecipeDao recipeDao;
        AchievementsDao achievementsDao;
        private CategoryDao categoryDao;
        private PlanExerciseDao planExerciseDao;
        private ExerciseDetailDao exerciseDetailDao;
        private UserWeightDao userWeightDao;
        private DailyExerciseProgressDao dailyExerciseProgressDao;

        PopulateDbAsync(AppDatabase db) {
            categoryDao = db.categoryDao();
            planExerciseDao = db.planExerciseDao();
            recipeDao = db.recipeDao();
            exerciseDetailDao = db.exerciseDetailDao();
            achievementsDao = db.achievementsDao();
            userWeightDao = db.userWeightDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            // mDao.deleteAllCategory();

            insertCategoryData();
            insertPlanDaysData();
            insertRecipeData();
            insertExerciseDetail();
            insertAchievements();
            insertUserWeight();

            return null;
        }

        private void insertUserWeight() {
            userWeightDao.inserUser(new UserWeight(AppUtils.getLongDate(), AppUtils.getLongDate(), 60));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appPreference.setBoolean(IS_FIRST_RUN, true);
        }

        private void insertAchievements() {

            for (int i = 1; i <= 9; i++) {
                Achievement achievement = new Achievement(i, i + (i - 1), false);
                achievementsDao.insertAchievements(achievement);
            }
        }

        private void insertExerciseDetail() {
            List<ExerciseDetail> exerciseDetailList = JsonManager.getInstance().getExerciseDetailList();
            for (ExerciseDetail exerciseDetail : exerciseDetailList) {
                exerciseDetailDao.insertCategory(exerciseDetail);
            }
        }

        private void insertRecipeData() {
            for (int i = 1; i <= 30; i++) {
                Recipe recipe = new Recipe(i, false);
                recipeDao.insertRecipe(recipe);
            }
        }

        private void insertPlanDaysData() {
            PlanModel planModel = JsonManager.getInstance().getPlanExercise();
            for (Plan plan : planModel.getPlan()) {
                for (Day day : plan.getDay()) {
                    for (Exercise exercise : day.getExerciseList()) {
                        PlanExercise planExercise1 = new PlanExercise(plan.getPlanId(), day.getDayId(), exercise.getExerciseId(), exercise.getExerciseName(), exercise.getExerciseReps(), exercise.getExerciseStatus());
                        planExerciseDao.insertPlanDays(planExercise1);
                    }
                }
            }
        }

        private void insertCategoryData() {
            List<Category> categoryList = JsonManager.getInstance().getCategoriseModel();
            for (Category category : categoryList) {
                categoryDao.insertCategory(category);
            }
        }
    }
}
