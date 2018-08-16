package com.workout.sixpacksabs.di;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.workout.sixpacksabs.data.database.AppDatabase;
import com.workout.sixpacksabs.helper.AppPreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.workout.sixpacksabs.data.database.AppDatabase.DATABASE_NAME;

@Module
public class AppModule {

    @Singleton
    @Provides
    AppPreference providesPreference(Context context) {
        return new AppPreference(context);
    }

    @Singleton
    @Provides
    Context provideContext(Application application) {
        return application.getApplicationContext();
    }
}
