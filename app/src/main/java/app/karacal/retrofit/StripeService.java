package app.karacal.retrofit;

import java.util.Map;

import app.karacal.retrofit.models.request.PaymentRequest;
import app.karacal.retrofit.models.response.BaseResponse;
import app.karacal.retrofit.models.response.PaymentResponse;
import app.karacal.retrofit.models.response.PaymentsListResponse;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface StripeService {

    @FormUrlEncoded
    @POST("ephemeral_keys")
    Observable<ResponseBody> createEphemeralKey(@FieldMap Map<String, String> apiVersionMap);

    @POST("payments")
    Observable<PaymentResponse> makePayment(@Header("Authorization") String token, @Body PaymentRequest request);

    @GET("payments")
    Observable<PaymentsListResponse> getPayments(@Header("Authorization") String token);
}
