package app.karacal.viewmodels;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import app.karacal.App;
import app.karacal.R;
import app.karacal.data.SavedPaymentMethods;
import app.karacal.models.CardDetails;

public class PaymentMethodViewModel extends ViewModel {

    public PaymentMethodViewModel() {
        App.getAppComponent().inject(this);
        loadPaymentMethods(App.getInstance());
    }

    private SavedPaymentMethods savedPaymentMethods;

    private SingleLiveEvent<String> errorEvent = new SingleLiveEvent<>();

    public SingleLiveEvent<String> getErrorEvent(){
        return errorEvent;
    }

    private MutableLiveData<String> activityTitle = new MutableLiveData<>();

    public LiveData<String> getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String title) {
        activityTitle.postValue(title);
    }

    private MutableLiveData<List<CardDetails>> paymentMethods = new MutableLiveData<>();

    public LiveData<List<CardDetails>> getPaymentMethods(){
        return paymentMethods;
    }

    public void loadPaymentMethods(Context context){
        try {
            savedPaymentMethods = SavedPaymentMethods.getInstance(context);
            paymentMethods.postValue(savedPaymentMethods.getPaymentMethodsList());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPaymentMethod(Context context, CardDetails cardDetails){
        try {
            paymentMethods.setValue(savedPaymentMethods.addPaymentMethod(context, cardDetails));
        } catch (Exception e) {
            errorEvent.setValue(App.getResString(R.string.can_not_add_payment_method));
            e.printStackTrace();
        }
    }

    public void deletePaymentMethod(Context context, int position){
        Log.v(App.TAG, "deletePaymentMethod");
        try {
            paymentMethods.setValue(savedPaymentMethods.deletePaymentMethod(context, position));
        } catch (Exception e) {
            errorEvent.setValue(App.getResString(R.string.can_not_delete_payment_method));
            e.printStackTrace();
        }
    }

    public void makePaymentMethodDefault(Context context, int position){
        Log.v(App.TAG, "makePaymentMethodDefault");
        try {
            paymentMethods.setValue(savedPaymentMethods.makePaymentMethodDefault(context, position));
        } catch (Exception e) {
            errorEvent.setValue(App.getResString(R.string.common_error));
            e.printStackTrace();
        }
    }

}
