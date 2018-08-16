package com.workout.sixpacksabs.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.firebase.jobdispatcher.JobService;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.view.activity.PlayingActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_REMINDER;

public class ReminderService extends JobService {

    public static final String PRIMARY_CHANNEL = "default";
    private static final String TAG = ReminderService.class.getSimpleName();
    private static final String CHANNEL_ID = "123";
    Intent notificationIntent;
    PendingIntent pendingIntent;
    AppPreference appPreference;
    private NotificationManager manager;

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters jobParameters) {
        Log.i(TAG, "onStartJob- OnPost");
        //Context context = ReminderService.this;
        appPreference = AppPreference.getInstance(AppConstant.mContext);
        String planName = AppUtils.formatName(appPreference.getCategoryString());

        notificationIntent = new Intent(this, PlayingActivity.class);
        appPreference.setBoolean(SNOOZE_REMINDER, true);

       // notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
       // pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, FLAG_UPDATE_CURRENT);
        //--------Create Back stack for specific activity if app instance not exists----
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        // Get the PendingIntent containing the entire back stack
        pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //--------------------------------------------------------------

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    getString(R.string.reminder_title), NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan1);

            Notification.Builder notificationBuilder = getNotification1(getApplicationContext().getString(R.string.six_pack_time), getApplicationContext().getString(R.string.reminder_detail, AppUtils.formatName(planName)));
            getManager().notify(1, notificationBuilder.build());
        } else {

            Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());
            mBuilder.setSmallIcon(R.drawable.ic_notification_status_bar)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher_round))
                    .setContentTitle(getApplicationContext().getString(R.string.reminder_title))
                    .setContentText(getApplicationContext().getString(R.string.reminder_detail, AppUtils.formatName(planName)))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            assert getManager() != null;
            getManager().notify(1, mBuilder.build());
            jobFinished(jobParameters, false);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notification_status_bar)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.i(TAG, "onStopJob");
        /* true means, we're not done, please reschedule */
        return false;
    }
}