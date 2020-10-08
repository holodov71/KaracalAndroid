package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.PaymentMethodListAdapter;
import app.karacal.viewmodels.PaymentMethodViewModel;

public class PaymentMethodListFragment extends Fragment {

    private PaymentMethodViewModel viewModel;

    private PaymentMethodListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(getActivity()).get(PaymentMethodViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_methods_list, container, false);
        viewModel.setActivityTitle(getString(R.string.payment));
        setupList(view);
        setupAddPaymentMethodButton(view);
        observePaymentMethods();
        return view;
    }

    private void setupList(View view){
        RecyclerView recyclerView = view.findViewById(R.id.rvPaymentMethods);
        adapter = new PaymentMethodListAdapter(new PaymentMethodListAdapter.PaymentMethodCallback(){

            @Override
            public void onDeletePaymentMethodClick(int position) {
                viewModel.deletePaymentMethod(getContext(), position);
            }

            @Override
            public void onMakeDefaultClick(int position) {
                viewModel.makePaymentMethodDefault(getContext(), position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void setupAddPaymentMethodButton(View view){
        Button button = view.findViewById(R.id.buttonAddPaymentMethod);
        button.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.paymentMethodAdd));
    }

    private void observePaymentMethods(){
        viewModel.getPaymentMethods().observe(getViewLifecycleOwner(), paymentMethods -> {
            if (paymentMethods != null) {
                adapter.setPaymentMethods(paymentMethods);
            }
        });
    }

}