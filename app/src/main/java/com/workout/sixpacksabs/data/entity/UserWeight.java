package com.workout.sixpacksabs.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "user_weight")
public class UserWeight implements Comparable<UserWeight> {
    @PrimaryKey
    private long id;
    private long time;
    private float weight;

    public UserWeight(long id, long time, float weight) {
        this.id = id;
        this.time = time;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }


    /* public float getWeight(int metric) {
         if (metric == 1) {
             return MetricHelper.getInstance().convertKGtoLB(weight);
         }

         return weight;
     }
     */
    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }


    @Override
    public int compareTo(@NonNull UserWeight userWeight) {
        return 0;
    }
}
