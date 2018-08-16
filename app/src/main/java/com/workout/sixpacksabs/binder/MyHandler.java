package com.workout.sixpacksabs.binder;

import android.util.Log;

import com.workout.sixpacksabs.data.entity.Category;

/**
 * Created by AdnanAli on 3/9/2018.
 */

public class MyHandler {
    private static final String TAG = MyHandler.class.getSimpleName();
   
    public void onCategoryClicked( Category categoryModel){
        Log.d(TAG, "Click from button");
        //Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT).show();
        //String mRandom = getRandomString();
    }
   /* private String getRandomString(){
        Collections.shuffle(randomString);
        return randomString.get(0);
    }*/
}