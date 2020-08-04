package app.karacal.data.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import app.karacal.App;
import app.karacal.R;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.Album;
import app.karacal.models.Tour;
import app.karacal.models.Track;
import app.karacal.network.models.response.UploadTrackResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class AlbumRepository {

    @Inject
    ApiHelper apiHelper;

    private Context context;

    public MutableLiveData<Album> albumLiveData = new MutableLiveData<>();

    @Inject
    public AlbumRepository(App context){
        this.context = context;
    }

    public Album loadAlbumByTour(Tour tour){
        ArrayList<Track> tracks = new ArrayList<>();

        if (tour.getAudio() != null && !tour.getAudio().isEmpty()) {
            tracks.add(new Track("Bienvenue", R.raw.track_bienvenue));
            tracks.addAll(tour.getAudio());
            tracks.add(new Track("Abientt", R.raw.track_abientt));
        }

        return new Album(tour.getTitle(), tour.getImageUrl(), tracks);
    }

    @SuppressLint("CheckResult")
    public void loadTracksByTour(String tourId) {
        apiHelper.loadTracks(PreferenceHelper.loadToken(context), tourId)
                .flatMapIterable(list -> list)
                .map(Track::new)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tracks -> {
                    if (!tracks.isEmpty()) {
                        tracks.add(0, new Track("Bienvenue", R.raw.track_bienvenue));
                        tracks.add(new Track("Abientt", R.raw.track_abientt));
                    }
                    albumLiveData.setValue(new Album("", "", tracks));
                    // TODO: save to DB

                }, throwable -> {
                    Log.e("loadTracksByTour", "Error: " +throwable.getMessage());
                    // TODO: load list from DB
                });
    }

    public Single<List<UploadTrackResponse>> uploadAudioToServer(String guideId, String tourId, List<Track> tracks){
        return Observable.fromIterable(tracks)
                .flatMap(track -> apiHelper.uploadAudioToServer(PreferenceHelper.loadToken(context), guideId, tourId, track))
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



}
