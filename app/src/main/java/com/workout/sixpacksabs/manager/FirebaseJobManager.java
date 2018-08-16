package com.workout.sixpacksabs.manager;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.service.ReminderService;

import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_NOTIFICATION;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_REMINDER;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_TIME;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;

public class FirebaseJobManager {
    private static FirebaseJobManager ourInstance;
    FirebaseJobDispatcher firebaseJobDispatcher;
    private AppPreference appPreference;
    Toast mToast;
    private FirebaseJobManager() {
        Driver driver = new GooglePlayDriver(AppConstant.mContext);
        firebaseJobDispatcher = new FirebaseJobDispatcher(driver);
        appPreference = AppPreference.getInstance(mContext);
    }

    public static FirebaseJobManager getInstance() {
        if (ourInstance == null)
            ourInstance = new FirebaseJobManager();

        return ourInstance;
    }

    public FirebaseJobDispatcher getFirebaseJobDispatcher() {
        return firebaseJobDispatcher;
    }

    public void setReminderJob(AppCompatActivity activity) {

        int startTime=appPreference.getIntValue(SNOOZE_TIME) < 60 ? 260:appPreference.getIntValue(SNOOZE_TIME) - 50;
        int endTime=appPreference.getIntValue(SNOOZE_TIME) < 60 ? 300 : appPreference.getIntValue(SNOOZE_TIME);

        Job constraintReminderJob = getFirebaseJobDispatcher().newJobBuilder()
                .setService(ReminderService.class)
                .setTag(SNOOZE_REMINDER)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow( startTime, endTime))
                .setReplaceCurrent(true)
                .build();
        getFirebaseJobDispatcher().schedule(constraintReminderJob);
        AnalyticsManager.getInstance().sendAnalytics("snooze_exercise","snooze_clicked");
        appPreference.setBoolean(SNOOZE_NOTIFICATION, true);
        showAToast(activity, "Snooze time set "+(endTime/60)+" minute");

    }
    public void showAToast (AppCompatActivity activity,String message){

        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        mToast.show();
    }
    public void cancelAllJobs() {
        if (firebaseJobDispatcher != null)
            firebaseJobDispatcher.cancelAll();
    }
}
