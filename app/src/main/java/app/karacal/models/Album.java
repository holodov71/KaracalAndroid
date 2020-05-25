package app.karacal.models;

import java.util.ArrayList;
import java.util.List;

public class Album {

    private String title;

    private ArrayList<Track> tracks = new ArrayList<>();

    public Album(String title, List<Track> tracks) {
        this.title = title;
        this.tracks.addAll(tracks);
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }

    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", tracks=" + tracks +
                '}';
    }
}
