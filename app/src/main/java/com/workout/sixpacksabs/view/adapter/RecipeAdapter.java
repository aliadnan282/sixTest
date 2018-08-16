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
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.databinding.CardVhRecipeBinding;
import com.workout.sixpacksabs.view.activity.RecipeDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdnanAli on 3/8/2018.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VHCategory> implements android.databinding.DataBindingComponent {
    private static final String TAG = RecipeAdapter.class.getSimpleName();
    LayoutInflater inflater;
    Context context;
    private List<Recipe> categoryList = new ArrayList<>();

    public RecipeAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public VHCategory onCreateViewHolder(ViewGroup parent, int viewType) {
        DataBindingUtil.setDefaultComponent(this);
        CardVhRecipeBinding cardVhCategory = DataBindingUtil.inflate(inflater, R.layout.card_vh_recipe, parent, false);
        return new VHCategory(cardVhCategory);
    }


    @Override
    public void onBindViewHolder(VHCategory holder, int position) {
        Recipe category = categoryList.get(position);
        holder.bind(category);

    }

    public void onRecipeDayClicked(View view, Recipe categoryModel) {
        Log.d(TAG, "onCategoryClicked: ");
        Intent intent=new Intent(context,RecipeDetailActivity.class);
        intent.putExtra("day", categoryModel.getDayId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setRecipeDays(List<Recipe> categories) {
        Log.d(TAG, "setCategories: size" + categories.size());
        categoryList = categories;
        notifyDataSetChanged();
    }

    class VHCategory extends RecyclerView.ViewHolder {
        CardVhRecipeBinding mBinding;

        VHCategory(CardVhRecipeBinding cardVhDaysBinding) {
            super(cardVhDaysBinding.getRoot());
            mBinding = cardVhDaysBinding;
        }

        void bind(Recipe planDays) {
            mBinding.setRecipeDays(planDays);
            mBinding.setAdapter(RecipeAdapter.this);
            mBinding.executePendingBindings();

        }
    }
}
