package com.workout.sixpacksabs.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.databinding.CardVhAchievementsBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.view.activity.ExerciseListActivity;

import java.util.ArrayList;
import java.util.List;


public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.VHCategory> {
    private static final String TAG = AchievementsAdapter.class.getSimpleName();
    private final AppPreference appPreference;
    LayoutInflater inflater;
    Context context;
    private List<Achievement> categoryList = new ArrayList<>();

    public AchievementsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        appPreference = AppPreference.getInstance(context);
    }

    @Override
    public VHCategory onCreateViewHolder(ViewGroup parent, int viewType) {
        CardVhAchievementsBinding cardVhAchievementsBinding = DataBindingUtil.inflate(inflater, R.layout.card_vh_achievements, parent, false);
        return new VHCategory(cardVhAchievementsBinding);
    }


    @Override
    public void onBindViewHolder(VHCategory holder, int position) {
        Achievement category = categoryList.get(position);
        holder.bind(category);

    }

    public void onPlanDaysClicked(View view, Achievement categoryModel) {
        appPreference.setDay(categoryModel.getAchievementId());
        context.startActivity(new Intent(context, ExerciseListActivity.class));
        Toast.makeText(view.getContext(), "" + categoryModel.getAchievementId(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public void setAchievements(List<Achievement> achievements) {
        Log.d(TAG, "setCategories: size" + achievements.size());
        categoryList = achievements;
        notifyDataSetChanged();

    }

    class VHCategory extends RecyclerView.ViewHolder {
        CardVhAchievementsBinding mBinding;

        VHCategory(CardVhAchievementsBinding cardVhAchievementsBinding) {
            super(cardVhAchievementsBinding.getRoot());
            mBinding = cardVhAchievementsBinding;
        }

        void bind(Achievement achievement) {
            mBinding.setAchivement(achievement);
            mBinding.setAdapter(AchievementsAdapter.this);
            mBinding.executePendingBindings();

        }
    }
}
