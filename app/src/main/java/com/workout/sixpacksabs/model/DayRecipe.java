package com.workout.sixpacksabs.model;

/**
 * Created by AdnanAli on 11/17/2017.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class DayRecipe implements Parcelable{
    boolean isAdsView = false;
    @SerializedName("day")
    private String day;
    @SerializedName("meals")
    private List<Meal> meals;


    public DayRecipe() {
    }

    public String getDay() {
        return day;
    }

    protected DayRecipe(Parcel in) {
        day = in.readString();
    }


    public static final Creator<DayRecipe> CREATOR = new Creator<DayRecipe>() {
        @Override
        public DayRecipe createFromParcel(Parcel in) {
            return new DayRecipe(in);
        }

        @Override
        public DayRecipe[] newArray(int size) {
            return new DayRecipe[size];
        }
    };

    public boolean isAdsView() {
        return isAdsView;
    }


    public void setDay(String day) {
        this.day = day;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isAdsView ? 1 : 0));
        dest.writeString(day);
    }
}