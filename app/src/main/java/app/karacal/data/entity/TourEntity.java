package app.karacal.data.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tours",
        foreignKeys = @ForeignKey(
                entity = AlbumEntity.class,
                parentColumns = "id",
                childColumns = "album_id",
                onDelete = ForeignKey.CASCADE))
public class TourEntity {

    public TourEntity(int id, int imageId, String title, String description, int priceInCents, int rating, int duration, String author, double lat, double lng, int albumId) {
        this.id = id;
        this.title = title;
        this.imageId = imageId;
        this.albumId = albumId;
        this.description = description;
        this.priceInCents = priceInCents;
        this.rating = rating;
        this.duration = duration;
        this.author = author;
        this.lat = lat;
        this.lng = lng;
    }

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "image_id")
    public int imageId;

    @ColumnInfo(name = "album_id", index = true)
    public int albumId;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "price")
    public int priceInCents;

    @ColumnInfo(name = "rating")
    public int rating;

    @ColumnInfo(name = "duration")
    public int duration;

    @ColumnInfo(name = "author")
    public String author;

    @ColumnInfo(name = "lat")
    public double lat;

    @ColumnInfo(name = "lng")
    public double lng;

}
