package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CategoryActivity;
import app.karacal.adapters.GuideHorizontalListAdapter;
import app.karacal.adapters.TourHorizontalListAdapter;
import app.karacal.data.TourRepository;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.EmailHelper;
import app.karacal.helpers.WebLinkHelper;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;

public class MainMenuFragment extends Fragment {

    @Inject
    TourRepository tourRepository;

    private TourHorizontalListAdapter.TourClickListener tourClickListener = this::showTour;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        setupButtons(view);
        setupCategories(view);
        return view;
    }

    private void setupButtons(View view) {
        LinearLayout buttonRefer = view.findViewById(R.id.buttonReferFriend);
        buttonRefer.setOnClickListener(v -> NavigationHelper.startReferFriendActivity(getActivity()));
        LinearLayout buttonHowWeChoose = view.findViewById(R.id.buttonHowWeChoose);
        buttonHowWeChoose.setOnClickListener(v -> WebLinkHelper.howWeChooseOurGuide(getActivity()));
        LinearLayout buttonContact = view.findViewById(R.id.buttonContactKaracal);
        buttonContact.setOnClickListener(v -> EmailHelper.contactKaracal(getActivity()));
        LinearLayout buttonSettings = view.findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> NavigationHelper.startSettingsActivity(getActivity()));
        LinearLayout buttonDashboard = view.findViewById(R.id.buttonDashboardGuide);
        buttonDashboard.setOnClickListener(v -> NavigationHelper.startDashboardActivity(getActivity()));
    }

    private void setupCategories(View view) {
        View categoryRecommended = view.findViewById(R.id.categoryRecommended);
        setupTourCategory(categoryRecommended, 0, getString(R.string.recommended_for_you), tourRepository.getRecommendedTours());
        View categoryRecommendedGuide = view.findViewById(R.id.categoryRecommendedGuides);
        setupGuideCategory(categoryRecommendedGuide, 1, getString(R.string.recommended_for_you));
        View categoryDownloaded = view.findViewById(R.id.categoryDownloaded);
        setupTourCategory(categoryDownloaded, 2, getString(R.string.already_downloaded), tourRepository.getOriginalTours());
    }

    private void setupTourCategory(View categoryView, int id, String title, ArrayList<Tour> tours) {
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

    private void setupGuideCategory(View categoryView, int id, String title) {
        TextView textViewTitle = categoryView.findViewById(R.id.textViewTitle);
        if (textViewTitle != null) {
            textViewTitle.setText(title);
        }
        TextView textViewViewAll = categoryView.findViewById(R.id.textViewViewAll);
        textViewViewAll.setOnClickListener(v -> DummyHelper.dummyAction(getContext()));
        RecyclerView recyclerView = categoryView.findViewById(R.id.recyclerView);
        GuideHorizontalListAdapter adapter = new GuideHorizontalListAdapter(getContext());
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(() -> NavigationHelper.startProfileActivity(getActivity()));
    }

    private void showCategory(int categoryId, String categoryName) {
        CategoryActivity.Args args = new CategoryActivity.Args(categoryId, categoryName);
        NavigationHelper.startCategoryActivity(getActivity(), args);
    }

    private void showTour(int tourId) {
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }
}
