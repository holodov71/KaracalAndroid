package app.karacal.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import app.karacal.R;
import app.karacal.adapters.DashboardTourPagerAdapter;
import app.karacal.navigation.NavigationHelper;
import apps.in.android_logger.LogActivity;
import apps.in.android_logger.Logger;

public class DashboardActivity extends LogActivity {

    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        setupButtons();
        setupAuthor();
        setupViewPager();
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
//        buttonFollowListenings.setVisibility(View.GONE);
        buttonFollowListenings.setOnClickListener(v -> NavigationHelper.startFollowMyListeningsActivity(this));
        Button buttonCreateTour = findViewById(R.id.buttonCreateTour);
//        buttonCreateTour.setVisibility(View.GONE);
        buttonCreateTour.setOnClickListener(v -> NavigationHelper.startEditGuideActivity(this,  new EditGuideActivity.Args(null)));
    }

    private void setupAuthor(){
        int avatarResId = R.mipmap.avatar_example;
        String name = "Alexander McQueen";
        String location = "Paris, France";
        avatar = findViewById(R.id.imageViewAvatar);
        avatar.setImageResource(avatarResId);
        TextView textViewName = findViewById(R.id.textViewGuideName);
        textViewName.setText(name);
        TextView textViewLocation = findViewById(R.id.textViewGuideLocation);
        textViewLocation.setText(location);
    }

    private void setupViewPager(){
        ViewPager viewPager = findViewById(R.id.viewPager);
        DashboardTourPagerAdapter adapter = new DashboardTourPagerAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                avatar.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Logger.log(DashboardActivity.this, "Image cropping error", error);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
