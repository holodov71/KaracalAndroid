package app.karacal.network;

import app.karacal.network.models.request.CreateCommentRequest;
import app.karacal.network.models.response.BaseResponse;
import app.karacal.network.models.response.CommentsResponse;
import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface  CommentsService {

    @GET("comments/by-tour/{tour_id}")
    Observable<CommentsResponse> getCommentsByTour(@Header("Authorization") String token, @Path("tour_id") int tourId);

    @POST("comments")
    Observable<BaseResponse> createNewComment(@Header("Authorization") String token, @Body CreateCommentRequest request);


}
