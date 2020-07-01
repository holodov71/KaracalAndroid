package app.karacal.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CategoryActivity;
import app.karacal.adapters.TourHorizontalListAdapter;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.ProfileHolder;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.MainActivityViewModel;

public class MainHomeFragment extends Fragment {

    private View categoryRecommended;
    private View categoryNear;
    private View categoryOriginal;

    private MainActivityViewModel viewModel;

    @Inject
    ProfileHolder profileHolder;

    @Inject
    TourRepository tourRepository;

    private TourHorizontalListAdapter.TourClickListener tourClickListener = this::showTour;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_home, container, false);

        setupLogo(view);
        setupGreetings(view);
        setupSeeAroundMeButton(view);
        setupCategories(view);

        viewModel.loadTours();
        viewModel.loadOriginalTours();

        observeRecommendedTours();
        observeNearTours();
        observeOriginalTours();
        observeLocation();
        return view;
    }

    private void setupLogo(View view){
        ImageView imageView = view.findViewById(R.id.imageTitle);

        Glide.with(this)
                .load(R.mipmap.main_logo)
                .centerCrop()
                .into(imageView);
    }

    private void setupGreetings(View view) {
        TextView textView = view.findViewById(R.id.textViewGreetings);
        textView.setText(getString(R.string.greetings, profileHolder.getProfile().getFirstName()));
    }

    private void setupSeeAroundMeButton(View view) {
        Button button = view.findViewById(R.id.buttonSeeAroundMe);
        button.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.mainLocationFragment));
    }

    private void observeNearTours(){
        viewModel.getNearTours().observe(getViewLifecycleOwner(), tours -> {
            if (!tours.isEmpty()) {
                setupCategory(categoryNear, 1, getString(R.string.meters_from_you), tours);
            }
        });
    }

    private void observeOriginalTours(){
        viewModel.getOriginalTours().observe(getViewLifecycleOwner(), tours -> {
            if (!tours.isEmpty()) {
                setupCategory(categoryOriginal, 2, getString(R.string.originals), tours);
            }
        });

//        TourHorizontalListPagerAdapter pagingAdapter = new TourHorizontalListPagerAdapter(new TourComparator());
//        RecyclerView recyclerView = categoryOriginal.findViewById(R.id.recyclerView);
//        recyclerView.setAdapter(pagingAdapter);
//
//        viewModel.flowable
//                .autoDispose(this) // Using AutoDispose to handle subscription lifecycle.
//                .subscribe(pagingData -> pagingAdapter.submitData(getLifecycle(), pagingData));
    }



    private void observeRecommendedTours(){
        viewModel.getTours().observe(getViewLifecycleOwner(), tours -> {
            Log.v("getTours", "observeRecommendedTours");
            if (!tours.isEmpty()) {
                setupCategory(categoryRecommended, 0, getString(R.string.recommended_for_you), tours);
            }
        });
    }

    private void observeLocation(){
        viewModel.subscribeLocationUpdates(this, location -> {
            if (location != null) {
                viewModel.loadNearTours(location);
            }
        });
    }

    private void setupCategories(View view) {
        categoryRecommended = view.findViewById(R.id.categoryRecommended);
        categoryNear = view.findViewById(R.id.categoryNear);
        categoryOriginal = view.findViewById(R.id.categoryOriginals);
    }

    private void setupCategory(View categoryView, int id, String title, List<Tour> tours) {
        categoryView.setVisibility(View.VISIBLE);

        TextView textViewTitle = categoryView.findViewById(R.id.textViewTitle);
        if (textViewTitle != null) {
            textViewTitle.setText(title);
        }
        TextView textViewViewAll = categoryView.findViewById(R.id.textViewViewAll);
        textViewViewAll.setOnClickListener(v -> showCategory(id, title));
        RecyclerView recyclerView = categoryView.findViewById(R.id.recyclerView);
        TourHorizontalListAdapter adapter = new TourHorizontalListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setTours(tours);
        adapter.setClickListener(tourClickListener);
    }

    private void showCategory(int categoryId, String categoryName){
        CategoryActivity.Args args = new CategoryActivity.Args(categoryId, categoryName);
        NavigationHelper.startCategoryActivity(getActivity(), args);
    }

    private void showTour(int tourId){
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }
}
