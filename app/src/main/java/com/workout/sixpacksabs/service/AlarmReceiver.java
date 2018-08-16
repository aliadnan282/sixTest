package com.workout.sixpacksabs.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.manager.CustomNotificationManager;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private void showNotification(Context context) {
        CustomNotificationManager.getInstance(AppConstant.mContext).showNotification();
    }

}