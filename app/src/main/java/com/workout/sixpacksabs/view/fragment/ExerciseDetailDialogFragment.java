package com.workout.sixpacksabs.view.fragment;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.DialogExerciseDetailBinding;
import com.workout.sixpacksabs.helper.AppUtils;
import com.workout.sixpacksabs.interfaces.DialogFinishListener;

import static com.workout.sixpacksabs.helper.AppConstant.ASSETS_IMAGES;

public class ExerciseDetailDialogFragment extends DialogFragment {

    public static final String TAG = ExerciseDetailDialogFragment.class.getSimpleName();
    DialogExerciseDetailBinding dialogExerciseDetailBinding;
    private DialogFinishListener mCallback;

    public static ExerciseDetailDialogFragment newInstance(String title, String detail) {
        ExerciseDetailDialogFragment f = new ExerciseDetailDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("detail", detail);
        f.setArguments(args);

        return f;
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
    public void onResume() {
        super.onResume();

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                skipOrFinishDialog();

            }
            // Otherwise, do nothing else
            return false;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle state) {
        super.onCreateView(inflater, parent, state);
        dialogExerciseDetailBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_exercise_detail, parent, false);
        dialogExerciseDetailBinding.playExerciseDetail.setText(Html.fromHtml(getArguments().getString("detail")));
        dialogExerciseDetailBinding.exeTitlePlay.setText(AppUtils.formatName(getArguments().getString("title")));
        Picasso.get()
                .load(ASSETS_IMAGES + getArguments().getString("title") + ".png")
                .into(dialogExerciseDetailBinding.imageAnimations);

        dialogExerciseDetailBinding.ivCancel.setOnClickListener(view -> {
            skipOrFinishDialog();

        });
        return dialogExerciseDetailBinding.getRoot();
    }

    private void skipOrFinishDialog() {
        mCallback.isExerciseDetailDailogResult(false);
        dismiss();
    }
} 