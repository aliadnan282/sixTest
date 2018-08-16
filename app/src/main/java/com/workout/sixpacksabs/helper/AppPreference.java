package com.workout.sixpacksabs.helper;

import android.content.Context;
import android.content.SharedPreferences;

import static com.workout.sixpacksabs.helper.AppConstant.BELLY_ABS;
import static com.workout.sixpacksabs.helper.AppConstant.CATEGORY_TYPE;
import static com.workout.sixpacksabs.helper.AppConstant.DAY_HOUR_NOTIFY;
import static com.workout.sixpacksabs.helper.AppConstant.DAY_MINUTE_NOTIFY;
import static com.workout.sixpacksabs.helper.AppConstant.DAY_NUMBER;
import static com.workout.sixpacksabs.helper.AppConstant.PERFECT_ABS;
import static com.workout.sixpacksabs.helper.AppConstant.PLAN_NUMBER;
import static com.workout.sixpacksabs.helper.AppConstant.SIX_PACK_ABS;


public class AppPreference {

    private static final String PREF_NAME = "com.six.pack.PREF_NAME";

    private static AppPreference sInstance;
    private final SharedPreferences mPref;

    public AppPreference(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized AppPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppPreference(context);
        }
        return sInstance;
    }

    public String getCalculatorValue(String key) {
        return mPref.getString(key, null);
    }

    public void setCalculatorValue(String key, String value) {
        mPref.edit()
                .putString(key, value)
                .apply();
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .apply();
    }

    public String getCategoryString() {
        if (getPlan() == 1)
            return BELLY_ABS;
        if (getPlan() == 2)
            return PERFECT_ABS;
        if (getPlan() == 3)
            return SIX_PACK_ABS;
        return BELLY_ABS;
    }

    public void setCategoryString(String value) {
        mPref.edit()
                .putString(CATEGORY_TYPE, value)
                .apply();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }

    public void setBoolean(String key, boolean value) {
        mPref.edit()
                .putBoolean(key, value)
                .apply();
    }

    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    public void setIntValue(String key, int value) {
        mPref.edit()
                .putInt(key, value)
                .apply();
    }

    public int getIntValue(String key) {
        return mPref.getInt(key, 1);
    }

    public int getPlan() {
        return mPref.getInt(PLAN_NUMBER, 1);
    }

    public void setPlan(int value) {
        mPref.edit()
                .putInt(PLAN_NUMBER, value)
                .apply();
    }

    public int getDay() {
        return mPref.getInt(DAY_NUMBER, 1);
    }

    public void setDay(int value) {
        mPref.edit()
                .putInt(DAY_NUMBER, value)
                .apply();
    }

    public long getLongValue(String key) {
        return mPref.getLong(key, 1);
    }

    public void setLongValue(String key, long value) {
        mPref.edit()
                .putLong(key, value)
                .apply();
    }

    public int getHourOfDay() {
        return mPref.getInt(DAY_HOUR_NOTIFY, 7);
    }

    public void setHourOfDay(int hour) {
        mPref.edit()
                .putInt(DAY_HOUR_NOTIFY, hour)
                .apply();
    }

    public int getMinutesOfDay() {
        return mPref.getInt(DAY_MINUTE_NOTIFY, 00);
    }

    public void setMinutesOfDay(int minute) {
        mPref.edit()
                .putInt(DAY_MINUTE_NOTIFY, minute)
                .apply();
    }

}
