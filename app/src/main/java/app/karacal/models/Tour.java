package app.karacal.models;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.karacal.R;
import app.karacal.data.entity.TourEntity;
import app.karacal.network.models.response.ContentResponse;
import app.karacal.network.models.response.TourResponse;
import app.karacal.network.models.response.TrackResponse;

public class Tour implements Serializable {

    private int id;
    private int image = -1;
    private String imageUrl;
    private String title;
    private String description;
    private long price;
    private int rating;
    private int duration;
    private int authorId;
    private String author;
    private String address;
    private Date createdAt;
    private Date updatedAt;
    private double lat;
    private double lng;
    private List<Track> audio;

    public Tour(int id, int image, String title, String description, long price, int rating, int duration, int authorId, double lat, double lng, String address) {
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
        this.address = address;
    }

    public Tour(TourResponse tourResponse) {
        Log.v("TourResponse", "Tour(TourResponse tourResponse)");
        Log.v("TourResponse", "tourResponse = "+tourResponse);
        this.id = tourResponse.getId();
        this.image = -1;
        this.imageUrl = tourResponse.getImage();
        this.title = tourResponse.getTitle();
        this.description = tourResponse.getDescription();
        this.price = 0;
        this.rating = tourResponse.getStars();
        this.duration = tourResponse.getDuration() != null ? Integer.parseInt(tourResponse.getDuration()) : 100;
        this.authorId = tourResponse.getGuideId();
        this.lat = tourResponse.getLatitude();
        this.lng = tourResponse.getLongitude();
        this.address = tourResponse.getAddress();
        Log.v("TourResponse", "Finish = "+this.toString());

    }

    public Tour(ContentResponse content) {
        Log.v("ContentResponse", "content = "+content);
        this.id = content.getId();
        this.imageUrl = content.getImg();
        this.image = -1;
        this.title = content.getTitle();
        this.description = content.getDesc();
        this.price = 0;
        this.rating = content.getRating();
        this.duration = content.parseDuration();
        this.author = content.getAuthor();
        this.authorId = content.getGuideId();
        this.lat = content.parseLatitude();
        this.lng = content.parseLongitude();
        this.address = content.getAddress();
        audio = new ArrayList<>();
        for (TrackResponse response: content.getAudio()){
            audio.add(new Track(response));
        }
        this.createdAt = content.getCreatedAt();
        this.updatedAt = content.getUpdatedAt();
    }

    public Tour(TourEntity entity) {
        this.id = entity.id;
        this.image = entity.imageId;
        this.title = entity.title;
        this.description = entity.description;
        this.price = entity.priceInCents;
        this.rating = entity.rating;
        this.duration = entity.duration;
        this.authorId = 1;
        this.address = "";
        this.lat = entity.lat;
        this.lng = entity.lng;
    }

    public int getId() {
        return id;
    }

    public int getImage() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getPrice() {
        return price;
    }

    public double getDoublePrice() {
        return ((double)price)/100;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public int getDuration() {
        return duration;
    }

    public String getFormattedTourDuration(Context context) {
        int durationInMinutes = duration / 60;
        int hours = durationInMinutes / 60;
        String hoursText = hours > 0 ? String.format(Locale.getDefault(), "%d %s", hours, context.getString(hours > 1 ? R.string.hours : R.string.hour)) : "";
        int minutes = durationInMinutes % 60;
        String minutesText = String.format(Locale.getDefault(), "%d %s", minutes, context.getString(minutes != 1 ? R.string.minutes : R.string.minute));
        return String.format("%s %s", hoursText, minutesText);
    }

    public String getShortFormattedTourDuration(Context context) {
        int durationInMinutes = duration / 60;
        int hours = durationInMinutes / 60;
        int minutes = durationInMinutes % 60;
        return context.getString(R.string.duration_format, hours, minutes);
    }

    public int getAuthorId() {
        return authorId;
    }

    public String getAuthor() {
        return author;
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

    public List<Track> getAudio() {
        return audio;
    }

    public void setAudio(List<Track> audio) {
        this.audio = audio;
    }

    public Album getAlbum() {
        List<Track> tracks = new ArrayList<>();
        if (!audio.isEmpty()) {
            tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
            tracks.addAll(audio);
            tracks.add(new Track("Abientt", R.raw.track_abientt));
        }
        return new Album(title, tracks);
    }

    public String getAddress() {
        return address;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Tour{" +
                "id=" + id +
                ", image=" + image +
                ", imageUrl='" + imageUrl + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", duration=" + duration +
                ", authorId=" + authorId +
                ", author='" + author + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", audio=" + audio +
                '}';
    }

    public void addAudioFiles(Tour downloadedTour) {
        for (Track track: audio){
            for (Track downloadedTrack: downloadedTour.audio){
                if (track.getTitle().equals(downloadedTrack.getTitle())){
                    track.setFileUri(downloadedTrack.getFileUri());
                    break;
                }
            }
        }
    }
}
