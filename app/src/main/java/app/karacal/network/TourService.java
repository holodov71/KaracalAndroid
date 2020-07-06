package app.karacal.network;

import java.util.List;

import app.karacal.network.models.request.NearToursRequest;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.response.ContentResponse;
import app.karacal.network.models.response.SaveTourResponse;
import app.karacal.network.models.response.TourDetailsResponse;
import app.karacal.network.models.response.TourResponse;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TourService {
    @GET("tours")
    Observable<List<TourResponse>> getToursList(@Header("Authorization") String token);

    @POST("tours")
    Observable<SaveTourResponse> createTour(@Header("Authorization") String token, @Body SaveTourRequest tourRequest);

    @GET("contents")
    Observable<List<ContentResponse>> getContentsList(@Header("Authorization") String token);

    @GET("contents/by-guide/{guideId}")
    Observable<List<ContentResponse>> getToursByAuthor(@Header("Authorization") String token, @Path("guideId") int guideId);

    @GET("contents")
    Observable<List<ContentResponse>> getContentsPage(@Header("Authorization") String token);

    @POST("contents/by-location")
    Observable<List<ContentResponse>> loadNearTours(@Header("Authorization") String token, @Body NearToursRequest request);

    @GET("tours/{tour_id}")
    Observable<TourDetailsResponse> getTourById(@Header("Authorization") String token, @Path("tour_id") String tourId);

}
