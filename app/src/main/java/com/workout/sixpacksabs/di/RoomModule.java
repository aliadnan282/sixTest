package com.workout.sixpacksabs.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.workout.sixpacksabs.data.dao.DailyExerciseProgressDao;
import com.workout.sixpacksabs.data.database.AppDatabase;
import com.workout.sixpacksabs.data.repository.RoomRepository;
import com.workout.sixpacksabs.data.repository.SixPackRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.workout.sixpacksabs.data.database.AppDatabase.DATABASE_NAME;

@Module
public class RoomModule {

    @Singleton
    @Provides
    AppDatabase providesRoomDatabase(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
    }

    @Singleton
    @Provides
    DailyExerciseProgressDao providesProductDao(AppDatabase appDatabase) {
        return appDatabase.dailyExerciseProgressDao();
    }

    @Singleton
    @Provides
    RoomRepository provideRepository(AppDatabase AppDatabase) {
        return new RoomRepository();
    }
}

