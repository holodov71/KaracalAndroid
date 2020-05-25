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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.activities.CategoryActivity;
import app.karacal.activities.ProfileActivity;
import app.karacal.adapters.GuideHorizontalListAdapter;
import app.karacal.adapters.TourHorizontalListAdapter;
import app.karacal.data.repository.GuideRepository;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.EmailHelper;
import app.karacal.helpers.ProfileHolder;
import app.karacal.helpers.WebLinkHelper;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.MainActivityViewModel;

public class MainMenuFragment extends Fragment {

    private MainActivityViewModel viewModel;

    private GoogleSignInClient mGoogleApiClient;

    @Inject
    TourRepository tourRepository;

    @Inject
    GuideRepository guideRepository;

    @Inject
    ProfileHolder profileHolder;

    private TourHorizontalListAdapter.TourClickListener tourClickListener = this::showTour;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);
        setupGoogleClient();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

        viewModel.loadGuides();

        setupButtons(view);
        setupCategories(view);


        return view;
    }

    private void setupButtons(View view) {
        LinearLayout buttonRefer = view.findViewById(R.id.buttonReferFriend);
        buttonRefer.setOnClickListener(v -> NavigationHelper.startReferFriendActivity(getActivity()));
        View buttonHowWeChoose = view.findViewById(R.id.buttonHowWeChoose);
        buttonHowWeChoose.setOnClickListener(v -> WebLinkHelper.howWeChooseOurGuide(getActivity()));
        LinearLayout buttonContact = view.findViewById(R.id.buttonContactKaracal);
        buttonContact.setOnClickListener(v -> EmailHelper.contactKaracal(getActivity()));
        LinearLayout buttonSettings = view.findViewById(R.id.buttonSettings);
        buttonSettings.setOnClickListener(v -> NavigationHelper.startSettingsActivity(getActivity()));
        LinearLayout buttonDashboard = view.findViewById(R.id.buttonDashboardGuide);
        buttonDashboard.setVisibility(View.GONE);
        buttonDashboard.setOnClickListener(v -> NavigationHelper.startDashboardActivity(getActivity()));
        LinearLayout buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> logout());
    }

    private void setupCategories(View view) {
        View categoryRecommended = view.findViewById(R.id.categoryRecommended);
        categoryRecommended.setVisibility(View.GONE);
//        setupTourCategory(categoryRecommended, 0, getString(R.string.recommended_for_you), tourRepository.getRecommendedTours());
        View categoryRecommendedGuide = view.findViewById(R.id.categoryRecommendedGuides);
        setupGuideCategory(categoryRecommendedGuide, 1, getString(R.string.recommended_for_you));

        View categoryDownloaded = view.findViewById(R.id.categoryDownloaded);
        categoryDownloaded.setVisibility(View.GONE);
//        setupTourCategory(categoryDownloaded, 2, getString(R.string.already_downloaded), tourRepository.getOriginalTours());
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
        adapter.setClickListener((guideId) -> {
            ProfileActivity.Args args = new ProfileActivity.Args(guideId);
            NavigationHelper.startProfileActivity(getActivity(), args);
        });
        observeGuidesList(adapter);
    }

    private void observeGuidesList(GuideHorizontalListAdapter adapter){
        viewModel.getGuides().observe(getViewLifecycleOwner(), adapter::setGuidesList);
    }

    private void showCategory(int categoryId, String categoryName) {
        CategoryActivity.Args args = new CategoryActivity.Args(categoryId, categoryName);
        NavigationHelper.startCategoryActivity(getActivity(), args);
    }

    private void showTour(int tourId) {
        AudioActivity.Args args = new AudioActivity.Args(tourId);
        NavigationHelper.startAudioActivity(getActivity(), args);
    }

    private void setupGoogleClient() {
        String serverClientId = getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(serverClientId)
                .requestEmail()
                .build();
        mGoogleApiClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    private void logout(){
        try {
            LoginManager.getInstance().logOut();
            mGoogleApiClient.signOut();

            if (getActivity() != null){
                profileHolder.removeProfile(getActivity());

                NavigationHelper.startLoginActivity(getActivity());
                getActivity().finish();
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
