package com.workout.sixpacksabs;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.workout.sixpacksabs.di.AppComponent;
import com.workout.sixpacksabs.di.DaggerAppComponent;
import com.workout.sixpacksabs.helper.AppConstant;
import com.workout.sixpacksabs.helper.MyExceptionHandler;
import com.workout.sixpacksabs.manager.AdsManager;
import com.workout.sixpacksabs.manager.TTSManager;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import io.fabric.sdk.android.Fabric;


/**
 * Created by AdnanAli on 3/9/2018.
 */

public class SixPackAbs extends DaggerApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        AppConstant.mContext = getApplicationContext();
        //Stetho.initializeWithDefaults(this);
        TTSManager.getInstance(this);
        AdsManager.getInstance();
        Fabric.with(this,new Crashlytics());
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent= DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
