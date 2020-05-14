package app.karacal.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import app.karacal.data.entity.AlbumEntity;

@Dao
public interface AlbumDao {

    @Query("SELECT * FROM albums")
    LiveData<List<AlbumEntity>> getAllAlbums();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AlbumEntity album);

    @Delete
    void deleteAlbum(AlbumEntity album);
}
