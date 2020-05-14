package app.karacal.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import app.karacal.data.dao.ToursDao;
import app.karacal.data.entity.AlbumEntity;
import app.karacal.data.entity.TourEntity;
import app.karacal.data.entity.TrackEntity;

@Database(entities = {TourEntity.class, AlbumEntity.class, TrackEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ToursDao toursDao();

}
