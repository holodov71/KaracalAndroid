package app.karacal.network;

import app.karacal.network.models.request.LoginRequest;
import app.karacal.network.models.request.RegisterRequest;
import app.karacal.network.models.request.ResetPasswordRequest;
import app.karacal.network.models.request.SocialLoginRequest;
import app.karacal.network.models.response.BaseResponse;
import app.karacal.network.models.response.ResetPasswordResponse;
import io.reactivex.Observable;
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
    Observable<BaseResponse> register(@Body RegisterRequest registerRequest);

    @POST("init/reset-password")
    Observable<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest registerRequest);
}
