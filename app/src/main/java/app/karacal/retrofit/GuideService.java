package app.karacal.retrofit;

import java.util.List;

import app.karacal.retrofit.models.response.GuideResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GuideService {

    @GET("guides")
    Observable<List<GuideResponse>> getGuidesList(@Header("Authorization") String token);

    @GET("guides/{guide_id}")
    Observable<GuideResponse> getGuideById(@Header("Authorization") String token, @Path("guide_id") String guideId);

}
