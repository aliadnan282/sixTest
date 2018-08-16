package com.workout.sixpacksabs.module;

import com.workout.sixpacksabs.in_app.util.SixPacksBilling;
import com.workout.sixpacksabs.view.activity.DrawerActivity;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    /*@Binds
    abstract DrawerActivity provideDrawerActivity(DrawerActivity drawerActivity);*/

    @Provides
    SixPacksBilling provideSixPackBilling(DrawerActivity drawerActivity){
        return new SixPacksBilling(drawerActivity);
    }
}
