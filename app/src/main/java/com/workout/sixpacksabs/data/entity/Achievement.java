package com.workout.sixpacksabs.data.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * Created by AdnanAli on 3/12/2018.
 */
@Entity(tableName = "achievement", primaryKeys = {"achievement_id"})
public class Achievement {
    @ColumnInfo(name = "achievement_id")
    int achievementId;
    @ColumnInfo(name = "achievement_days")
    int achievementsDays;
    @ColumnInfo(name = "achievement_status")
    boolean isComplete;
    public Achievement(int id, int achievementsDays, boolean isComplete) {
        this.achievementId = id;
        this.achievementsDays = achievementsDays;
        this.isComplete = isComplete;
    }
    public Achievement() {
    }

    public int getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(int achievementId) {
        this.achievementId = achievementId;
    }

    public int getAchievementsDays() {
        return achievementsDays;
    }

    public void setAchievementsDays(int achievementsDays) {
        this.achievementsDays = achievementsDays;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }
}
