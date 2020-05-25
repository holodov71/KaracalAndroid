package app.karacal.retrofit;

import java.util.List;

import app.karacal.retrofit.models.GuideResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GuideService {

    @GET("guides")
    Observable<List<GuideResponse>> getGuidesList(@Header("Authorization") String token);

}
