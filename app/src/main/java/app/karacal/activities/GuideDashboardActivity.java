package app.karacal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.DashboardTourPagerAdapter;
import app.karacal.data.ProfileCache;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Profile;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.DashboardActivityViewModel;
import apps.in.android_logger.LogActivity;
import apps.in.android_logger.Logger;

public class GuideDashboardActivity extends LogActivity {

    private ImageView avatar;
    private ProgressBar progressLoading;
    private DashboardActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(this).get(DashboardActivityViewModel.class);

        setContentView(R.layout.activity_dashboard);
        setupButtons();
        Profile profile = viewModel.getProfile(this);
        setupAuthor(profile);
        setupViewPager(profile);
        setupProgressLoading();

        observeViewModel();
    }

    private void setupButtons(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
        ImageView buttonPhoto = findViewById(R.id.buttonPhoto);
        buttonPhoto.setOnClickListener(v -> {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setAspectRatio(1,1)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setAllowFlipping(false)
                    .setActivityMenuIconColor(getColor(R.color.colorTextOrange))
                    .setFixAspectRatio(true)
                    .start(this);
        });
        Button buttonFollowListenings = findViewById(R.id.buttonFollowMyListenings);
        buttonFollowListenings.setVisibility(View.GONE);
        buttonFollowListenings.setOnClickListener(v -> NavigationHelper.startFollowMyListeningsActivity(this));
        Button buttonCreateTour = findViewById(R.id.buttonCreateTour);
        buttonCreateTour.setOnClickListener(v -> NavigationHelper.startEditGuideActivity(this,  new AddEditTourActivity.Args(null)));
    }

    private void setupAuthor(Profile profile){

        avatar = findViewById(R.id.imageViewAvatar);
        ImageHelper.setImage(avatar, profile.getAvatar(), R.drawable.ic_person, true);

        TextView textViewName = findViewById(R.id.textViewGuideName);
        textViewName.setText(profile.getName());
        TextView textViewLocation = findViewById(R.id.textViewGuideLocation);
        textViewLocation.setText("");
    }

    private void setupViewPager(Profile profile){
        ViewPager viewPager = findViewById(R.id.viewPager);
        DashboardTourPagerAdapter adapter = new DashboardTourPagerAdapter(this, getSupportFragmentManager(), ProfileCache.getInstance(this).getGuideId());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupProgressLoading(){
        progressLoading = findViewById(R.id.progressLoading);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                viewModel.changeAvatar(this, resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Logger.log(GuideDashboardActivity.this, "Image cropping error", error);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void observeViewModel() {
        viewModel.getAvatarUploadedAction().observe(this, filePath -> {
            ImageHelper.setImage(avatar, filePath, R.drawable.ic_person, true);
        });

        viewModel.getAvatarUploadingLiveData().observe(this, isUploading -> {
            progressLoading.setVisibility(isUploading ? View.VISIBLE : View.GONE);
            avatar.setVisibility(isUploading ? View.INVISIBLE : View.VISIBLE);
        });

        viewModel.getAvatarUploadingErrorAction().observe(this, errorMessage -> {
            ToastHelper.showToast(this, errorMessage);
        });
    }


}
