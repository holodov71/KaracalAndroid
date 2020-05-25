package app.karacal.retrofit;

import java.util.List;

import app.karacal.retrofit.models.TourResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface TourService {
    @GET("tours")
    Observable<List<TourResponse>> getToursList(@Header("Authorization") String token);
}
