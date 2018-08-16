package com.workout.sixpacksabs.manager;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.workout.sixpacksabs.helper.AppConstant;

/**
 * Created by adnanali on 16/06/2017.
 */

public class AnalyticsManager {

    static AnalyticsManager INSTANCE;
    FirebaseAnalytics firebaseAnalytics;

    public AnalyticsManager() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(AppConstant.mContext);
    }

    public static AnalyticsManager getInstance() {
        INSTANCE = new AnalyticsManager();
        return INSTANCE;
    }

    public void sendAnalytics(String actionDetail, String actionName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, (actionDetail));
        bundle.putString(AppConstant.ACTION_TYPE, actionName);
        getFirebaseAnalytics().logEvent(actionName, bundle);
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        if (firebaseAnalytics == null)
            firebaseAnalytics = FirebaseAnalytics.getInstance(AppConstant.mContext);
        return firebaseAnalytics;
    }
}
