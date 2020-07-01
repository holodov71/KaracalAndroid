package app.karacal.retrofit;

import app.karacal.models.Profile;
import app.karacal.retrofit.models.request.ProfileRequest;
import app.karacal.retrofit.models.response.BaseResponse;
import app.karacal.retrofit.models.response.PurchasesResponse;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProfileService {

    @GET("profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

    @GET("profile")
    Observable<Profile> loadProfile(@Header("Authorization") String token);

    @GET("profile/purchased/{token}")
    Observable<PurchasesResponse> loadPurchases(@Path("token") String token);

    @PUT("profile/edit")
    Observable<BaseResponse> editProfile(@Header("Authorization") String token, @Body ProfileRequest request);

}
