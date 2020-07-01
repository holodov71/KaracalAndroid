package app.karacal.retrofit;

import java.util.List;

import app.karacal.retrofit.models.request.NearToursRequest;
import app.karacal.retrofit.models.request.SaveTourRequest;
import app.karacal.retrofit.models.response.BaseResponse;
import app.karacal.retrofit.models.response.ContentResponse;
import app.karacal.retrofit.models.response.SaveTourResponse;
import app.karacal.retrofit.models.response.TourDetailsResponse;
import app.karacal.retrofit.models.response.TourResponse;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("contents")
    Observable<List<ContentResponse>> getContentsPage(@Header("Authorization") String token);

    @POST("contents/by-location")
    Observable<List<ContentResponse>> loadNearTours(@Header("Authorization") String token, @Body NearToursRequest request);

    @GET("tours/{tour_id}")
    Observable<TourDetailsResponse> getTourById(@Header("Authorization") String token, @Path("tour_id") String tourId);

}
