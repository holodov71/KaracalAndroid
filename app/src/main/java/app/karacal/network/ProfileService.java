package app.karacal.network;

import app.karacal.models.Profile;
import app.karacal.network.models.request.ProfileRequest;
import app.karacal.network.models.response.BaseResponse;
import app.karacal.network.models.response.PurchasesResponse;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProfileService {

    @GET("profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @GET("profile")
    Observable<Profile> loadProfile(@Header("Authorization") String token);

    @GET("profile/purchased/{token}")
    Observable<PurchasesResponse> loadPurchases(@Header("Authorization") String tokenHeader, @Path("token") String token);

    @PUT("profile/edit")
    Observable<BaseResponse> editProfile(@Header("Authorization") String token, @Body ProfileRequest request);

    @DELETE("profile/delete")
    Observable<BaseResponse> deleteProfile(@Header("Authorization") String token);

}
