package app.karacal.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tracks")
public class TrackEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    public String id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "album_id")
    public String albumId;

    @ColumnInfo(name = "resId")
    public String resId;
}
