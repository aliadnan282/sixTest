package com.workout.sixpacksabs.helper;

import android.support.v4.view.ViewPager;
import android.view.View;

public class FadePageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;
 
   /* public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
 
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left. 
            view.setAlpha(0);
 
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well 
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else { 
                view.setTranslationX(-horzMargin + vertMargin / 2);
            } 
 
            // Scale the page down (between MIN_SCALE and 1) 
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
 
            // Fade the page relative to its size. 
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));
 
        } else { // (1,+Infinity] 
            // This page is way off-screen to the right. 
            view.setAlpha(0);
        } 
    }
    */
    public void transformPage(View view, float position) {
        if(position <= -1.0F || position >= 1.0F) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(0.0F);
        } else if( position == 0.0F ) {
            view.setTranslationX(view.getWidth() * position);
            view.setAlpha(1.0F);
        } else {
            // position is between -1.0F & 0.0F OR 0.0F & 1.0F
            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(1.0F - Math.abs(position));
        }
    }
}