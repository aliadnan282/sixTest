package com.workout.sixpacksabs.manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.service.AlarmReceiver;
import com.workout.sixpacksabs.view.activity.DrawerActivity;
import com.workout.sixpacksabs.view.activity.WorkoutActivity;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.ALARM_SERVICE;
import static com.workout.sixpacksabs.helper.AppConstant.ALARM_RECEIVER_INTENT_TRIGGER;
import static com.workout.sixpacksabs.helper.AppConstant.PRIMARY_CHANNEL;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;

/**
 * Created by AdnanAli on 2/14/2018.
 */

public class CustomNotificationManager {
    private static final String TAG = CustomNotificationManager.class.getSimpleName();
    static AppPreference appPreference;
    private static CustomNotificationManager instance;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private NotificationManager manager;

    public CustomNotificationManager() {
    }

    public static CustomNotificationManager getInstance(Context context) {
        if (instance == null) {
            instance = new CustomNotificationManager();
            appPreference = AppPreference.getInstance(mContext);
        }
        return instance;
    }

    public void setNotification(int timeHour, int timeMinute) {
        alarmManager = (AlarmManager) AppConstant.mContext.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(ALARM_RECEIVER_INTENT_TRIGGER);
        intent.setClass(AppConstant.mContext, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AppConstant.mContext, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, timeHour);
        calendar.set(Calendar.MINUTE, timeMinute);
        calendar.set(Calendar.SECOND, 00);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public void cancelNotification() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public void showNotification() {

        Notification.Builder notificationBuilder = null;
        String planName = appPreference.getCategoryString();
        Intent notificationIntent = new Intent(AppConstant.mContext, WorkoutActivity.class);
        notificationIntent.putExtra("alarm_service", true);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(AppConstant.mContext, 0, notificationIntent, FLAG_UPDATE_CURRENT);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                    mContext.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan1);

            notificationBuilder = new Notification.Builder(mContext, PRIMARY_CHANNEL)
                    .setContentTitle(mContext.getString(R.string.app_name))
                    .setContentText(mContext.getString(R.string.reminder_detail, AppUtils.formatName(planName)))
                    .setSmallIcon(R.drawable.ic_notification_status_bar)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            getManager().notify(1, notificationBuilder.build());
        } else {
            notificationBuilder = new Notification.Builder(mContext);
            notificationBuilder.setSmallIcon(R.drawable.ic_notification_status_bar)
                    .setLargeIcon(BitmapFactory.decodeResource(AppConstant.mContext.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(AppConstant.mContext.getString(R.string.app_name))
                    .setContentText(AppConstant.mContext.getString(R.string.reminder_detail, AppUtils.formatName(planName)))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            getManager().notify(1, notificationBuilder.build());
        }
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}