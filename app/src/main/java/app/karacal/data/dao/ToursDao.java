package app.karacal.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import app.karacal.data.entity.TourEntity;

@Dao
public interface ToursDao {

    /**
     * Counts the number of tours in the table.
     *
     * @return The number of tours.
     */
    @Query("SELECT COUNT(*) FROM tours")
    int count();

    @Query("SELECT * FROM tours")
    LiveData<List<TourEntity>> getAllTours();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TourEntity tour);

    @Delete
    void deleteTour(TourEntity tour);
}
