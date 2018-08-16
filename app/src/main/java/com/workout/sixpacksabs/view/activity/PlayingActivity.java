package com.workout.sixpacksabs.view.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.pojo.CompleteExercise;
import com.workout.sixpacksabs.databinding.ActivityPlayingBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.helper.SixPackThreadPoolExecutor;
import com.workout.sixpacksabs.interfaces.DialogFinishListener;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.manager.FirebaseJobManager;
import com.workout.sixpacksabs.manager.TTSManager;
import com.workout.sixpacksabs.model.PlayingExerciseModel;
import com.workout.sixpacksabs.view.fragment.WarmUpDialogFragment;
import com.workout.sixpacksabs.viewmodel.PlanDaysViewModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.workout.sixpacksabs.helper.AppConstant.ASSETS_IMAGES;
import static com.workout.sixpacksabs.helper.AppConstant.SNOOZE_REMINDER;
import static com.workout.sixpacksabs.helper.AppConstant.TTS_UNMUTE;
import static com.workout.sixpacksabs.model.PlayingExerciseModel.PLAYING_STATUS.FINISHED;
import static com.workout.sixpacksabs.model.PlayingExerciseModel.PLAYING_STATUS.PAUSED;
import static com.workout.sixpacksabs.model.PlayingExerciseModel.PLAYING_STATUS.PLAYING;

public class PlayingActivity extends BaseActivity implements DialogFinishListener {
    private static final int TIME_EXERCISE = 122;
    private static final int ANIMATION_TIME = 1000;
    private static final int EXERCISE_TIME_INTERVAL = 1000;
    private static final long EXERCISE_REPS_INTERVAL = 2000;
    private static final long INTERVAL_DELAY = 1000l;
    public static long TIME = 15000l;
    final String TAG = PlayingActivity.class.getSimpleName();
    // TO store total time for an exercise with break or Pause
    long TIMEOUT = 10000;
    // TO store total time for an exercise without any break or Pause for TTS Half the time sound
    long TOTAL_TIME = 0;
    // TO store total  done time for an exercise done without any break or Pause for TTS Half the time sound
    long TOTAL_TIME_DONE = 0;
    ActivityPlayingBinding activityPlayingBinding;
    List<CompleteExercise> completeExerciseList;
    PlanDaysViewModel playingListViewModel;
    AppPreference appPreference;
    LoadData loadData;
    TimerTask timerTask;
    Timer timer;
    int elapsed = 0;
    private int COUNTER = 0;
    private int progressStatus = 0;
    private AnimationDrawable animationDrawable;
    // Handle home button clikc which trigger onstop show or hide warm up layout
    private boolean isStopCalled = false;
    //private CountDownTimer mCountDownTimer;
    private PlayingExerciseModel playingExerciseModel;
    private Handler customHandler;
    //------Warm up layout views and Data Controller -----------------------
    private Runnable updateTimerThread = new Runnable() {

        public void run() {
            //write here whaterver you want to repeat
            if (TIME == 0) {
                AppUtils.playAudio(R.raw.ding);
                activityPlayingBinding.pbNexExe.setText(String.valueOf(0));
                activityPlayingBinding.pbNexExe.setValue((float) (0));
                customHandler.removeCallbacks(updateTimerThread);
                activityPlayingBinding.layoutWarmUp.setVisibility(View.GONE);
                activityPlayingBinding.layoutPlay.setVisibility(View.VISIBLE);
                TTSManager.getInstance(getApplication()).stop();
                setDataInUI();
                playCurrentExercise();
                TIME = 15000l;
            } else {
                AppUtils.playAudio(R.raw.clock);
                if (TIME <= 3000)
                    TTSManager.getInstance(getApplication()).play(String.valueOf(TIME / INTERVAL_DELAY));

                activityPlayingBinding.pbNexExe.setText(String.valueOf(TIME / INTERVAL_DELAY));
                activityPlayingBinding.pbNexExe.setValue((float) (TIME / INTERVAL_DELAY));
                customHandler.postDelayed(this, INTERVAL_DELAY);
                TIME -= INTERVAL_DELAY;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activityPlayingBinding = DataBindingUtil.setContentView(this, R.layout.activity_playing);
        appPreference = AppPreference.getInstance(this);

        AnalyticsManager.getInstance().sendAnalytics("start_exercise_day_" + appPreference.getDay(), appPreference.getCategoryString());
        AdsManager.getInstance().showFacebookBannerAd(activityPlayingBinding.adContainer);
        AdsManager.getInstance().showFacebookBannerAd(activityPlayingBinding.adContainerDetail);
        AdsManager.getInstance().loadNativeAppInstall(activityPlayingBinding.adContainerPause);
        AdsManager.getInstance().loadNativeAppInstall(activityPlayingBinding.adContainerWarmUp);

        playingExerciseModel = new PlayingExerciseModel();
        playingListViewModel = ViewModelProviders.of(this).get(PlanDaysViewModel.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        loadData = new LoadData();
        loadData.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());


        activityPlayingBinding.ivPause.setOnClickListener(view -> {
            pauseCurrentExercise(false);
            showPauseLayout();
        });
        activityPlayingBinding.playExerciseInfo.setOnClickListener(view -> {
            pauseCurrentExercise(true);
            activityPlayingBinding.layoutExeDetail.setVisibility(View.VISIBLE);
            activityPlayingBinding.layoutPause.setVisibility(View.GONE);
            showExerciseDetailLayout();
        });
        activityPlayingBinding.playExerciseVideo.setOnClickListener(view -> {
            //Playing video against given ID
            startActivity(AppUtils.getVideoUrl(completeExerciseList.get(COUNTER).getDayExerciseDetail.get(0).getExeLink()));
        });

    }

    //-----------Pause Layout views and data controller------------------------
    private void showPauseLayout() {
        // Pause layout controlles
        activityPlayingBinding.fbPauseDialogInfo.setOnClickListener(view -> {
            //pauseCurrentExercise();
            showExerciseDetailLayout();
            activityPlayingBinding.layoutExeDetail.setVisibility(View.VISIBLE);

        });
        activityPlayingBinding.fbPauseDialogVideo.setOnClickListener(view -> {
            //Playing video against given ID
            startActivity(AppUtils.getVideoUrl(completeExerciseList.get(COUNTER).getDayExerciseDetail.get(0).getExeLink()));
        });
        activityPlayingBinding.fbPauseDialogPlay.setOnClickListener(view -> {
            if (isStopCalled)
                setDataInUI();
            playCurrentExercise();
            isStopCalled = false;
        });
        Picasso.get()
                .load(ASSETS_IMAGES + completeExerciseList.get(COUNTER).planExercise.getExeName() + ".png")
                .into(activityPlayingBinding.ivDialogPauseExe);

        activityPlayingBinding.tvDialogPauseExeTitle.setText(AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()));
        if (isExerciseTimeOrReps()) {
            activityPlayingBinding.tvPauseDialogCounter.setText(getString(R.string.seconds_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps()));
        } else {
            activityPlayingBinding.tvPauseDialogCounter.setText(getString(R.string.reps_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps()));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!appPreference.getBoolean(TTS_UNMUTE))
            TTSManager.getInstance(getApplication()).mute();
        else
            TTSManager.getInstance(getApplication()).unmute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (COUNTER > -1 && COUNTER < completeExerciseList.size())
            pauseCurrentExercise(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (COUNTER > -1 && COUNTER < completeExerciseList.size()) {

            pauseCurrentExercise(false);
        }
    }

    //-----------Detail layout views and Data Controller----------------
    private void showExerciseDetailLayout() {
        activityPlayingBinding.detailIvCancel.setOnClickListener(view -> {
            activityPlayingBinding.layoutExeDetail.setVisibility(View.GONE);
            if (activityPlayingBinding.layoutPause.getVisibility() != View.VISIBLE)
                playCurrentExercise();

        });
        activityPlayingBinding.detailExeDescription.setText(Html.fromHtml(completeExerciseList.get(COUNTER).getDayExerciseDetail.get(0).getExeDescription()));
        activityPlayingBinding.detailExeTitle.setText(AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()));
        Picasso.get()
                .load(ASSETS_IMAGES + completeExerciseList.get(COUNTER).planExercise.getExeName() + ".png")
                .into(activityPlayingBinding.detailExeImage);

       /* ExerciseDetailDialogFragment dialog = ExerciseDetailDialogFragment.newInstance(completeExerciseList.get(COUNTER).planExercise.getExeName(), completeExerciseList.get(COUNTER).getDayExerciseDetail.get(0).getExeDescription());
        dialog.setDialogListener(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        dialog.show(ft, ExerciseDetailDialogFragment.TAG);*/
    }

    private void playExercise() {
        if (!playingExerciseModel.getmPlayingStatus().equals(PLAYING))
            playCurrentExercise();
        else if (playingExerciseModel.getmPlayingStatus().equals(PLAYING)) {
            pauseCurrentExercise(false);
        }
    }

    private void playCurrentExercise() {
        activityPlayingBinding.layoutPlay.setVisibility(View.VISIBLE);
        activityPlayingBinding.layoutPause.setVisibility(View.GONE);
        playingExerciseModel.setmPlayingStatus(PLAYING);
        if (completeExerciseList.get(COUNTER).planExercise.getExeId() < TIME_EXERCISE)
            startAnimation();

        startTimer();
    }

    private void skipPreviousExercise() {
        COUNTER--;
        // Reset done time for an exercise
        TOTAL_TIME_DONE = 0;
        if (COUNTER < 0) {
            showCancelDialog();
            COUNTER++;
        } else if (COUNTER > -1) {
            setDataInUI();
        }
    }

    private void playNextOrPreviousTTS(String string) {
        if (isExerciseTimeOrReps()) {
            TTSManager.getInstance(getApplication()).play(string + " " + AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()) + completeExerciseList.get(COUNTER).planExercise.getExerciseReps() + " " + getString(R.string.seconds));
        } else {
            TTSManager.getInstance(getApplication()).play(string + " " + AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()) + " " + completeExerciseList.get(COUNTER).planExercise.getExerciseReps() + " " + getString(R.string.reps));
        }
    }

    public void showCancelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.cancel_timer))
                .setMessage(getString(R.string.cancel_timer_detail))
                .setPositiveButton(getString(R.string.text_snooze), (dialogInterface, i) -> {

                    dialogInterface.dismiss();
                    TTSManager.getInstance(getApplication()).shutdown();
                    FirebaseJobManager.getInstance().setReminderJob(PlayingActivity.this);
                    finish();
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                })
                .setNegativeButton(getString(R.string.text_quit), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    FirebaseJobManager.getInstance().getFirebaseJobDispatcher().cancel(SNOOZE_REMINDER);
                    Intent intent = new Intent(PlayingActivity.this, DrawerActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("home_fragment", true);
                    startActivity(intent);
                    finish();
                })
                .create()
                .show();
    }

    private void skipNextExercise() {


        if (COUNTER < completeExerciseList.size()) {
            setMarkDayAsCompleted(COUNTER);
            TTSManager.getInstance(getApplication()).stop();
        }
        // Reset done time for an exercise
        TOTAL_TIME_DONE = 0;


    }

    private void setMarkDayAsCompleted(int tempCounter) {
        final int counter = tempCounter;
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(TAG, "doInBackground: COUNTER=" + counter);
                completeExerciseList.get(counter).planExercise.setComplete(true);
                playingListViewModel.updateDay(completeExerciseList.get(counter).planExercise);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.d(TAG, "onPostExecute: save" + COUNTER);
                COUNTER++;
                if (COUNTER < completeExerciseList.size()) {
                    if (activityPlayingBinding.layoutPause.getVisibility() != View.VISIBLE)
                        showWarmUpLayout();
                    else
                        setDataInUI();
                } else {
                    startFinishActivity();
                }
            }
        }.executeOnExecutor(SixPackThreadPoolExecutor.getInstance());
    }

    private void startFinishActivity() {
        removeWarmUpHandler();
        Intent intent = new Intent(this, CongratulationsActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

    }

    private AnimationDrawable createAnimationDrawable() {

        AnimationDrawable newAnim = new AnimationDrawable();
        // Log.d(TAG, "createAnimationDrawable: " + completeExerciseList.get(COUNTER).planExercise.getExeName() + ".png" + "\t\t createAnimationDrawable=" + completeExerciseList.get(COUNTER).planExercise.getExeName() + "0.png");
        newAnim.addFrame(AppUtils.getDrawable(completeExerciseList.get(COUNTER).planExercise.getExeName() + ".png"), ANIMATION_TIME);

        newAnim.addFrame(AppUtils.getDrawable(completeExerciseList.get(COUNTER).planExercise.getExeName() + "0.png"), ANIMATION_TIME);
        newAnim.setOneShot(false);
        return newAnim;
    }

    private void setDataInUI() {

        activityPlayingBinding.completedProgressbar.setMax(completeExerciseList.get(COUNTER).planExercise.getExerciseReps());
        activityPlayingBinding.completedProgressbar.setProgress(completeExerciseList.get(COUNTER).planExercise.getExerciseReps());
        activityPlayingBinding.exerciseProgressBar.setMax(completeExerciseList.size() - 1);
        activityPlayingBinding.exerciseProgressBar.setProgress(COUNTER);
        playingExerciseModel.setmTotalTime(completeExerciseList.get(COUNTER).planExercise.getExerciseReps());
        playingExerciseModel.setmPlayingStatus(FINISHED);
        playingExerciseModel.setmCompletedTime(0);
        playingExerciseModel.setmPausedTime(0);

        activityPlayingBinding.exeTitlePlay.setText(AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()));

        if (isExerciseTimeOrReps()) {
            activityPlayingBinding.completedProgressbar.setText(AppUtils.convertSecondsToHMmSs(completeExerciseList.get(COUNTER).planExercise.getExerciseReps()));
            activityPlayingBinding.tvPlayCounter.setText(getString(R.string.seconds_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps()));
            activityPlayingBinding.ivPlay.setBackground(null);
            Picasso.get()
                    .load(ASSETS_IMAGES + completeExerciseList.get(COUNTER).planExercise.getExeName() + ".png")
                    .into(activityPlayingBinding.ivPlay);

        } else {
            activityPlayingBinding.completedProgressbar.setText(playingExerciseModel.getmTotalTime() + "/" + playingExerciseModel.getmTotalTime());
            activityPlayingBinding.tvPlayCounter.setText(getString(R.string.reps_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps()));
            activityPlayingBinding.ivPlay.setBackground(null);
            Picasso.get()
                    .load(".png")
                    .into(activityPlayingBinding.ivPlay);
            activityPlayingBinding.ivPlay.setBackground(createAnimationDrawable());
        }
        if (activityPlayingBinding.layoutPause.getVisibility() == View.VISIBLE)
            showPauseLayout();
        else if (activityPlayingBinding.layoutWarmUp.getVisibility() == View.VISIBLE)
            showWarmUpLayout();
    }

    private boolean isExerciseTimeOrReps() {
        return completeExerciseList.get(COUNTER).planExercise.getExeId() >= TIME_EXERCISE;
    }

    public void startAnimation() {
        runOnUiThread(() -> {
            animationDrawable = (AnimationDrawable) activityPlayingBinding.ivPlay.getBackground();
            if (animationDrawable != null) {
                animationDrawable.setOneShot(false);
                animationDrawable.start();
            }
        });
    }

    private void stopAnimation() {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    private void startTimer() {

        cancelTimer();
        TOTAL_TIME = completeExerciseList.get(COUNTER).planExercise.getExerciseReps() * 1000;
        long paused = playingExerciseModel.getmPausedTime() * 1000;
        if (paused > 0) {
            TIMEOUT = paused;
        } else {
            TIMEOUT = completeExerciseList.get(COUNTER).planExercise.getExerciseReps() * 1000;
            TTSManager.getInstance(getApplication()).play(getString(R.string.to_the_exercise_text).concat(AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()).concat(" ").concat(getExerciseRepsOrTimer())));

        }

        Log.d(TAG, "startTimer:TIMEOUT= " + TIMEOUT);

        elapsed = 0;
        timerTask = new TimerTask() {
            @Override
            public void run() {
                elapsed += EXERCISE_TIME_INTERVAL;
                TOTAL_TIME_DONE += EXERCISE_TIME_INTERVAL;
                Log.d(TAG, "run: elapse=" + elapsed + "\t\ttotal=" + TIMEOUT);
                if (elapsed >= TIMEOUT) {
                    cancelTimer();
                    exerciseTimeFinished();
                    return;
                }
                if ((TOTAL_TIME - TOTAL_TIME_DONE) == Math.ceil(TOTAL_TIME / 2))
                    TTSManager.getInstance(getApplication()).play("half the time");

                else if ((TIMEOUT - elapsed) <= 3000)
                    TTSManager.getInstance(getApplication()).play(String.valueOf((TIMEOUT - elapsed) / EXERCISE_TIME_INTERVAL));

                setProgressBarModel(TIMEOUT - elapsed);
            }
        };

        timer = new Timer();
        if (isExerciseTimeOrReps())
            timer.schedule(timerTask, EXERCISE_TIME_INTERVAL, EXERCISE_TIME_INTERVAL);
        else
            timer.schedule(timerTask, EXERCISE_TIME_INTERVAL, EXERCISE_REPS_INTERVAL);
    }

    private void setProgressBarModel(long millisUntilFinished) {
        runOnUiThread(() -> {
            AppUtils.playAudio(R.raw.clock);
            playingExerciseModel.setmTimeToFinish((int) (millisUntilFinished / 1000));
            activityPlayingBinding.completedProgressbar.setProgress(playingExerciseModel.getmTimeToFinish());
            if (isExerciseTimeOrReps())
                activityPlayingBinding.completedProgressbar.setText(AppUtils.convertSecondsToHMmSs(playingExerciseModel.getmTimeToFinish()));
            else
                activityPlayingBinding.completedProgressbar.setText(playingExerciseModel.getmTimeToFinish() + "/" + playingExerciseModel.getmTotalTime());
            playingExerciseModel.setmCompletedTime(progressStatus += 1);
            Log.d(TAG, "Total Time" + playingExerciseModel.getmTotalTime() + "\tTime to Finish:" + playingExerciseModel.getmTimeToFinish() + "\t progress:" + activityPlayingBinding.completedProgressbar.getProgress());
        });
    }

    private void exerciseTimeFinished() {
        runOnUiThread(() -> {
            AppUtils.playAudio(R.raw.ding);
            stopAnimation();
            playingExerciseModel.setmPlayingStatus(FINISHED);
            playingExerciseModel.setmPausedTime(0);
            progressStatus = 0;
            if (COUNTER >= completeExerciseList.size())
                startFinishActivity();
            else
                skipNextExercise();
        });
    }

    @Override
    public void onBackPressed() {
        //pauseCurrentExercise();
        if (activityPlayingBinding.layoutExeDetail.getVisibility() == View.VISIBLE) {
            activityPlayingBinding.layoutExeDetail.setVisibility(View.GONE);
            if (activityPlayingBinding.layoutPause.getVisibility() != View.VISIBLE) {
                playCurrentExercise();
            }
        } else
            showCancelDialog();
    }

    public boolean cancelTimer() {
        if (timer != null) {
            Log.d(TAG, "cancelTimer: ");
            timer.cancel();
            elapsed = 0;
            TIMEOUT = 0;
            return true;
        }
        return false;
    }

    private void pauseCurrentExercise(boolean isFromDetail) {

        activityPlayingBinding.layoutPlay.setVisibility(View.GONE);
        activityPlayingBinding.layoutWarmUp.setVisibility(View.GONE);
        if (!isFromDetail)
            activityPlayingBinding.layoutPause.setVisibility(View.VISIBLE);
        showPauseLayout();
        playingExerciseModel.setmPlayingStatus(PAUSED);
        TTSManager.getInstance(getApplication()).stop();
        pauseProgressBar();
        stopAnimation();
        removeWarmUpHandler();
    }

    private void removeWarmUpHandler() {
        if (customHandler != null)
            customHandler.removeCallbacks(updateTimerThread);
    }

    //-----Warm up layout views and data controller-----------------------
    private void showWarmUpLayout() {

        isStopCalled = true;
        activityPlayingBinding.ivWarmUpDialogNext.setOnClickListener(view -> {
            skipNextExercise();
        });
        activityPlayingBinding.ivWarmUpDialogPrevious.setOnClickListener(view -> {
            skipPreviousExercise();
        });
        activityPlayingBinding.tvNextExeSkip.setOnClickListener(view -> {
            activityPlayingBinding.layoutWarmUp.setVisibility(View.GONE);
            activityPlayingBinding.layoutPlay.setVisibility(View.VISIBLE);
            customHandler.removeCallbacks(updateTimerThread);
            setDataInUI();
            playCurrentExercise();
        });
        TTSManager.getInstance(getApplication()).play(getString(R.string.next_exercise_text).concat(" ").concat(AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName())).concat(" ").concat(getExerciseRepsOrTimer()));
        activityPlayingBinding.tvNextExeTitle.setText(AppUtils.formatName(completeExerciseList.get(COUNTER).planExercise.getExeName()));
        activityPlayingBinding.tvNextExeReps.setText(getExerciseRepsOrTimer());
        Picasso.get()
                .load(ASSETS_IMAGES + completeExerciseList.get(COUNTER).planExercise.getExeName() + ".png")
                .into(activityPlayingBinding.ivNextExe);
        if (activityPlayingBinding.layoutWarmUp.getVisibility() != View.VISIBLE) {
            activityPlayingBinding.layoutWarmUp.setVisibility(View.VISIBLE);

            customHandler = new Handler();
            activityPlayingBinding.pbNexExe.setMaxValue(TIME / 1000);
            customHandler.postDelayed(updateTimerThread, 0);
        }

  /*      String title;
        String reps;
        if (isExerciseTimeOrReps())
            reps = getString(R.string.seconds_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps());
        else
            reps = getString(R.string.reps_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps());

        title = completeExerciseList.get(COUNTER).planExercise.getExeName();

        NextExerciseDialogFragment nextExerciseDialogFragment = NextExerciseDialogFragment.newInstance(title, reps);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        nextExerciseDialogFragment.setDialogListener(this);
        nextExerciseDialogFragment.show(fragmentTransaction, NextExerciseDialogFragment.TAG);*/
    }

    private void pauseProgressBar() {
        cancelTimer();
        playingExerciseModel.setmPausedTime(playingExerciseModel.getmTimeToFinish());
        activityPlayingBinding.completedProgressbar.setProgress(playingExerciseModel.getmTimeToFinish());
    }

    private String getExerciseRepsOrTimer() {
        if (isExerciseTimeOrReps())
            return
                    getString(R.string.seconds_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps());
        else
            return getString(R.string.reps_format, completeExerciseList.get(COUNTER).planExercise.getExerciseReps());
    }

    @Override
    public void isDialogFinished(boolean b) {
        //playExercise();
    }

    @Override
    public void isNextExerciseDailogResult(boolean b) {
        setDataInUI();
        playExercise();
    }

    @Override
    public void isExerciseDetailDailogResult(boolean b) {
        setDataInUI();
        if (activityPlayingBinding.layoutPause.getVisibility() != View.VISIBLE)
            playExercise();
    }

    @Override
    public void isWarmpupDailogResult(boolean flag) {
        isStopCalled = true;
        setDataInUI();
        playCurrentExercise();
    }

    private void showWarmUpDailogFragment() {
        if (getIntent().getBooleanExtra("warm_up", false)) {
            WarmUpDialogFragment warmUpDialogFragment = WarmUpDialogFragment.newInstance(completeExerciseList.get(COUNTER).planExercise.getExeName(), getExerciseRepsOrTimer());
            warmUpDialogFragment.setDialogListener(PlayingActivity.this);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            warmUpDialogFragment.show(ft, WarmUpDialogFragment.TAG);
        }
    }

    public class LoadData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            completeExerciseList = playingListViewModel.getDayExerciseDetail(appPreference.getPlan(), appPreference.getDay());

            for (CompleteExercise completExe : completeExerciseList) {
                if (completExe.planExercise.isComplete())
                    COUNTER++;
            }
            if (completeExerciseList.size() == COUNTER) COUNTER = 0;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Show Warm up dialog
            if (appPreference.getBoolean(SNOOZE_REMINDER)) {
                setDataInUI();
                playCurrentExercise();
                appPreference.setBoolean(SNOOZE_REMINDER, false);
            } else
                //showWarmUpLayout();
                showWarmUpDailogFragment();


        }
    }
}
