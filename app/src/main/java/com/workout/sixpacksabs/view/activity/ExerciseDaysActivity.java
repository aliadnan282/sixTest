package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.DayProgress;
import com.workout.sixpacksabs.databinding.ActivityDaysBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.view.adapter.DaysAdapter;
import com.workout.sixpacksabs.viewmodel.PlanDaysViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.workout.sixpacksabs.helper.AppConstant.PLAN_NUMBER;
import static com.workout.sixpacksabs.helper.AppConstant.TOTAL_DAYS;

public class ExerciseDaysActivity extends BaseActivity {

    private static final String TAG = ExerciseDaysActivity.class.getSimpleName();
    ActivityDaysBinding activityDaysBinding;
    DaysAdapter daysAdapter;
    PlanDaysViewModel planDaysViewModel;
    AppPreference appPreference;
    List<DayProgress> mergeList = new ArrayList<>();
    List<DayProgress> completeDaysList = new ArrayList<>();
    List<DayProgress> inCompleteDaysList = new ArrayList<>();

    // Live data listener for get day exercise status
    //NOTE: This is all about if a user do some exercise in day and then jump to day 15---20 then Tracking down day progress becom a little bit tricky
    //Example:  if i do exercises at day 1 30percent and then jump to day 10 and complete 60 percent, now if you getlist based on completion(1) it will return two size list(index 0, 1)
    // but the actual day position is 1 and 10, how to map return list(index 0, 1) to adapter day 1 and day 10, which require replacement in adapter list
    Observer<List<DayProgress>> listObserverProgress = new Observer<List<DayProgress>>() {
        @Override
        public void onChanged(@Nullable List<DayProgress> dayProgresses) {

            // CASE 1:  All day have zero progress (No exercise started OR done ) so populate data on Adapter no other calculation needed
            if (dayProgresses.size() != 0 && dayProgresses.get(0).getExe_status() == 0 && dayProgresses.size() == TOTAL_DAYS) {
                inCompleteDaysList = dayProgresses;
                setDataOnAdapter(dayProgresses, inCompleteDaysList);
            }
            //--CASE 2: ------All day have 100 progress (inverse of CASE 1 ) populate data on Adapter
            else if (dayProgresses.size() != 0 && dayProgresses.get(0).getExe_status() == 1 && dayProgresses.size() == TOTAL_DAYS) {
                setDataOnAdapter(setPercentageForList(dayProgresses), inCompleteDaysList);
            }
            //CASE 3: --- Some days have non zero progress and fetch day list for exercise status 1
            else if (dayProgresses.size() != 0 && dayProgresses.get(0).getExe_status() == 0) {
                inCompleteDaysList = dayProgresses;
                merginglist(1);
            }
            //--CAES 4:------Inverse of CASE 3, Here second pass completed for fetching data against exercise status 1, now merg the final list and populate data on Adapter
            else if (dayProgresses.size() != 0 && dayProgresses.get(0).getExe_status() == 1) {
                completeDaysList = dayProgresses;
                mergeList = getMergeList(completeDaysList, inCompleteDaysList);
                setDataOnAdapter(mergeList, inCompleteDaysList);
            }
            // CASE 5:-------if no macht found pass for exercise status 1
            else {
                merginglist(1);
            }
        }

        private List<DayProgress> getMergeList(List<DayProgress> completeDaysList, List<DayProgress> inCompleteDaysList) {

            List<DayProgress> tempMergeList = new ArrayList<>(removeHalfCompletedDays(inCompleteDaysList));
            for (int i = 0; i < completeDaysList.size(); i++) {
                if (completeDaysList.get(i).getDay_percentage() == 0)
                    completeDaysList.get(i).setDay_percentage(100);
                else if (completeDaysList.get(i).getDay_percentage() != 0)
                    completeDaysList.get(i).setDay_percentage(100 - completeDaysList.get(i).getDay_percentage());
                Log.d(TAG, "getMergeList: =" + (completeDaysList.get(i).day_id) + "\t\t=" + completeDaysList.get(i).getDay_percentage());
                tempMergeList.add(completeDaysList.get(i).day_id - 1, completeDaysList.get(i));
            }
            return tempMergeList;
        }
    };

    private List<DayProgress> removeHalfCompletedDays(List<DayProgress> inCompleteDaysList) {

        for (int i = 0; i < inCompleteDaysList.size(); i++) {
            if (inCompleteDaysList.get(i).getDay_percentage() != 0) {
                inCompleteDaysList.remove(i);
                // if two consecutive index has value not equal to zero remove first index will skip the second one as i++ jump to the next index while list size decreased by 1
                i--;
            }
        }
        return inCompleteDaysList;
    }

    private List<DayProgress> setPercentageForList(List<DayProgress> dayProgresses) {
        for (DayProgress dayProgress :
                dayProgresses) {
            dayProgress.setDay_percentage(100);
        }
        return dayProgresses;
    }

    private void setDataOnAdapter(List<DayProgress> dayProgressList, List<DayProgress> inCompleteDaysList) {
        if (inCompleteDaysList.size() == 0 && dayProgressList.size() == TOTAL_DAYS)
            activityDaysBinding.tvDaysLeft.setText(getString(R.string.complete_all_days));
        else
            activityDaysBinding.tvDaysLeft.setText(getString(R.string.left_days, inCompleteDaysList.size()));
        activityDaysBinding.tvDaysPercentage.setText(Math.ceil(100 - (inCompleteDaysList.size() / (float) TOTAL_DAYS * 100f)) + "%");
        activityDaysBinding.daysProgressbar.setProgress(TOTAL_DAYS - inCompleteDaysList.size());

        daysAdapter.setPlanDays(dayProgressList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDaysBinding = DataBindingUtil.setContentView(this, R.layout.activity_days);
        AdsManager.getInstance().showFacebookBannerAd(activityDaysBinding.adContainer);
        appPreference = AppPreference.getInstance(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            appPreference.setPlan(extras.getInt(PLAN_NUMBER, 1));
        }
        if (appPreference.getPlan() == 2)
            Picasso.get().load(R.drawable.daily_perfect_abs)
                    .into(activityDaysBinding.ivPlanType);
        else if (appPreference.getPlan() == 3)
            Picasso.get().load(R.drawable.daily_six_packs_abs)
                    .into(activityDaysBinding.ivPlanType);


        activityDaysBinding.rvDays.setLayoutManager(new LinearLayoutManager(this));
        daysAdapter = new DaysAdapter(this);
        activityDaysBinding.rvDays.setNestedScrollingEnabled(false);
        activityDaysBinding.rvDays.setAdapter(daysAdapter);
        setSupportActionBar(activityDaysBinding.toolbar);
        getSupportActionBar().setTitle(AppUtils.formatName(appPreference.getCategoryString()));

        activityDaysBinding.daysProgressbar.setProgress(appPreference.getDay());

        // Get a new or existing ViewModel from the ViewModelProvider.
        planDaysViewModel = ViewModelProviders.of(this).get(PlanDaysViewModel.class);

        // Merging two list based on exercise status which is 0 OR 1
        merginglist(0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

    }

    // this function will execute two time and then unregister reciever
    private void merginglist(int status) {
        if (planDaysViewModel.daysProgress != null && planDaysViewModel.daysProgress.hasActiveObservers()) {
            planDaysViewModel.daysProgress.removeObservers(this);
        }
        // First time get day exercise list base on exercise status
        planDaysViewModel.getDayProgress(appPreference.getPlan(), status).observe(this, listObserverProgress);
    }


}

/*     Firebase DB getting data

 FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("plan_categories");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Category category = dataSnapshot1.getValue(Category.class);
                    Log.d(TAG, "onDataChange: id="+category.getId());
                }
                long post = dataSnapshot.getChildrenCount();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                Toast.makeText(MainActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        };
        myRef.addValueEventListener(postListener);
 */