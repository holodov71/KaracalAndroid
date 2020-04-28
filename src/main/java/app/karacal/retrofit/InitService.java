package app.karacal.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InitService {

    @POST("init/login")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);

    @POST("init/register")
    Call<ResponseBody> register(@Body RegisterRequest registerRequest);
}
