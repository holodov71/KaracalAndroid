package app.karacal.retrofit;

import java.util.List;

import app.karacal.retrofit.models.TourResponse;
import app.karacal.retrofit.models.TrackResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface TracksService {

    @GET("audio/by-tour/{tour_id}")
    Observable<List<TrackResponse>> getTracksList(@Header("Authorization") String token, @Path(value = "tour_id", encoded = true) String tourId);

}
