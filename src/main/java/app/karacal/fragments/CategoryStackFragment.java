package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.adapters.TourStackPagerAdapter;
import app.karacal.helpers.StackPageTransformer;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.CategoryActivityViewModel;
import app.karacal.views.StarsView;

public class CategoryStackFragment extends Fragment {

    private CategoryActivityViewModel viewModel;

    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewDuration;
    private StarsView starsView;
    private Button buttonDetails;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(CategoryActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_stack, container, false);
        setupTitleTextView(view);
        setupDescriptionTextView(view);
        setupDurationTextView(view);
        setupStarsView(view);
        setupDetailsButton(view);
        setupViewPager(view);
        return view;
    }

    private void setupViewPager(View view) {
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TourStackPagerAdapter adapter = new TourStackPagerAdapter(getFragmentManager(), viewModel.getTours());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Do nothing
            }

            @Override
            public void onPageSelected(int position) {
                Tour tour = adapter.getTour(position);
                setContent(tour);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //Do nothing
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageTransformer(true, new StackPageTransformer());
        setContent(adapter.getTour(viewPager.getCurrentItem()));
    }

    private void setupTitleTextView(View view) {
        textViewTitle = view.findViewById(R.id.textViewTitle);
    }

    private void setupDescriptionTextView(View view) {
        textViewDescription = view.findViewById(R.id.textViewDescription);
    }

    private void setupDurationTextView(View view){
        textViewDuration = view.findViewById(R.id.textViewDuration);
    }

    private void setupStarsView(View view) {
        starsView = view.findViewById(R.id.starsView);
    }

    private void setupDetailsButton(View view){
        buttonDetails = view.findViewById(R.id.buttonDetails);
    }

    private void setContent(Tour tour){
        setTitleText(tour);
        setDescriptionText(tour);
        setDurationText(tour);
        setRating(tour);
        setButtonDetails(tour);
    }


    private void setTitleText(Tour tour){
        textViewTitle.setText(tour.getTitle());
    }

    private void setDescriptionText(Tour tour){
        textViewDescription.setText(tour.getDescription());
    }

    private void setDurationText(Tour tour){
        int hours = tour.getDuration() / 60;
        int minutes = tour.getDuration() % 60;
        String duration = textViewDuration.getContext().getString(R.string.duration_format, hours, minutes);
        textViewDuration.setText(duration);
    }

    private void setRating(Tour tour){
        starsView.setRating(tour.getRating());
    }

    private void setButtonDetails(Tour tour){
        buttonDetails.setOnClickListener(v -> showTour(tour.getId()));
    }

    private void showTour(int tourId){
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }
}
