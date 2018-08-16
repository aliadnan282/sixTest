package com.workout.sixpacksabs.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.workout.sixpacksabs.BuildConfig;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.ActivityMainBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.in_app.util.SixPacksBilling;
import com.workout.sixpacksabs.interfaces.CloseAppListener;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.CustomNotificationManager;
import com.workout.sixpacksabs.view.fragment.ExitDialog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

import static com.workout.sixpacksabs.helper.AppConstant.CLOCK_TYPE;
import static com.workout.sixpacksabs.helper.AppConstant.IS_FIRST_RUN;
import static com.workout.sixpacksabs.helper.AppConstant.NOTIFICATION_ENABLE;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_TIME;
import static com.workout.sixpacksabs.helper.AppConstant.TTS_UNMUTE;
import static com.workout.sixpacksabs.helper.AppConstant.USER_EEA;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;

public class MainActivity extends DaggerAppCompatActivity implements CloseAppListener {
    private static final String TAG = "MainActivity";
    ActivityMainBinding activityMainBinding;
    ConsentForm form;
    @Inject
    AppPreference appPreference;
    @Inject
    SixPacksBilling sixPacksBilling;
    @Inject
    ExitDialog exitDialog;
    private ConsentInformation consentInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setActivity(this);
        sixPacksBilling.onCreate();
        setNotificationAlarm();
        if (!appPreference.getBoolean(getString(R.string.is_consent_first_time_requested)) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestGoogleConsentForm();
        } else {
            AdsManager.getInstance().showFacebookInterstitialAd();
        }
    }

    private void setNotificationAlarm() {
        if (!appPreference.getBoolean(IS_FIRST_RUN)) {
            appPreference.setBoolean(TTS_UNMUTE, true);
            appPreference.setBoolean(NOTIFICATION_ENABLE, true);
            appPreference.setIntValue(SNOOZE_TIME, 300);  // Snooze time set by default 5 minutes
            Calendar calendar = Calendar.getInstance();
            CustomNotificationManager.getInstance(mContext).setNotification(calendar.get(HOUR_OF_DAY) + CLOCK_TYPE, calendar.get(MINUTE));
        }
    }

    public void openWorkoutActivity() {
        Intent intent = new Intent(this, WorkoutActivity.class);
        startNextActivity(intent);
    }

    public void openDietPlanActivity() {
        Intent intent = new Intent(this, RecipeDetailActivity.class);
        startNextActivity(intent);
    }

    public void openReportActivity() {
        Intent intent = new Intent(this, ReportActivity.class);
        startNextActivity(intent);
    }

    private void startNextActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
    }

    @Override
    public void onBackPressed() {
        exitDialog.showDialog();
    }

    private void requestGoogleConsentForm() {
        consentInformation = ConsentInformation.getInstance(this);
        if (BuildConfig.DEBUG) {
            consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
            consentInformation.addTestDevice("BF1E10604107999DB67DA1C6570D3645");
        }
        consentInformation.requestConsentInfoUpdate(new String[]{getString(R.string.publisher_id)}, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d(TAG, consentStatus.name());
                // User's consent status successfully updated
                // check where user is located either not in EEA or in EEA
                // case 1 (not in EEA): if user is not located in EEA, no consent is required, you can request to Google Mobile Ads SDK
                // case 2 (in EEA): consent is required
                if (consentInformation.isRequestLocationInEeaOrUnknown()) {
                    // user is located in EEA, consent is required
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        appPreference.setBoolean(USER_EEA, true);
                        showGoogleConsentForm();
                    }
                } else {
                    appPreference.setBoolean(USER_EEA, false);
                }
                appPreference.setBoolean(getString(R.string.is_consent_first_time_requested), true);
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                // User's consent status failed to update
                Log.d(TAG, "onFailedToUpdateConsentInfo: " + errorDescription);
            }
        });
    }

    private void showGoogleConsentForm() {
        // consent not provided, collect consent using Google-rendered consent form
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getString(R.string.privacy_policy_url));
        } catch (MalformedURLException e) {
            Log.e(TAG, "onConsentInfoUpdated: " + e.getLocalizedMessage());
        }
        form = new ConsentForm.Builder(this, privacyUrl)
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "onConsentFormLoaded");
                        form.show();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        // Consent form was closed.
                        Log.d(TAG, "onConsentFormClosed");
                        if (consentStatus == ConsentStatus.PERSONALIZED) {
                            appPreference.setBoolean(getString(R.string.npa), false);
                        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                            appPreference.setBoolean(getString(R.string.npa), true);
                        } else if (userPrefersAdFree) {
                            sixPacksBilling.purchaseRemoveAds();
                        }
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        // Consent form error.
                        Log.d(TAG, "onConsentFormError: " + errorDescription);
                    }
                })
                .build();
        form.load();
    }

    @Override
    public void closeApp() {
        finish();
    }
}
