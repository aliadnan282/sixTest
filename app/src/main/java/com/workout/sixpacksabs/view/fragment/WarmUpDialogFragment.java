package com.workout.sixpacksabs.view.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.DialogWarmUpBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.interfaces.DialogFinishListener;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.TTSManager;
import com.workout.sixpacksabs.view.activity.ExerciseDaysActivity;

import static com.workout.sixpacksabs.helper.AppConstant.ASSETS_IMAGES;
import static com.workout.sixpacksabs.helper.AppConstant.mContext;

public class WarmUpDialogFragment extends DialogFragment {

    public static final String TAG = WarmUpDialogFragment.class.getSimpleName();
    private final long INTERVAL_DELAY = 1000l;
    DialogWarmUpBinding activityWarmUpBinding;
    AppPreference appPreference;
    long TIME = 15000l;
    Handler customHandler;
    boolean isSkipOrComplete = true;
    MediaPlayer mp;
    private DialogFinishListener mCallback;

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            //write here whaterver you want to repeat
            AppUtils.playAudio(R.raw.clock);
            if (TIME == 0) {
                AppUtils.playAudio(R.raw.ding);
                activityWarmUpBinding.circleView.setText(String.valueOf(0));
                activityWarmUpBinding.circleView.setValue((float) (0));
                customHandler.removeCallbacks(updateTimerThread);
                TTSManager.getInstance(getActivity().getApplication()).stop();
                mCallback.isWarmpupDailogResult(false);
                startOrSkipActivity();
            } else {
                if (TIME <= 3000)
                    TTSManager.getInstance(getActivity().getApplication()).play(String.valueOf(TIME / INTERVAL_DELAY), null, null, new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT).show();
                        }
                    });
                else if (TIME == 12000)
                    TTSManager.getInstance(getActivity().getApplication()).play(getString(R.string.next_exercise_text).concat(" ").concat(AppUtils.formatName(getArguments().getString("title")).concat(getArguments().getString("reps"))));
                activityWarmUpBinding.circleView.setText(String.valueOf(TIME / INTERVAL_DELAY));
                activityWarmUpBinding.circleView.setValue((float) (TIME / INTERVAL_DELAY));
                customHandler.postDelayed(this, INTERVAL_DELAY);
                TIME -= INTERVAL_DELAY;
            }
        }
    };

    public static WarmUpDialogFragment newInstance(String title, String detail) {
        WarmUpDialogFragment f = new WarmUpDialogFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("reps", detail);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                TTSManager.getInstance(getActivity().getApplication()).stop();
                Intent intent = new Intent(getActivity(), ExerciseDaysActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startOrSkipActivity();
                startActivity(intent);
                return true;
            }
            // Otherwise, do nothing else
            else return false;
        });
    }


    private void pauseDialog() {
        mCallback.isWarmpupDailogResult(true);
        customHandler.removeCallbacks(updateTimerThread);
        dismiss();
    }

    public void setDialogListener(DialogFinishListener listener) {
        this.mCallback = listener;
    }

    public void startOrSkipActivity() {
        TTSManager.getInstance(getActivity().getApplication()).stop();
        customHandler.removeCallbacks(updateTimerThread);
        dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        appPreference = AppPreference.getInstance(mContext);

    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {

            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.x = 0; // left margin
            layoutParams.y = AppUtils.dpToPx(10); // b

            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setAttributes(layoutParams);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Dialog");
        pauseDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);
        activityWarmUpBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_warm_up, parent, false);
        AdsManager.getInstance().showFacebookBannerAd(activityWarmUpBinding.adContainer);
        activityWarmUpBinding.circleView.setMaxValue(TIME / INTERVAL_DELAY);
        TTSManager.getInstance(getActivity().getApplication()).play("Ready to go ");
        Picasso.get()
                .load(ASSETS_IMAGES + getArguments().get("title") + ".png")
                .into(activityWarmUpBinding.ivWarmUp);
        activityWarmUpBinding.exeNameWarmUp.setText(AppUtils.formatName(getArguments().getString("title")));
        activityWarmUpBinding.exerciseReps.setText(AppUtils.formatName(getArguments().getString("reps")));
        customHandler = new Handler();
        customHandler.postDelayed(updateTimerThread, 0);
        activityWarmUpBinding.tvSkip.setOnClickListener(view -> {
            mCallback.isWarmpupDailogResult(false);
            startOrSkipActivity();
        });
        return activityWarmUpBinding.getRoot();
    }


}