package app.karacal.network;

import java.util.List;
import java.util.Map;

import app.karacal.network.models.request.NearToursRequest;
import app.karacal.network.models.request.SaveTourRequest;
import app.karacal.network.models.response.ContentResponse;
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

//    @POST("tours")
//    Observable<SaveTourResponse> createTour(@Header("Authorization") String token, @Body SaveTourRequest tourRequest);

    @Streaming
    @Multipart
    @POST("contents")
    Observable<SaveTourResponse> createTour(@Header("Authorization") String token,
                                            @Part MultipartBody.Part image,
                                            @Part MultipartBody.Part data);
//    @Body RequestBody data);

//    @Multipart
//    @Streaming
//    @POST("audio/upload/{guide_id}/{tour_id}")
//    Observable<UploadTrackResponse> uploadAudio(@Header("Authorization") String token,
//                                                @Path(value = "guide_id", encoded = true) String guideId,
//                                                @Path(value = "tour_id", encoded = true) String tourId,
//                                                @Part MultipartBody.Part audio);

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
