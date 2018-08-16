package com.workout.sixpacksabs.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.DayProgress;
import com.workout.sixpacksabs.databinding.CardVhDaysBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.view.activity.ExerciseListActivity;
import com.workout.sixpacksabs.view.activity.RestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdnanAli on 3/8/2018.
 */

public class DaysAdapter extends RecyclerView.Adapter<DaysAdapter.VHCategory> {
    private static final String TAG = DaysAdapter.class.getSimpleName();
    private final AppPreference appPreference;
    LayoutInflater inflater;
    Context context;
    private List<DayProgress> categoryList = new ArrayList<>();

    public DaysAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appPreference = AppPreference.getInstance(context);
    }

    @Override
    public VHCategory onCreateViewHolder(ViewGroup parent, int viewType) {
        CardVhDaysBinding cardVhCategory = DataBindingUtil.inflate(inflater, R.layout.card_vh_days, parent, false);
        return new VHCategory(cardVhCategory);
    }


    @Override
    public void onBindViewHolder(VHCategory holder, int position) {
        DayProgress category = categoryList.get(position);
        holder.bind(category);

    }

    public void onPlanDaysClicked(View view, DayProgress dayProgress) {
        appPreference.setDay(dayProgress.getDay_id());
        if (dayProgress.getDay_id() % 7 == 0)
            context.startActivity(new Intent(context, RestActivity.class));
        else
            context.startActivity(new Intent(context, ExerciseListActivity.class));

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setPlanDays(List<DayProgress> categories) {
        Log.d(TAG, "setCategories: size" + categories.size());
        categoryList = categories;
        notifyDataSetChanged();
    }

    class VHCategory extends RecyclerView.ViewHolder {
        CardVhDaysBinding mBinding;

        VHCategory(CardVhDaysBinding cardVhDaysBinding) {
            super(cardVhDaysBinding.getRoot());
            mBinding = cardVhDaysBinding;
        }

        void bind(DayProgress planDays) {
            mBinding.setPlanDays(planDays);
            mBinding.setAdapter(DaysAdapter.this);
            mBinding.executePendingBindings();

        }
    }
}
