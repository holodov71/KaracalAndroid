package app.karacal.models;

import java.util.ArrayList;

public class Album {

    private String title;

    private ArrayList<Track> tracks;

    public Album(String title, ArrayList<Track> tracks) {
        this.title = title;
        this.tracks = tracks;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Track> getTracks() {
        return tracks;
    }
}
