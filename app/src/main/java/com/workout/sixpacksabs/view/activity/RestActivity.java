package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.DailyExerciseProgress;
import com.workout.sixpacksabs.data.pojo.CompleteExercise;
import com.workout.sixpacksabs.databinding.ActivityRestBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.viewmodel.PlanDaysViewModel;

import java.util.List;


public class RestActivity extends AppCompatActivity {


    AppPreference appPreference;
    ActivityRestBinding activityRestBinding;
    private PlanDaysViewModel playingListViewModel;
    private List<CompleteExercise> completeExerciseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRestBinding = DataBindingUtil.setContentView(this, R.layout.activity_rest);
        AdsManager.getInstance().loadNativeAppInstall(activityRestBinding.adContainer);
        playingListViewModel = ViewModelProviders.of(this).get(PlanDaysViewModel.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appPreference = AppPreference.getInstance(this);
        getSupportActionBar().setTitle(getString(R.string.rest_day_text));
        activityRestBinding.finishExercise.setOnClickListener(view -> onBackPressed());
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
                completeExerciseList = playingListViewModel.getDayExerciseDetail(appPreference.getPlan(), appPreference.getDay());
                completeExerciseList.get(0).planExercise.setComplete(true);
                playingListViewModel.updateDay(completeExerciseList.get(0).planExercise);

                //Insert daily exercise record
                playingListViewModel.insertDailyExerciseProgress(new DailyExerciseProgress(AppUtils.getLongDate(), AppUtils.getStringDate()));

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent(RestActivity.this, DrawerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("home_fragment", true);
                startActivity(intent);
                finish();
            }
        }.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());

        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }
}
