package com.workout.sixpacksabs.view.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.DialogPauseExerciseBinding;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.interfaces.DialogFinishListener;
import com.workout.sixpacksabs.view.activity.PlayingActivity;

import static com.workout.sixpacksabs.helper.AppConstant.ASSETS_IMAGES;

public class ExercisePauseDialogFragment extends DialogFragment {

    public static final String TAG = ExercisePauseDialogFragment.class.getSimpleName();
    private final long DELAY_INTERVAL = 1000l;
    DialogPauseExerciseBinding dialogPauseExerciseBinding;
    private DialogFinishListener mCallback;

    public static ExercisePauseDialogFragment newInstance(String title, String detail) {
        ExercisePauseDialogFragment f = new ExercisePauseDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("reps", detail);
        f.setArguments(args);

        return f;
    }

    private void startOrSkipDialog() {
        mCallback.isNextExerciseDailogResult(true);
        dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                if (getActivity() instanceof PlayingActivity) {
                    ((PlayingActivity) getActivity()).showCancelDialog();
                }
                // startOrSkipDialog();
            }
            return false;
        });
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
        dialogPauseExerciseBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_pause_exercise, parent, false);
        dialogPauseExerciseBinding.tvDialogPauseExeTitle.setText(AppUtils.formatName(getArguments().getString("title")));
        dialogPauseExerciseBinding.tvNextExeReps.setText(AppUtils.formatName(getArguments().getString("reps")));
        Picasso.get()
                .load(ASSETS_IMAGES + getArguments().getString("title") + ".png")
                .into(dialogPauseExerciseBinding.ivDialogPauseExe);
        dialogPauseExerciseBinding.ivPauseDialogNext.setOnClickListener(view -> {
            startOrSkipDialog();
        });
        return dialogPauseExerciseBinding.getRoot();
    }
} 