package app.karacal.data;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.R;
import app.karacal.models.Album;
import app.karacal.models.Track;

@Singleton
public class AlbumRepository {

    private final Album album;

    @Inject
    public AlbumRepository(){
        ArrayList<Track> tracks = new ArrayList<>();
        tracks.add(new Track("Introduction", R.raw.test_1));
        tracks.add(new Track("Historical meaning", R.raw.test_2));
        tracks.add(new Track("First appearance", R.raw.test_3));
        tracks.add(new Track("Clarity of perception", R.raw.test_4));
        tracks.add(new Track("International relationships", R.raw.test_5));
        tracks.add(new Track("International relationships", R.raw.test_6));
        tracks.add(new Track("International relationships", R.raw.test_7));
        tracks.add(new Track("International relationships", R.raw.test_8));
        tracks.add(new Track("International relationships", R.raw.test_9));
        tracks.add(new Track("International relationships", R.raw.test_10));
        album = new Album("Title of the Audio Album", tracks);
    }

    public Album getAlbumByTourId(int tourId){
        return album;
    }
}
