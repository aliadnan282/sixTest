package com.workout.sixpacksabs.binder;

import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.Achievement;
import com.workout.sixpacksabs.data.entity.Category;
import com.workout.sixpacksabs.data.entity.DayProgress;
import com.workout.sixpacksabs.data.entity.Recipe;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.model.Last30DaysModel;

import static android.view.View.VISIBLE;

/**
 * Created by AdnanAli on 3/9/2018.
 */

public class CustomBindingAdapter {


    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String resId) {
        Picasso.get()
                .load("file:///android_asset/images/" + resId)
                .into(imageView);
        //        Picasso.with(mContext).load("file:///android_asset/images/"+item.getIcon()).into( holder.exerciseImage);
    /*    Glide.with(mContext)
                .load("https://images.pexels.com/photos/33109/fall-autumn-red-season.jpg?h=350&auto=compress&cs=tinysrgb")
                .into(imageView);*/
    }

    @BindingAdapter("imagelevelUrl")
    public static void loadImage(ImageView imageView, Category category) {
        String imageName;
        if (category.getId() == 1)
            Picasso.get()
                    .load(R.drawable.ic_level_1)
                    .into(imageView);
        else if (category.getId() == 2)
            Picasso.get()
                    .load(R.drawable.ic_level_2)
                    .into(imageView);
        else if (category.getId() == 3)
            Picasso.get()
                    .load(R.drawable.ic_level_3)
                    .into(imageView);
        //        Picasso.with(mContext).load("file:///android_asset/images/"+item.getIcon()).into( holder.exerciseImage);
    /*    Glide.with(mContext)
                .load("https://images.pexels.com/photos/33109/fall-autumn-red-season.jpg?h=350&auto=compress&cs=tinysrgb")
                .into(imageView);*/
    }

    @BindingAdapter({"imageRecipeUrl", "imageRecipeModel"})
    public static void loadRecipeImage(ImageView imageView, String resId, Recipe recipe) {
        if (recipe.isComplete())
            Picasso.get().load(R.drawable.ic_circle_complete).into(imageView);
        else {
            TypedArray imgs = AppConstant.mContext.getResources().obtainTypedArray(R.array.diet_plan_icons);
            Picasso.get().load(imgs.getResourceId(recipe.getDayId() % 11, 1)).into(imageView);
            imgs.recycle();
        }
    }

    @BindingAdapter("textFormatExerciseDay")
    public static void loadExerciseImage(TextView textView, DayProgress dayProgress) {
        if (dayProgress.getDay_id() % 7 == 0) {
            textView.setText(AppConstant.mContext.getString(R.string.rest_day_text));
        } else
            textView.setText(String.format(AppConstant.mContext.getString(R.string.days_text), dayProgress.day_id));
    }

    @BindingAdapter("bgLast30Days")
    public static void loadExerciseImage(ImageView imageView, Last30DaysModel dayProgress) {
        if (dayProgress.isCompleted()) {
            imageView.setBackgroundResource(R.drawable.round_tv_background_monthly_completed);
        } else
            imageView.setImageResource(R.drawable.round_tv_background_monthly_incompleted);
    }

    @BindingAdapter("completeDaysVisiblity")
    public static void setCompleteDayVisibility(ImageView imageView, DayProgress dayProgress) {
        if (dayProgress.getDay_percentage() == 100) {
            imageView.setVisibility(VISIBLE);
        } else
            imageView.setVisibility(View.GONE);
    }

    @BindingAdapter("imageExerciseUrl")
    public static void loadExerciseImage(ImageView imageView, String resId) {
        Picasso.get()
                .load("file:///android_asset/images/" + resId.concat(".png"))
                .into(imageView);
    }

    @BindingAdapter("day_progress")
    public static void setDayProgress(CircleProgressBar circleProgressBar, DayProgress dayProgress) {
        if (dayProgress.getDay_percentage() == 100)
            circleProgressBar.setVisibility(View.GONE);
        else
            circleProgressBar.setVisibility(View.VISIBLE);
        circleProgressBar.setProgress((int) dayProgress.getDay_percentage());
    }

    @BindingAdapter("achievementImage")
    public static void loadExerciseImage(ImageView imageView, Achievement achievement) {
        if (achievement.isComplete())
            Picasso.get()
                    .load(R.drawable.award_selected)
                    .into(imageView);
        else {
            Picasso.get()
                    .load(R.drawable.award_unselected)
                    .into(imageView);
        }
    }

/*    @BindingAdapter({"entries", "layout"})
    public static <T> void setEntries(ViewGroup viewGroup,  List<T> entries, int layoutId) {
        viewGroup.removeAllViews();
        if (entries != null) {
            LayoutInflater inflater = (LayoutInflater)
                    viewGroup.getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < entries.size(); i++) {
                T entry = entries.get(i);
                ViewDataBinding binding = DataBindingUtil
                        .inflate(inflater, layoutId, viewGroup, true);
                binding.setVariable(BR.data, entry);
            }
        }
    }*/
}












