package app.karacal.retrofit;

import app.karacal.retrofit.models.request.LoginRequest;
import app.karacal.retrofit.models.request.RegisterRequest;
import app.karacal.retrofit.models.request.SocialLoginRequest;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InitService {

    @POST("init/login")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);

    @POST("init/social-login")
    Call<ResponseBody> socialLogin(@Body SocialLoginRequest loginRequest);

    @POST("init/register")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);
}
