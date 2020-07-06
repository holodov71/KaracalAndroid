package app.karacal.network;

import java.util.Map;

import app.karacal.network.models.request.CreateCardRequest;
import app.karacal.network.models.request.CreateCustomerRequest;
import app.karacal.network.models.request.CreateSubscriptionRequest;
import app.karacal.network.models.request.PaymentRequest;
import app.karacal.network.models.response.BaseResponse;
import app.karacal.network.models.response.CreateCardResponse;
import app.karacal.network.models.response.CreateCustomerResponse;
import app.karacal.network.models.response.CreateSubscriptionResponse;
import app.karacal.network.models.response.PaymentResponse;
import app.karacal.network.models.response.PaymentsListResponse;
import app.karacal.network.models.response.SubscriptionsListResponse;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StripeService {

    @FormUrlEncoded
    @POST("ephemeral_keys")
    Observable<ResponseBody> createEphemeralKey(@FieldMap Map<String, String> apiVersionMap);

    @POST("payments")
    Observable<PaymentResponse> makePayment(@Header("Authorization") String token, @Body PaymentRequest request);

    @GET("payments")
    Observable<PaymentsListResponse> getPayments(@Header("Authorization") String token);

    @POST("payments/create-customer")
    Observable<CreateCustomerResponse> createCustomer(@Header("Authorization") String token, @Body CreateCustomerRequest request);

    @POST("payments/create-card")
    Observable<CreateCardResponse> createCard(@Header("Authorization") String token, @Body CreateCardRequest request);

    @POST("payments/create-subscription")
    Observable<CreateSubscriptionResponse> createSubscription(@Header("Authorization") String token, @Body CreateSubscriptionRequest request);

    @DELETE("payments/cancel-subscription/{subscription_id}")
    Observable<BaseResponse> cancelSubscription(@Header("Authorization") String token, @Path("subscription_id") String subscriptionId);


    @GET("payments/list-subscription/{customer_id}")
    Observable<SubscriptionsListResponse> getSubscriptions(@Header("Authorization") String token, @Path("customer_id") String customerId);


}
