package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.pojo.CompleteExercise;
import com.workout.sixpacksabs.databinding.ActivityExerciseListBinding;
import com.workout.sixpacksabs.dragable.OnStartDragListener;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.view.adapter.ExerciseListAdapter;
import com.workout.sixpacksabs.viewmodel.ExerciseListViewModel;

import java.util.List;

public class ExerciseListActivity extends BaseActivity implements OnStartDragListener {

    private static final String TAG = ExerciseListActivity.class.getSimpleName();
    ActivityExerciseListBinding activityExerciseListBinding;
    ExerciseListAdapter exerciseListAdapter;
    ExerciseListViewModel exerciseListViewModel;
    //ItemTouchHelper itemTouchHelper;
    AppPreference appPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityExerciseListBinding = DataBindingUtil.setContentView(this, R.layout.activity_exercise_list);
        //Ads Initialization
        AdsManager.getInstance().showFacebookBannerAd(activityExerciseListBinding.adContainer);
        AdsManager.getInstance().showInterstitialAd();
        // Shared Preference initialization
        appPreference = AppPreference.getInstance(this);
        // Recyclerview Initializtion
        activityExerciseListBinding.rvExerciseList.setLayoutManager(new LinearLayoutManager(this));
        exerciseListAdapter = new ExerciseListAdapter(this, this);
        activityExerciseListBinding.rvExerciseList.setAdapter(exerciseListAdapter);

        //ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(exerciseListAdapter);
        //itemTouchHelper = new ItemTouchHelper(callback);
        //itemTouchHelper.attachToRecyclerView(activityExerciseListBinding.rvExerciseList);

        // Get a new or existing ViewModel from the ViewModelProvider.
        exerciseListViewModel = ViewModelProviders.of(this).get(ExerciseListViewModel.class);
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        exerciseListViewModel.getSelectedDayExercises(appPreference.getPlan(), appPreference.getDay()).observe(this, planDays -> exerciseListAdapter.setExerciseList(planDays));

        activityExerciseListBinding.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayingActivity.class);
            intent.putExtra("warm_up", true);
            intent.putExtra("plan", 0);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
    }
}