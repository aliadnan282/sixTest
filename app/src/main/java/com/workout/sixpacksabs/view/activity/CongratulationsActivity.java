package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.databinding.ActivityCongratulationsBinding;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.viewmodel.CongratulationViewModel;

public class CongratulationsActivity extends BaseActivity {


    AppPreference appPreference;
    CongratulationViewModel congratulationViewModel;
    ActivityCongratulationsBinding activityCongratulationsBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCongratulationsBinding = DataBindingUtil.setContentView(this, R.layout.activity_congratulations);
        appPreference = AppPreference.getInstance(this);

        AnalyticsManager.getInstance().sendAnalytics("completed_exercise_day_" + appPreference.getDay(), appPreference.getCategoryString());
        AdsManager.getInstance().loadNativeAppInstall(activityCongratulationsBinding.adContainer);
        AdsManager.getInstance().showInterstitialAd();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        congratulationViewModel = ViewModelProviders.of(this).get(CongratulationViewModel.class);
        getSupportActionBar().setTitle(getString(R.string.congratulation_activity));

        activityCongratulationsBinding.includeCongrat.finishExercise.setOnClickListener(view -> onBackPressed());
        activityCongratulationsBinding.includeCongrat.shareAchievements.setOnClickListener(view -> shareAchievements());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                congratulationViewModel.insertDailyExerciseProgress(new DailyExerciseProgress(AppUtils.getLongDate(), AppUtils.getStringDate()));
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent(CongratulationsActivity.this, DrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("home_fragment", true);
                startActivity(intent);
                finish();

            }
        }.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());
    }


    public void shareAchievements() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String shareText = "Wow! I have completed day " + appPreference.getDay() + " of Six pack abs workouts. Would you also try\n".concat(getString(R.string.app_share_text_intent)).concat("\n\n").concat( AppConstant.GOOGLE_PLAY_URL + getPackageName());
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }
}
