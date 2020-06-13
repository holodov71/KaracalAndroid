package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Locale;

import app.karacal.R;
import app.karacal.helpers.ImageHelper;
import app.karacal.models.Tour;

public class TourStackItemFragment extends Fragment {

    private Tour tour;

    public static TourStackItemFragment create(Tour tour){
        TourStackItemFragment fragment = new TourStackItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(Tour.class.getName(), tour);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        tour = (Tour) args.getSerializable(Tour.class.getName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_tour_stack_list, container, false);
        setupImageTitle(view);
        setupPrice(view);
        return view;
    }

    private void setupImageTitle(View view) {
        ImageView imageView = view.findViewById(R.id.imageViewTitle);
        ImageHelper.setImage(imageView, tour.getImageUrl(), tour.getImage(), false);
    }

    private void setupPrice(View view){
        TextView textView = view.findViewById(R.id.textViewPrice);
        if (tour.getPrice() != 0){
            textView.setVisibility(View.VISIBLE);
            textView.setText(getContext().getString(R.string.price_format, tour.getDoublePrice(), getContext().getString(R.string.euro)));
        } else {
            textView.setVisibility(View.GONE);
        }

    }

    public Tour getTour(){
        return tour;
    }
}
