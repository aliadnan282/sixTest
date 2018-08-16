package com.workout.sixpacksabs.view.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.interfaces.CardAdapter;
import com.workout.sixpacksabs.model.HeaderModel;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private static final String TAG=CardPagerAdapter.class.getSimpleName();
    private List<CardView> mViews;
    private List<HeaderModel> mData;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(HeaderModel item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        Log.d(TAG, "getBaseElevation: ");
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        Log.d(TAG, "getCardViewAt: ");

        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.d(TAG, "isViewFromObject: ");
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: ");
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.card_vh_header, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(TAG, "destroyItem: ");
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(HeaderModel item, View view) {
        TextView titleTextView = (TextView) view.findViewById(R.id.tv_header);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_header);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);
        titleTextView.setText(item.getTitle());
        Picasso.get().load(item.getmImageResource()).into(imageView);
        cardView.setCardBackgroundColor(item.getmColorResource());
    }
}
