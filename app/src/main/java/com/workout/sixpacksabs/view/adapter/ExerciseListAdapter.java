package com.workout.sixpacksabs.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.entity.PlanExercise;
import com.workout.sixpacksabs.databinding.CardVhExerciseListBinding;
import com.workout.sixpacksabs.dragable.ItemTouchHelperAdapter;
import com.workout.sixpacksabs.dragable.ItemTouchHelperViewHolder;
import com.workout.sixpacksabs.dragable.OnStartDragListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AdnanAli on 3/8/2018.
 */

public class ExerciseListAdapter extends RecyclerView.Adapter<ExerciseListAdapter.VHCategory> implements ItemTouchHelperAdapter {
    private static final String TAG = ExerciseListAdapter.class.getSimpleName();
    LayoutInflater inflater;
    private List<PlanExercise> daysList = new ArrayList<>();
    private OnStartDragListener mDragStartListener;

    public ExerciseListAdapter(Context context, OnStartDragListener startDragListener) {
        inflater = LayoutInflater.from(context);
        this.mDragStartListener = startDragListener;
    }

    @Override
    public VHCategory onCreateViewHolder(ViewGroup parent, int viewType) {
        CardVhExerciseListBinding cardVhCategory = DataBindingUtil.inflate(inflater, R.layout.card_vh_exercise_list, parent, false);
        return new VHCategory(cardVhCategory);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final VHCategory holder, int position) {
        PlanExercise category = daysList.get(position);

    /*    holder.holderIV.setOnTouchListener((v, event) -> {
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                mDragStartListener.onStartDrag(holder);
            }
            return false;
        });*/
        holder.bind(category);

    }

    public void onPlanDaysClicked(View view, PlanExercise categoryModel) {
        Log.d(TAG, "onCategoryClicked: ");
        Toast.makeText(view.getContext(), "" + categoryModel.getDayId(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public int getItemCount() {
        return daysList.size();
    }

    public void setExerciseList(List<PlanExercise> categories) {
        daysList = categories;
        Log.d(TAG, "dayslist start json: " + new Gson().toJson(daysList));
        notifyDataSetChanged();
    }

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and <strong>not</strong> at the end of a "drop" event.<br/>
     * <br/>
     * Implementations should call {@link RecyclerView.Adapter#notifyItemMoved(int, int)} after
     * adjusting the underlying data to reflect this move.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(daysList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * Called when an item has been dismissed by a swipe.<br/>
     * <br/>
     * Implementations should call {@link RecyclerView.Adapter#notifyItemRemoved(int)} after
     * adjusting the underlying data to reflect this removal.
     *
     * @param position The position of the item dismissed.
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    @Override
    public void onItemDismiss(int position) {
        daysList.remove(position);
        notifyItemRemoved(position);
    }

    public class VHCategory extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public CardVhExerciseListBinding mBinding;
        ImageView holderIV;
        CardView cardView;

        VHCategory(CardVhExerciseListBinding cardVhDaysBinding) {
            super(cardVhDaysBinding.getRoot());
            mBinding = cardVhDaysBinding;
            holderIV = mBinding.ivCategory;
            cardView = mBinding.cvExerciseList;
        }

        void bind(PlanExercise planDays) {
            mBinding.setPlanExercise(planDays);
            mBinding.setAdapter(ExerciseListAdapter.this);
            mBinding.executePendingBindings();
        }

        /**
         * Called when the { ItemTouchHelper} first registers an item as being moved or swiped.
         * Implementations should update the item view to indicate it's active state.
         */
        @Override
        public void onItemSelected() {
            cardView.setBackgroundColor(Color.LTGRAY);

        }

        /**
         * Called when the { ItemTouchHelper} has completed the move or swipe, and the active item
         * state should be cleared.
         */
        @Override
        public void onItemClear() {
            Log.d(TAG, "dayslist reshuffle : " + new Gson().toJson(daysList));
            cardView.setBackgroundColor(Color.WHITE);

        }
    }
}
