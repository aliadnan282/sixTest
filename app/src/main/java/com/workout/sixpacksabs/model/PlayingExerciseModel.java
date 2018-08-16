package com.workout.sixpacksabs.model;

import android.support.annotation.Keep;

import static com.workout.sixpacksabs.model.PlayingExerciseModel.PLAYING_STATUS.FINISHED;

/**
 * Created by adnanali on 28/02/2017.
 */
@Keep
public class PlayingExerciseModel {


    public PLAYING_STATUS mPlayingStatus;
    String  mExerciseName;
    String mTitle;
    int mTotalTime;
    int mTimeToFinish;
    int mCompletedTime;
    int mPausedTime;

    public int getmReps() {
        return mReps;
    }

    public void setmReps(int mReps) {
        this.mReps = mReps;
    }

    int mReps;


    public String getmExerciseName() {
        return mExerciseName;
    }
    public void setmExerciseName(String mExerciseName) {
        this.mExerciseName = mExerciseName;
    }

    public PlayingExerciseModel() {
    }

    public PLAYING_STATUS getmPlayingStatus() {
        return mPlayingStatus==null? FINISHED:mPlayingStatus;
    }

    public void setmPlayingStatus(PLAYING_STATUS mPlayingStatus) {
        this.mPlayingStatus = mPlayingStatus;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getmTotalTime() {
        return mTotalTime != 0 ? mTotalTime : 0;
    }

    public void setmTotalTime(int mTotalTime) {
        this.mTotalTime = mTotalTime;
    }

    public int getmTimeToFinish() {
        return mTimeToFinish != 0 ? mTimeToFinish : 0;
    }

    public void setmTimeToFinish(int mTimeToFinish) {
        this.mTimeToFinish = mTimeToFinish;
    }

    public long getmCompletedTime() {
        return mCompletedTime != 0 ? mCompletedTime : 0;
    }

    public void setmCompletedTime(int mCompletedTime) {
        this.mCompletedTime = mCompletedTime;
    }

    public long getmPausedTime() {
        return mPausedTime != 0 ? mPausedTime : 0;
    }

    public void setmPausedTime(int mPausedTime) {
        this.mPausedTime = mPausedTime;
    }


    public enum PLAYING_STATUS {
        PLAYING,
        PAUSED,
        RESUME,
        FINISHED
    }
    public void reset(){
         mExerciseName="";
         mTitle="";
         mTotalTime=0;
         mTimeToFinish=0;
         mCompletedTime=0;
         mPausedTime=0;
        setmPlayingStatus(FINISHED);
    }

}
