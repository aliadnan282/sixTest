package com.workout.sixpacksabs.module;

import com.workout.sixpacksabs.in_app.util.SixPacksBilling;
import com.workout.sixpacksabs.view.activity.MainActivity;
import com.workout.sixpacksabs.view.fragment.ExitDialog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MainActivityModule {

    /*@Binds
    abstract DrawerActivity provideDrawerActivity(DrawerActivity drawerActivity);*/

    @Provides
    SixPacksBilling provideSixPackBilling(MainActivity mainActivity) {
        return new SixPacksBilling(mainActivity);
    }

    @Provides
    ExitDialog provideExitDialog(MainActivity mainActivity) {
        return new ExitDialog(mainActivity);
    }
}
