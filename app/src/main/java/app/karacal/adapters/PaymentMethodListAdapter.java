package app.karacal.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.karacal.R;
import app.karacal.models.CardDetails;

public class PaymentMethodListAdapter extends RecyclerView.Adapter<PaymentMethodListAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView textViewCardNumber;
        private final TextView textViewExpiryDate;
        private final TextView textViewCardOwner;
        private final TextView buttonMakeDefault;
        private final ImageView buttonDeletePaymentMethod;
        private final View layoutPaymentMethod;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewCardNumber = itemView.findViewById(R.id.tvCardNumber);
            textViewExpiryDate = itemView.findViewById(R.id.tvCardExpiry);
            textViewCardOwner = itemView.findViewById(R.id.tvCardOwner);
            buttonMakeDefault = itemView.findViewById(R.id.tvMakeDefault);
            layoutPaymentMethod = itemView.findViewById(R.id.layoutPaymentMethod);
            buttonDeletePaymentMethod = itemView.findViewById(R.id.imgDeletePaymentMethod);
        }

        void bind(CardDetails cardDetails, int position) {
            String cardNumber = cardDetails.getNumber();
            if (cardNumber.length() > 4){
                cardNumber = cardNumber.substring(cardNumber.length() - 4);
            }

            cardNumber = cardDetails.getBrand() + " (" + cardNumber +")";

            textViewCardNumber.setText(cardNumber);


            String expYear = String.valueOf(cardDetails.getExpYear()).substring(2);
            String expMonth = String.valueOf(cardDetails.getExpMonth());
            if (expMonth.length() == 1) {
                expMonth = "0" + expMonth;
            }
            String expiry = textViewExpiryDate.getContext().getString(R.string.expiry, expMonth, expYear);
            textViewExpiryDate.setText(expiry);

            buttonDeletePaymentMethod.setOnClickListener(v -> paymentMethodCallback.onDeletePaymentMethodClick(position));

            if (cardDetails.isDefault()){
                buttonMakeDefault.setVisibility(View.GONE);
            } else {
                buttonMakeDefault.setVisibility(View.VISIBLE);
                buttonMakeDefault.setOnClickListener(v -> paymentMethodCallback.onMakeDefaultClick(position));
            }

            textViewCardOwner.setText(cardDetails.getOwner());


        }
    }


    private final ArrayList<CardDetails> paymentMethods = new ArrayList<>();

    private final PaymentMethodCallback paymentMethodCallback;

    public PaymentMethodListAdapter(PaymentMethodCallback paymentMethodCallback) {
        this.paymentMethodCallback = paymentMethodCallback;
    }

    @NonNull
    @Override
    public PaymentMethodListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new PaymentMethodListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodListAdapter.ViewHolder holder, int position) {
        holder.bind(paymentMethods.get(position), position);
    }

    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public void setPaymentMethods(List<CardDetails> paymentMethods) {
        this.paymentMethods.clear();
        this.paymentMethods.addAll(paymentMethods);
        notifyDataSetChanged();
    }

    public interface PaymentMethodCallback{
        void onDeletePaymentMethodClick(int position);

        void onMakeDefaultClick(int position);
    }


}
