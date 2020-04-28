package app.karacal.retrofit;

import app.karacal.models.Profile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface ProfileService {

    @GET("profile")
    Call<Profile> getProfile(@Header("Authorization") String token);

}
