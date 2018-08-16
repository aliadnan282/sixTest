package com.workout.sixpacksabs.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.databinding.CardLast30DaysBinding;
import com.workout.sixpacksabs.databinding.CardVhCategoryBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.model.Last30DaysModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdnanAli on 3/8/2018.
 */

public class Last30DaysAdapter extends RecyclerView.Adapter<Last30DaysAdapter.VHCategory> {
    private static final String TAG = Last30DaysAdapter.class.getSimpleName();
    LayoutInflater inflater;
    Context context;
    AppPreference appPreference;
    private List<Last30DaysModel> categoryList = new ArrayList<>();

    public Last30DaysAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        appPreference = AppPreference.getInstance(context);
    }

    @Override
    public VHCategory onCreateViewHolder(ViewGroup parent, int viewType) {
        CardLast30DaysBinding cardVhCategory = DataBindingUtil.inflate(inflater, R.layout.card_last_30_days, parent, false);
        return new VHCategory(cardVhCategory);
    }

    @Override
    public void onBindViewHolder(VHCategory holder, int position) {
        Last30DaysModel category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategories(List<Last30DaysModel> categories) {
        Log.d(TAG, "setCategories: size" + categories.size());
        categoryList = categories;
        notifyDataSetChanged();
    }

    class VHCategory extends RecyclerView.ViewHolder {
        CardLast30DaysBinding mBinding;

        VHCategory(CardLast30DaysBinding cardVhCategory) {
            super(cardVhCategory.getRoot());
            mBinding = cardVhCategory;
        }

        void bind(Last30DaysModel categoryModel) {
            mBinding.setLast30DaysModel(categoryModel);
           // mBinding.setAdapter(Last30DaysAdapter.this);
            mBinding.executePendingBindings();

        }
    }
}
