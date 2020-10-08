package app.karacal.network;

import java.util.List;
import java.util.Map;

import app.karacal.models.Tag;
import app.karacal.network.models.request.NearToursRequest;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.request.SerGuideRatingRequest;
import app.karacal.network.models.request.SerTourRatingRequest;
import app.karacal.network.models.response.ContentResponse;
import app.karacal.network.models.response.RatingResponse;
import app.karacal.network.models.response.SaveTourResponse;
import app.karacal.network.models.response.TourDetailsResponse;
import app.karacal.network.models.response.TourResponse;
import app.karacal.network.models.response.UploadTrackResponse;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface TourService {
    @GET("tours")
    Observable<List<TourResponse>> getToursList(@Header("Authorization") String token);

    @Streaming
    @Multipart
    @POST("contents")
    Observable<SaveTourResponse> createTour(@Header("Authorization") String token,
                                            @Part MultipartBody.Part image,
                                            @Part MultipartBody.Part data);

    @Streaming
    @Multipart
    @POST("contents/edit/{tour_id}")
    Observable<SaveTourResponse> editTour(@Header("Authorization") String token,
                                            @Path("tour_id") int tourId,
                                            @Part MultipartBody.Part image,
                                            @Part MultipartBody.Part data);

    @GET("contents")
    Observable<List<ContentResponse>> getContentsList(@Header("Authorization") String token);

    @GET("contents/by-guide/{guideId}")
    Observable<List<ContentResponse>> getToursByAuthor(@Header("Authorization") String token, @Path("guideId") int guideId);

    @POST("contents/by-location")
    Observable<List<ContentResponse>> loadNearTours(@Header("Authorization") String token, @Body NearToursRequest request);

    @GET("contents/{tour_id}")
    Observable<ContentResponse> getTourById(@Header("Authorization") String token, @Path("tour_id") String tourId);

    @GET("tags")
    Observable<List<Tag>> getTags(@Header("Authorization") String token);

    @POST("ratings")
    Observable<RatingResponse> setRatingForTour(@Header("Authorization") String token, @Body SerTourRatingRequest request);

    @POST("ratings/create-guide")
    Observable<RatingResponse> setRatingForGuide(@Header("Authorization") String token, @Body SerGuideRatingRequest request);


}
