package com.workout.sixpacksabs.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.databinding.CardVhCategoryBinding;
import com.workout.sixpacksabs.helper.AppPreference;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.AnalyticsManager;
import com.workout.sixpacksabs.view.activity.ExerciseDaysActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AdnanAli on 3/8/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = CategoryAdapter.class.getSimpleName();
    private static final int ADD_VIEW_HOLDER = 123;
    private static final int ITEM_VIEW_HOLDER = 23;
    LayoutInflater inflater;
    Context context;
    AppPreference appPreference;
    private List<Category> categoryList = new ArrayList<>();

    public CategoryAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        appPreference = AppPreference.getInstance(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ADD_VIEW_HOLDER) {
            Log.d(TAG, "onCreateViewHolder: type"+ viewType);
            FrameLayout nativeInstall = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.admob_app_install_vh, parent, false);
            return new VHAdmobNativeInstall(nativeInstall);
        } else {
            Log.d(TAG, "onCreateViewHolder: type"+ viewType);
            CardVhCategoryBinding cardVhCategory = DataBindingUtil.inflate(inflater, R.layout.card_vh_category, parent, false);
            return new VHCategory(cardVhCategory);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 3)
            return ADD_VIEW_HOLDER;
        else
            return ITEM_VIEW_HOLDER;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Category category = categoryList.get(position);

        if (getItemViewType(position) == ITEM_VIEW_HOLDER) {
            VHCategory viewHolder = (VHCategory) holder;
            viewHolder.bind(category);
        }
    }
        public void onCategoryClicked (View view, Category categoryModel){
            Log.d(TAG, "onCategoryClicked: ");
            appPreference.setCategoryString(categoryModel.getCategoryType());
            appPreference.setPlan(categoryModel.getId());
            AnalyticsManager.getInstance().sendAnalytics("category_visited",categoryModel.getCategoryType());
            context.startActivity(new Intent(context, ExerciseDaysActivity.class));
            ((AppCompatActivity) context).overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        }

        @Override
        public int getItemCount () {
            return categoryList.size();
        }

        public void setCategories (List < Category > categories) {
            Log.d(TAG, "setCategories: size" + categories.size());
            categoryList = categories;
            notifyDataSetChanged();
        }

        class VHCategory extends RecyclerView.ViewHolder {
            CardVhCategoryBinding mBinding;

            VHCategory(CardVhCategoryBinding cardVhCategory) {
                super(cardVhCategory.getRoot());
                mBinding = cardVhCategory;
            }

            void bind(Category categoryModel) {
                mBinding.setCategory(categoryModel);
                mBinding.setAdapter(CategoryAdapter.this);
                mBinding.executePendingBindings();

            }
        }

        class VHAdmobNativeInstall extends RecyclerView.ViewHolder {
             VHAdmobNativeInstall(final FrameLayout view) {
                super(view);
                AdsManager.getInstance().loadNativeAppInstall(view);
            }
        }
    }
