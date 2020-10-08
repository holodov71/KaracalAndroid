package app.karacal.data;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.karacal.helpers.AESEncyption;
import app.karacal.helpers.PreferenceHelper;
import app.karacal.models.CardDetails;

public class SavedPaymentMethods{
    private List<CardDetails> cardsList;

    private SavedPaymentMethods(List<CardDetails> cardsList) {
        this.cardsList = cardsList;
    }

    private SavedPaymentMethods(String savedMethods) {
        this(new Gson().fromJson(savedMethods, SavedPaymentMethods.class).getPaymentMethodsList());
    }

    public static SavedPaymentMethods getInstance(Context context) throws Exception {
        String decrypt = AESEncyption.decrypt(PreferenceHelper.getPaymentMethods(context));
        return new SavedPaymentMethods(decrypt);
    }

    public static SavedPaymentMethods getEmptyInstance(){
        return new SavedPaymentMethods(new ArrayList<>());
    }

    public String retrieveStringFormat() throws Exception{
        return AESEncyption.encrypt(new Gson().toJson(this));
    }

    public List<CardDetails> addPaymentMethod(Context context, CardDetails cardDetails) throws Exception {
        if (cardsList.isEmpty()){
            cardDetails.setDefault(true);
        }
        cardsList.add(cardDetails);
        saveChanges(context);
        return cardsList;
    }

    public CardDetails getDefaultPaymentMethod(){
        for(CardDetails cardDetails: cardsList){
            if (cardDetails.isDefault()) return cardDetails;
        }
        return null;
    }

    public List<CardDetails> deletePaymentMethod(Context context, int position) throws Exception {
        boolean isDef = cardsList.get(position).isDefault();

        cardsList.remove(position);

        if (isDef && cardsList.size() > 0) {
            cardsList.get(0).setDefault(true);
        }

        saveChanges(context);
        return cardsList;
    }

    public List<CardDetails> makePaymentMethodDefault(Context context, int position) throws Exception {
        for (CardDetails cardDetails: cardsList){
            cardDetails.setDefault(false);
        }
        cardsList.get(position).setDefault(true);
        saveChanges(context);
        return cardsList;
    }

    private void saveChanges(Context context) throws Exception {
        String encrypted = AESEncyption.encrypt(new Gson().toJson(this));
        PreferenceHelper.setPaymentMethods(context, encrypted);
    }

    public void clear(Context context){
        PreferenceHelper.setPaymentMethods(context,
                NotificationsCache.getEmptyInstance().retrieveStringFormat());
    }

    public List<CardDetails> getPaymentMethodsList() {
            return cardsList;
        }
}