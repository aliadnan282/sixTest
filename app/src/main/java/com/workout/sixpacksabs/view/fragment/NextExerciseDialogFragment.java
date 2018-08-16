package com.workout.sixpacksabs.view.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.DialogNextExerciseBinding;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.interfaces.DialogFinishListener;
import com.workout.sixpacksabs.manager.TTSManager;
import com.workout.sixpacksabs.view.activity.PlayingActivity;

import static com.workout.sixpacksabs.helper.AppConstant.ASSETS_IMAGES;

public class NextExerciseDialogFragment extends DialogFragment {

    public static final String TAG = NextExerciseDialogFragment.class.getSimpleName();
    private final long DELAY_INTERVAL = 1000l;
    DialogNextExerciseBinding dialogNextExerciseBinding;
    long TIME = 20000l;
    Handler customHandler;
    private DialogFinishListener mCallback;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            //write here whaterver you want to repeat
            AppUtils.playAudio(R.raw.clock);
            if (TIME == 0) {
                AppUtils.playAudio(R.raw.ding);
                dialogNextExerciseBinding.pbNexExe.setText(String.valueOf(0));
                dialogNextExerciseBinding.pbNexExe.setValue((float) (0));

                startOrSkipDialog();
            } else {
                if (TIME <= 3000)
                    TTSManager.getInstance(getActivity().getApplication()).play(String.valueOf(TIME / DELAY_INTERVAL));
                else if (TIME == 8000)
                    TTSManager.getInstance(getActivity().getApplication()).play(AppUtils.formatName(getArguments().getString("title")).concat(getArguments().getString("reps")));
                dialogNextExerciseBinding.pbNexExe.setText(String.valueOf(TIME / DELAY_INTERVAL));
                dialogNextExerciseBinding.pbNexExe.setValue((float) (TIME / DELAY_INTERVAL));
                customHandler.postDelayed(this, DELAY_INTERVAL);
                TIME -= DELAY_INTERVAL;
            }
        }
    };

    public static NextExerciseDialogFragment newInstance(String title, String detail) {
        NextExerciseDialogFragment f = new NextExerciseDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("reps", detail);
        f.setArguments(args);

        return f;
    }

    private void startOrSkipDialog() {
        customHandler.removeCallbacks(updateTimerThread);
        mCallback.isNextExerciseDailogResult(true);
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                pauseDialog();
                if (getActivity() instanceof PlayingActivity) {
                    ((PlayingActivity) getActivity()).showCancelDialog();
                }
                // startOrSkipDialog();
            }
            return false;
        });
    }

    private void pauseDialog() {
        customHandler.removeCallbacks(updateTimerThread);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public void setDialogListener(DialogFinishListener listener) {
        this.mCallback = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);
        dialogNextExerciseBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_next_exercise, parent, false);
        dialogNextExerciseBinding.pbNexExe.setMaxValue(TIME / DELAY_INTERVAL);
        dialogNextExerciseBinding.tvNextExeTitle.setText(AppUtils.formatName(getArguments().getString("title")));
        dialogNextExerciseBinding.tvNextExeReps.setText(AppUtils.formatName(getArguments().getString("reps")));
        Picasso.get()
                .load(ASSETS_IMAGES + getArguments().getString("title") + ".png")
                .into(dialogNextExerciseBinding.ivNextExe);
        customHandler = new Handler();
        customHandler.postDelayed(updateTimerThread, 0);
        dialogNextExerciseBinding.tvNextExeSkip.setOnClickListener(view -> {
            startOrSkipDialog();
        });
        return dialogNextExerciseBinding.getRoot();
    }
} 