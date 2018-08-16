package com.workout.sixpacksabs.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

import com.workout.sixpacksabs.R;
import com.workout.sixpacksabs.data.database.AppDatabase;
import com.workout.sixpacksabs.databinding.ActivitySplashBinding;


public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_TIME_OUT = 3000;
    ActivitySplashBinding activitySplashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Full screen flag
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);


        Animation animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_zoom_in);
        activitySplashBinding.ivMainLayout.startAnimation(animZoomIn);

        Animation translateFade = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_translate_up);

        AnimationSet set = new AnimationSet(true);
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        alpha.setDuration(2500);
        set.addAnimation(alpha);
        set.addAnimation(translateFade);


        activitySplashBinding.content1.startAnimation(alpha);
        activitySplashBinding.content2.startAnimation(set);
        activitySplashBinding.content3.startAnimation(set);

        // Initialze and export data to database from json
        AppDatabase.getInstance(getApplication());

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashActivity.this, DrawerActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        }, SPLASH_TIME_OUT);

    }

}

