package com.workout.sixpacksabs.di;

import com.workout.sixpacksabs.module.MainActivityModule;
import com.workout.sixpacksabs.view.activity.DrawerActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by mertsimsek on 25/05/2017.
 */
@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract DrawerActivity bindDrawerActivity();

    /*@ContributesAndroidInjector(modules = {DetailActivityModule.class, DetailFragmentProvider.class})
    abstract DetailActivity bindDetailActivity();*/

}
