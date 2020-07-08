package app.karacal.network;

import java.util.List;

import app.karacal.network.models.request.GuideByEmailRequest;
import app.karacal.network.models.response.GuideByEmailResponse;
import app.karacal.network.models.response.GuideResponse;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GuideService {

    @GET("guides")
    Observable<List<GuideResponse>> getGuidesList(@Header("Authorization") String token);

    @GET("guides/{guide_id}")
    Observable<GuideResponse> getGuideById(@Header("Authorization") String token, @Path("guide_id") String guideId);

    @POST("guides/by-email")
    Observable<GuideByEmailResponse> getGuideByEmail(@Header("Authorization") String token, @Body GuideByEmailRequest request);

}
