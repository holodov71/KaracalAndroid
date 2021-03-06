package app.karacal.dagger;

import android.app.Application;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.AppDatabase;
import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.TourEntity;
import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    public static final String KARACAL_DATABASE = "karacal.db";

    @Provides
    @Singleton
    public AppDatabase provideDatabase(App application)  {
        AppDatabase database = Room.databaseBuilder(
                application,
                AppDatabase.class,
                KARACAL_DATABASE)
            .build();

        return database;
    }

    @Provides
    ToursDao provideToursDao(AppDatabase database) {
        return database.toursDao();
    }


}
