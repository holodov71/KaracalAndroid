package app.karacal.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.SearchFilterActivity;
import app.karacal.adapters.SearchTagsAdapter;
import app.karacal.adapters.TourVerticalListAdapter;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.TextInputHelper;
import app.karacal.models.SearchFilter;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;

import static android.app.Activity.RESULT_OK;

public class MainSearchFragment extends Fragment {


    @Inject
    TourRepository tourRepository;

    private EditText editTextSearch;
    private ImageView buttonClear;
    private RecyclerView recyclerViewTags;
    private SearchTagsAdapter adapterTags;
    private RecyclerView recyclerViewResults;
    private TourVerticalListAdapter adapterResults;

    private SearchFilter searchFilter = new SearchFilter();

    private List<Tour> allTours = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_search, container, false);
        setupFilterButton(view);
        setupSearchResult(view);
        setupSearchField(view);
        return view;
    }

    private void setupFilterButton(View view){
        ImageView buttonFilter = view.findViewById(R.id.buttonFilter);
        buttonFilter.setVisibility(View.GONE);
        buttonFilter.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchFilterActivity.class);
            SearchFilterActivity.Args args = new SearchFilterActivity.Args(searchFilter);
            intent.putExtras(args.toBundle());
            startActivityForResult(intent, SearchFilterActivity.REQUEST_CODE);
        });
    }

    @SuppressLint("CheckResult")
    private void setupSearchField(View view){
        ConstraintLayout searchFieldLayout = view.findViewById(R.id.constraintLayout);
        buttonClear = view.findViewById(R.id.buttonClear);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonClear.setOnClickListener(v -> editTextSearch.setText(""));
        TextInputHelper.editTextObservable(editTextSearch).subscribe((search) -> {
            //TODO implement
            TransitionManager.beginDelayedTransition(searchFieldLayout);
            buttonClear.setVisibility(search.length() < 2 ? View.GONE : View.VISIBLE);
//            recyclerViewTags.setVisibility(search.length() < 2 ? View.VISIBLE : View.INVISIBLE);
            recyclerViewTags.setVisibility(View.INVISIBLE);
            searchByText(search);
        }, (throwable) -> {
            //TODO implement
        }, () -> {
            //TODO implement
        });
    }

    private void setupSearchResult(View view){
        recyclerViewTags = view.findViewById(R.id.recyclerViewTags);
        adapterTags = new SearchTagsAdapter(getContext());
        adapterTags.setTagClickListener(tag -> editTextSearch.setText(tag));
        recyclerViewTags.setAdapter(adapterTags);
        recyclerViewTags.setVisibility(View.INVISIBLE);
        recyclerViewResults = view.findViewById(R.id.recyclerViewResults);
        adapterResults = new TourVerticalListAdapter(getContext());
        adapterResults.setClickListener(this::showTour);
        recyclerViewResults.setAdapter(adapterResults);
        recyclerViewResults.setVisibility(View.VISIBLE);
        observeTours();
    }

    private void observeTours(){
        tourRepository.originalToursLiveData.observe(getViewLifecycleOwner(), tours -> {
            if (!tours.isEmpty()) {
                adapterResults.setTours(tours);
                allTours.clear();
                allTours.addAll(tours);
            }
        });
    }

    private void searchByText(String query){
        ArrayList<Tour> tours = new ArrayList<>();
        if (query.isEmpty()){
            tours.addAll(allTours);
        } else {
            tours.addAll(tourRepository.searchToursByText(query));
        }
        adapterResults.setTours(tours);
//        recyclerViewResults.setVisibility(query.length() < 2 ? View.INVISIBLE : View.VISIBLE);
    }

    private void showTour(int tourId){
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SearchFilterActivity.REQUEST_CODE && resultCode == RESULT_OK){
            SearchFilterActivity.Args args = SearchFilterActivity.Args.fromBundle(SearchFilterActivity.Args.class, data.getExtras());
            searchFilter = args.getSearchFilter();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
