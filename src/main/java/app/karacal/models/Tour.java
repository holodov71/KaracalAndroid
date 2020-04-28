package app.karacal.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class Tour implements Serializable {

    private int id;
    private int image;
    private String title;
    private String description;
    private Double price;
    private int rating;
    private int duration;
    private double lat;
    private double lng;

    public Tour(int id, int image, String title, String description, Double price, int rating, int duration, double lat, double lng) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.duration = duration;
        this.lat = lat;
        this.lng = lng;
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

    public LatLng getLocation() {
        return new LatLng(lat, lng);
    }
}
