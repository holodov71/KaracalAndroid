package app.karacal.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.SavedPaymentMethods;
import app.karacal.helpers.ApiHelper;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.models.CardDetails;
import app.karacal.network.models.request.CreateCardRequest;
import app.karacal.network.models.request.CreateCustomerRequest;
import app.karacal.network.models.request.CreateSubscriptionRequest;
import io.reactivex.disposables.CompositeDisposable;

public class SubscriptionActivityViewModel extends ViewModel {

    @Inject
    ProfileHolder profileHolder;

    @Inject
    ApiHelper apiHelper;

    private String customerId;
    private String subscriptionId;

    private CompositeDisposable disposable = new CompositeDisposable();

    private Stripe stripe;

    public SubscriptionActivityViewModel() {
        App.getAppComponent().inject(this);
        stripe = new Stripe(App.getInstance().getApplicationContext(), App.getResString(R.string.stripe_api_key));
        loadData(App.getInstance());
    }

    private SavedPaymentMethods savedPaymentMethods;

    public SingleLiveEvent<Void> subscriptionCanceledEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<String> subscriptionOpenedEvent = new SingleLiveEvent<>();

    public SingleLiveEvent<String> errorEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<Void> finishEvent = new SingleLiveEvent<>();

    public SingleLiveEvent<Boolean> hasPaymentMethod = new SingleLiveEvent<>();

    public SingleLiveEvent<Boolean> hasSubscription = new SingleLiveEvent<>();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<Boolean> isLoading(){
        return isLoading;
    }


    private void loadData(Context context){
        try {
            savedPaymentMethods = SavedPaymentMethods.getInstance(context);
            hasPaymentMethod.postValue(!savedPaymentMethods.getPaymentMethodsList().isEmpty());
        } catch (Exception e) {
            e.printStackTrace();
        }

        hasSubscription.postValue(profileHolder.isHasSubscription());

        subscriptionId = context.getString(R.string.monthly_subscription);
        createCustomer(context);
    }

    private void createCustomer(Context context){

        String serverToken = PreferenceHelper.loadToken(context);

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest(profileHolder.getProfile().getEmail());
        disposable.add(apiHelper.createCustomer(serverToken, createCustomerRequest)
                .subscribe(response -> {
                    Log.v("createCustomer", "Success response = " + response);
                    isLoading.setValue(false);
                    if (response.isSuccess()) {
                        customerId = response.getId();
                    } else {
                        errorEvent.setValue(response.getErrorMessage());
                        finishEvent.call();
                    }
                }, throwable -> {
                    errorEvent.setValue(App.getResString(R.string.connection_problem));
                    finishEvent.call();
                }));
    }

    public void toggleSubscription() {
        if(profileHolder.isHasSubscription()){
            cancelSubscription();
        } else {
            obtainCardToken();
        }
    }

    private void cancelSubscription(){
        isLoading.setValue(true);

        disposable.add(apiHelper.cancelSubscription(PreferenceHelper.loadToken(), profileHolder.getSubscriptionId())
                .subscribe(response -> {
                    Log.v("cancelSubscription", "Success response = " + response);
                    if (response.isSuccess()) {
                        profileHolder.setSubscription(null);
                        subscriptionCanceledEvent.call();
                    } else {
                        errorEvent.setValue(response.getErrorMessage());
                    }
                    isLoading.setValue(false);
                }, throwable -> {
                    errorEvent.setValue(App.getResString(R.string.connection_problem));
                    isLoading.setValue(false);
                }));
    }

    private void obtainCardToken(){
        isLoading.setValue(true);

        CardDetails paymentMethod = savedPaymentMethods.getDefaultPaymentMethod();

        Card card = new Card.Builder(
                paymentMethod.getNumber(),
                paymentMethod.getExpMonth(),
                paymentMethod.getExpYear(),
                paymentMethod.getCvc())
                .build();

                stripe.createCardToken(card, new ApiResultCallback<Token>() {
                    @Override
                    public void onSuccess(Token token) {
                        createCard(token.getId());
                    }

                    @Override
                    public void onError(@NotNull Exception e) {
                        isLoading.setValue(false);
                        errorEvent.setValue(App.getResString(R.string.common_error));
                    }
                });
    }

    private void createCard(String cardToken){
        String serverToken = PreferenceHelper.loadToken(App.getInstance());

        CreateCardRequest createCardRequest = new CreateCardRequest(customerId, cardToken);
        disposable.add(apiHelper.createCard(serverToken, createCardRequest)
                .subscribe(response -> {
                    Log.v("createCard", "Success response = " + response);
                    if (response.isSuccess()) {
                        openSubscription(customerId);
                    } else {
                        isLoading.setValue(false);
                        errorEvent.setValue(response.getErrorMessage());
                    }
                }, throwable -> {
                    isLoading.setValue(false);
                    errorEvent.setValue(App.getResString(R.string.connection_problem));
                }));
    }

    private void openSubscription(String customerId){
        CreateSubscriptionRequest createSubscriptionRequest = new CreateSubscriptionRequest(customerId, subscriptionId);
        disposable.add(apiHelper.createSubscription(PreferenceHelper.loadToken(App.getInstance()), createSubscriptionRequest)
                .subscribe(response -> {
                    Log.v("createCustomer", "Success response = " + response);
                    if (response.isSuccess()) {
                        profileHolder.setSubscription(response.getSubscriptionId());
                        subscriptionOpenedEvent.setValue(response.getSubscription());
                    } else {
                        errorEvent.setValue(response.getErrorMessage());
                    }
                    isLoading.setValue(false);
                }, throwable -> {
                    isLoading.setValue(false);
                    errorEvent.setValue(App.getResString(R.string.connection_problem));
                }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.dispose();
    }
}
