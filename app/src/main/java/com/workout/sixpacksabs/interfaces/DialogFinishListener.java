package com.workout.sixpacksabs.interfaces;

public interface DialogFinishListener {
    void isDialogFinished(boolean b);

    public default void isWarmpupDailogResult(boolean b) {
    }

    public default void isNextExerciseDailogResult(boolean b) {

    }

    public default void isExerciseDetailDailogResult(boolean b) {
    }
}
