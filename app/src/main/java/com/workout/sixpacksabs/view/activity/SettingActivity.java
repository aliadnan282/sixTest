package com.workout.sixpacksabs.view.activity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.workout.sixpacksabs.BuildConfig;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.databinding.ActivitySettingBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.in_app.util.SixPacksBilling;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.CustomNotificationManager;
import com.workout.sixpacksabs.manager.FirebaseJobManager;
import com.workout.sixpacksabs.viewmodel.SettingViewModel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

import static android.R.style.Theme_Material_Light_Dialog_NoActionBar;
import static com.workout.sixpacksabs.helper.AppConstant.NOTIFICATION_ENABLE;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_NOTIFICATION;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_REMINDER;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_TIME;
import static com.workout.sixpacksabs.helper.AppConstant.TTS_UNMUTE;
import static com.workout.sixpacksabs.helper.AppConstant.USER_EEA;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = DrawerActivity.class.getSimpleName();


    int switchCounter = 0;
    AppPreference appPreference;
    SwitchCompat switchNotifications;
    ActivitySettingBinding activitySettingBinding;
    SettingViewModel settingViewModel;
    ConsentForm form;
    private ConsentInformation consentInformation;
    private SixPacksBilling sixPacksBilling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        AdsManager.getInstance().showFacebookBannerAd(activitySettingBinding.adContainer);
        settingViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appPreference = AppPreference.getInstance(this);
        sixPacksBilling = new SixPacksBilling(this);
        sixPacksBilling.onCreate();
        // Notification switch settings
        switchCounter = 1;//appPreference.getBoolean(NOTIFICATION_ENABLE) == true ? 0 : ++switchCounter;
        activitySettingBinding.includeNotify.notifySwitch.setChecked(appPreference.getBoolean(NOTIFICATION_ENABLE));
        activitySettingBinding.includeNotify.snoozeSwitch.setChecked(appPreference.getBoolean(SNOOZE_NOTIFICATION));
        activitySettingBinding.includeNotify.notifySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (switchCounter > 0) {
                if (b) {
                    createDialogforTime();
                } else {
                    appPreference.setBoolean(NOTIFICATION_ENABLE, b);
                    cancelNotification();
                }
            }
            switchCounter++;
            activitySettingBinding.includeNotify.notifySwitch.setChecked(appPreference.getBoolean(NOTIFICATION_ENABLE));
        });
        activitySettingBinding.includeNotify.snoozeSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            activitySettingBinding.includeNotify.snoozeSwitch.setChecked(b);
            if (!b)
                FirebaseJobManager.getInstance().getFirebaseJobDispatcher().cancel(SNOOZE_REMINDER);
            else
                FirebaseJobManager.getInstance().setReminderJob(SettingActivity.this);

            appPreference.setBoolean(SNOOZE_NOTIFICATION, b);
        });

        // TTs switch settings
        activitySettingBinding.includeTts.muteTtsSwitch.setChecked(appPreference.getBoolean(TTS_UNMUTE));
        activitySettingBinding.includeTts.muteTtsSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            appPreference.setBoolean(TTS_UNMUTE, b);
        });
        activitySettingBinding.includeReset.layoutDietPlan.setOnClickListener(v -> {
            resetSixPackPlans(getString(R.string.reset_diet_plan), 0);
        });
        activitySettingBinding.includeReset.layoutBellyAbs.setOnClickListener(v -> {
            resetSixPackPlans(getString(R.string.reset_belly_abs), 1);
        });
        activitySettingBinding.includeReset.layoutSixPackAbs.setOnClickListener(v -> {
            resetSixPackPlans(getString(R.string.reset_six_pack_abs), 2);
        });
        activitySettingBinding.includeReset.layoutPerfectAbs.setOnClickListener(v -> {
            resetSixPackPlans(getString(R.string.reset_perfect_abs), 3);
        });
        activitySettingBinding.includeTime.numberPickerHorizontal.setListener(value -> {
            appPreference.setIntValue(SNOOZE_TIME, value * 60);
            activitySettingBinding.includeNotify.snoozeSwitch.setChecked(true);
            FirebaseJobManager.getInstance().setReminderJob(this);
        });

        activitySettingBinding.includePrivacy.changePrivacy.setOnClickListener(view -> {
                    consentInformation.setConsentStatus(ConsentStatus.UNKNOWN);
                    requestGoogleConsentForm();
                }
        );
        // By Default hide privacy if country other than EEA else show
        consentInformation = ConsentInformation.getInstance(this);
        if (appPreference.getBoolean(USER_EEA) && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activitySettingBinding.includePrivacy.cvSettingPrivacy.setVisibility(View.VISIBLE);
        } else {
            activitySettingBinding.includePrivacy.cvSettingPrivacy.setVisibility(View.GONE);
        }

    }


    private void resetSixPackPlans(String title, int position) {

        AlertDialog.Builder rateUsDialogBuilder = new AlertDialog.Builder(this);
        rateUsDialogBuilder.setCancelable(false)
                .setTitle(title)
                .setMessage(getResources().getString(R.string.reset_progress_detail))
                .setPositiveButton(getResources().getString(R.string.text_yes),
                        (dialog, which) -> {
                            switch (position) {
                                case 0:
                                    RecipeResetTask recipeTask = new RecipeResetTask();
                                    recipeTask.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());
                                    break;
                                case 1:
                                case 2:
                                case 3:
                                    PlanResetTask planResetTask = new PlanResetTask();
                                    planResetTask.executeOnExecutor(SixPackThreadPoolExecutor.getInstance(), position);
                                    break;

                            }
                            dialog.dismiss();
                        })
                .setNegativeButton(getResources().getString(R.string.text_no),
                        (dialog, which) -> dialog.dismiss()).show();
    }

    private void requestGoogleConsentForm() {
      /*  if (BuildConfig.DEBUG) {
            consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
            consentInformation.addTestDevice("BF1E10604107999DB67DA1C6570D3645");
        }*/
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
                        showGoogleConsentForm();
                    }
                } else {

                }
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
        form = new ConsentForm.Builder(SettingActivity.this, privacyUrl)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        sixPacksBilling.onActivityResult(requestCode, resultCode, data);
    }

    private void createDialogforTime() {

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;

        TimePickerDialog.OnTimeSetListener tPicker = (timePicker, hourOfDay, minute1) -> {
            appPreference.setBoolean(NOTIFICATION_ENABLE, true);
            switchCounter = 0;
            activitySettingBinding.includeNotify.notifySwitch.setChecked(appPreference.getBoolean(NOTIFICATION_ENABLE));
            appPreference.setHourOfDay(hourOfDay);
            appPreference.setHourOfDay(minute1);
            CustomNotificationManager.getInstance(mContext).setNotification(hourOfDay, minute1);
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            timePickerDialog = new TimePickerDialog(SettingActivity.this, Theme_Material_Light_Dialog_NoActionBar
                    , tPicker, hour, minute, false);
        } else {

            timePickerDialog = new TimePickerDialog(SettingActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog
                    , tPicker, hour, minute, false);
        }
        timePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                activitySettingBinding.includeNotify.notifySwitch.setChecked(appPreference.getBoolean(NOTIFICATION_ENABLE));
            }
        });
        timePickerDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AdsManager.getInstance().showInterstitialAd();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    private void cancelNotification() {
        CustomNotificationManager.getInstance(mContext).cancelNotification();
    }

    public class RecipeResetTask extends AsyncTask<Void, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(SettingActivity.this);

        public RecipeResetTask() {
        }

        protected void onPreExecute() {
            this.dialog.setMessage(getString(R.string.reset_progress));
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... lists) {
            List<Recipe> temp = settingViewModel.getAllRecipeUnobservable();
            for (Recipe recipe : temp) {
                recipe.setComplete(false);
                settingViewModel.updateRecipe(recipe);
            }
            return true;
        }

        protected void onPostExecute(final Boolean result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            AdsManager.getInstance().showInterstitialAd();
            Toast.makeText(SettingActivity.this, "Reset succesful", Toast.LENGTH_SHORT).show();
        }
    }

    public class PlanResetTask extends AsyncTask<Integer, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(SettingActivity.this);

        public PlanResetTask() {
        }


        protected void onPreExecute() {
            this.dialog.setMessage("Reset in proces...");
            this.dialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... lists) {
            List<PlanExercise> planExerciseList = settingViewModel.getPlanExercisesUnobservable(lists[0]);
            for (PlanExercise planExercise : planExerciseList) {
                planExercise.setComplete(false);
                settingViewModel.updatePlanExercise(planExercise);
            }
            return true;
        }

        protected void onPostExecute(final Boolean result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            AdsManager.getInstance().showInterstitialAd();
            Toast.makeText(SettingActivity.this, "Reset succesful", Toast.LENGTH_SHORT).show();
        }
    }

}
