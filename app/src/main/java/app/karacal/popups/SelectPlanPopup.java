package app.karacal.popups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import app.karacal.R;

public class SelectPlanPopup extends BasePopup {

    private static final double REGULAR_PRICE = 9.99;

    public interface SelectPlanPopupCallbacks{
        void onButtonCancelClick(BasePopup popup);
        void onButtonSinglePriceClick(BasePopup popup);
        void onButtonRegularPriceClick(BasePopup popup);
    }

    private final SelectPlanPopup.SelectPlanPopupCallbacks callbacks;

    private final View view;

    private final long price;

    public SelectPlanPopup(ViewGroup parent, SelectPlanPopup.SelectPlanPopupCallbacks callbacks, long price) {
        super(parent);
        this.callbacks = callbacks;
        this.price = price;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.dialog_select_plan, parent, false);
        setup(view);
    }

    private void setup(View view){
        Context context = view.getContext();
        TextView buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> callbacks.onButtonCancelClick(this));
        TextView textViewSinglePrice = view.findViewById(R.id.textViewSinglePrice);
        double doublePrice = ((double)price)/100;
        textViewSinglePrice.setText(context.getString(R.string.price_format, doublePrice, context.getString(R.string.euro)));
        TextView textViewRegularPrice = view.findViewById(R.id.textViewRegularPrice);
        textViewRegularPrice.setText(context.getString(R.string.price_format, REGULAR_PRICE, context.getString(R.string.euro)));
        ConstraintLayout buttonSinglePrice = view.findViewById(R.id.buttonSinglePrice);
        buttonSinglePrice.setOnClickListener(v -> callbacks.onButtonSinglePriceClick(this));
        ConstraintLayout buttonRegularPrice = view.findViewById(R.id.buttonRegularPrice);
        buttonRegularPrice.setOnClickListener(v -> callbacks.onButtonRegularPriceClick(this));
    }

    @Override
    public View getView() {
        return view;
    }
}
