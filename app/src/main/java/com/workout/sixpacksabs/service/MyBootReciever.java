package com.workout.sixpacksabs.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.manager.CustomNotificationManager;

import java.util.Calendar;

import static com.workout.sixpacksabs.helper.AppConstant.CLOCK_TYPE;
import static com.workout.sixpacksabs.helper.AppConstant.NOTIFICATION_ENABLE;

public class MyBootReciever extends BroadcastReceiver {

    AppPreference appPreference;

    @Override
    public void onReceive(Context context, Intent intent) {

        appPreference = AppPreference.getInstance(context);

        if (appPreference.getBoolean(NOTIFICATION_ENABLE)) {
            Calendar calendar = Calendar.getInstance();
            int hourCurrent = calendar.get(Calendar.HOUR_OF_DAY);
            if (appPreference.getHourOfDay() < hourCurrent)
                CustomNotificationManager.getInstance(context).setNotification(appPreference.getHourOfDay() + CLOCK_TYPE, appPreference.getMinutesOfDay());
            else
                CustomNotificationManager.getInstance(context).setNotification(appPreference.getHourOfDay(), appPreference.getMinutesOfDay());

        }
    }
}
