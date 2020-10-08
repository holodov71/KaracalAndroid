package app.karacal.network;

import java.util.List;

import app.karacal.network.models.request.RenameTrackRequest;
import app.karacal.network.models.response.BaseResponse;
import app.karacal.network.models.response.TrackResponse;
import app.karacal.network.models.response.UploadTrackResponse;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface TracksService {

    @GET("audio/by-tour/{tour_id}")
    Observable<List<TrackResponse>> getTracksList(@Header("Authorization") String token, @Path(value = "tour_id", encoded = true) String tourId);

    @Multipart
    @Streaming
    @POST("audio/upload/{guide_id}/{tour_id}")
    Observable<UploadTrackResponse> uploadAudio(@Header("Authorization") String token,
                                                @Path(value = "guide_id", encoded = true) String guideId,
                                                @Path(value = "tour_id", encoded = true) String tourId,
                                                @Part MultipartBody.Part audio,
                                                @Part MultipartBody.Part data);

    @DELETE("audio/{track_id}")
    Observable<BaseResponse> deleteTrack(@Header("Authorization") String token, @Path(value = "track_id", encoded = true) String trackId);

    @POST("audio/rename-track/{track_id}")
    Observable<BaseResponse> renameTrack(@Header("Authorization") String token,
                                      @Path(value = "track_id") int trackId,
                                      @Body RenameTrackRequest request);

}
