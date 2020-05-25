package app.karacal.models;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import app.karacal.data.entity.TourEntity;
import app.karacal.retrofit.models.TourResponse;

public class Tour implements Serializable {

    private int id;
    private int image = -1;
    private String imageUrl;
    private String title;
    private String description;
    private Double price;
    private int rating;
    private int duration;
    private int authorId;
    private double lat;
    private double lng;

    public Tour(int id, int image, String title, String description, Double price, int rating, int duration, int authorId, double lat, double lng) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.duration = duration;
        this.authorId = authorId;
        this.lat = lat;
        this.lng = lng;
    }

    public Tour(TourResponse tourResponse) {
        Log.v("TourResponse", "Tour(TourResponse tourResponse)");
        Log.v("TourResponse", "tourResponse = "+tourResponse);
        this.id = tourResponse.getId();
        this.imageUrl = tourResponse.getImage();
        this.title = tourResponse.getTitle();
        this.description = tourResponse.getDescription();
        this.price = null;
        this.rating = tourResponse.getStars();
        this.duration = Integer.parseInt(tourResponse.getDuration());
        this.authorId = tourResponse.getGuideId();
        this.lat = tourResponse.getLatitude();
        this.lng = tourResponse.getLongitude();
    }

    public Tour(TourEntity entity) {
        this.id = entity.id;
        this.image = entity.imageId;
        this.title = entity.title;
        this.description = entity.description;
        this.price = (double) entity.priceInCents/100;
        this.rating = entity.rating;
        this.duration = entity.duration;
        this.authorId = 1;
        this.lat = entity.lat;
        this.lng = entity.lng;
    }

    public int getId() {
        return id;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public int getRating() {
        return rating;
    }

    public int getDuration() {
        return duration;
    }

    public int getAuthorId() {
        return authorId;
    }

    public LatLng getLocation() {
        return new LatLng(lat, lng);
    }

    public Location getTourLocation() {
        Location tourLocation = new Location("dummyprovider");
        tourLocation.setLatitude(lat);
        tourLocation.setLongitude(lng);
        return tourLocation;
    }
}
