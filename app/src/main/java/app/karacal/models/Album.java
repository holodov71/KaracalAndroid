package app.karacal.models;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String title;

    private String imageUrl;

    private ArrayList<Track> tracks = new ArrayList<>();

    public Album(String title, String imageUrl, List<Track> tracks) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.tracks.addAll(tracks);
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", tracks=" + tracks +
                '}';
    }
}
