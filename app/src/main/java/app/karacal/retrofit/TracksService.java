package app.karacal.retrofit;

import java.util.List;

import app.karacal.retrofit.models.TourResponse;
import app.karacal.retrofit.models.TrackResponse;
import app.karacal.retrofit.models.UploadTrackResponse;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TracksService {

    @GET("audio/by-tour/{tour_id}")
    Observable<List<TrackResponse>> getTracksList(@Header("Authorization") String token, @Path(value = "tour_id", encoded = true) String tourId);

    @POST("audio/upload/{guide_id}/{tour_id}")
    Observable<UploadTrackResponse> uploadAudio(@Header("Authorization") String token,
                                                @Path(value = "guide_id", encoded = true) String guideId,
                                                @Path(value = "tour_id", encoded = true) String tourId,
                                                @Body RequestBody data);


}
