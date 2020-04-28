package app.karacal.activities;

import android.Manifest;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.Serializable;
import java.util.Locale;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.HelpfulInformationPagerAdapter;
import app.karacal.data.TourRepository;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.TextInputHelper;
import app.karacal.models.Tour;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.EditGuideActivityViewModel;
import apps.in.android_logger.Logger;

public class EditGuideActivity extends PermissionActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final Integer tourId;

        public Args(Integer tourId) {
            this.tourId = tourId;
        }

        public Integer getTourId() {
            return tourId;
        }

    }

    @Inject
    TourRepository tourRepository;

    private Tour tour;

    private EditGuideActivityViewModel viewModel;

    private Button buttonContinue;
    private ConstraintLayout placeholder;
    private ConstraintLayout constraintLayoutImage;
    private ImageView imageViewTitle;
    private ImageView buttonDelete;
    private TextInputLayout textInputLayoutTitle;
    private TextInputLayout textInputLayoutLocation;
    private TextInputLayout textInputLayoutDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(this).get(EditGuideActivityViewModel.class);
        EditGuideActivity.Args args = EditGuideActivity.Args.fromBundle(EditGuideActivity.Args.class, getIntent().getExtras());
        Integer tourId = args.getTourId();
        if (tourId != null) {
            tour = tourRepository.getTourById(tourId);
        }
        setContentView(R.layout.activity_edit_guide);
        setupBackButton();
        setupInfoButton();
        setupTitleImage();
        setupInputs();
        setupContinueButton();
        setupViewPager();
    }

    private void setupBackButton() {
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupInfoButton() {
        ImageView button = findViewById(R.id.buttonInfo);
        button.setOnClickListener(v -> DummyHelper.dummyAction(this));
    }

    private void setupTitleImage() {
        placeholder = findViewById(R.id.imagePlaceholder);
        constraintLayoutImage = findViewById(R.id.constraintLayoutImage);
        imageViewTitle = findViewById(R.id.imageViewTitle);
        buttonDelete = findViewById(R.id.buttonDelete);
        if (tour != null && tour.getImage() != 0) {
            constraintLayoutImage.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);
            imageViewTitle.setImageResource(tour.getImage());
        } else {
            constraintLayoutImage.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
        }
        buttonDelete.setOnClickListener(v -> {
            constraintLayoutImage.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
        });
        View.OnClickListener imageClickListener = v -> CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.OFF)
                .setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAllowFlipping(false)
                .setActivityMenuIconColor(getColor(R.color.colorTextOrange))
                .setFixAspectRatio(true)
                .start(this);
        placeholder.setOnClickListener(imageClickListener);
        constraintLayoutImage.setOnClickListener(imageClickListener);
    }

    private void setupInputs() {
        setupTitleInput();
        setupLocationInput();
        setupDescriptionInput();
        setupTagsInput();
    }

    private void setupTitleInput() {
        textInputLayoutTitle = findViewById(R.id.textInputLayoutTitle);
        viewModel.setTitle(tour != null ? tour.getTitle() : null);
        textInputLayoutTitle.getEditText().setText(tour != null ? tour.getTitle() : "");
        TextInputHelper.editTextObservable(textInputLayoutTitle).subscribe((s) -> {
            viewModel.setTitle(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void setupLocationInput() {
        ProgressBar progressBar = findViewById(R.id.progressBarGeoCoding);
        textInputLayoutLocation = findViewById(R.id.textInputLayoutLocation);
        viewModel.setLocation("");
        textInputLayoutLocation.getEditText().setText(viewModel.getLocation() != null ? viewModel.getLocation() : "");
        TextInputHelper.editTextObservable(textInputLayoutLocation).subscribe((s) -> {
            viewModel.setLocation(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
        ImageView buttonLocation = findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(v -> permissionHelper.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                () -> viewModel.obtainLocation(),
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()));
        viewModel.subscribeLocationUpdates(this, location -> {
            if (location != null) {
                textInputLayoutLocation.getEditText().setText(location);
            } else {
                Toast.makeText(this, R.string.error_obtaining_location, Toast.LENGTH_LONG).show();
            }
        });
        viewModel.getGeoCodingState().observe(this, isActive -> {
            textInputLayoutLocation.setEnabled((!isActive));
            buttonLocation.setEnabled((!isActive));
            progressBar.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
            validateInputs();
        });
    }

    private void setupDescriptionInput() {
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        viewModel.setDescription(tour != null ? tour.getDescription() : null);
        textInputLayoutDescription.getEditText().setText(tour != null ? tour.getDescription() : "");
        TextInputHelper.editTextObservable(textInputLayoutDescription).subscribe((s) -> {
            viewModel.setDescription(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void validateInputs() {
        buttonContinue.setEnabled(viewModel.getTitle() != null && viewModel.getLocation() != null && !viewModel.isGeoCodingInProgress() && viewModel.getDescription() != null);
    }

    private void setupTagsInput() {
        ChipGroup chipGroup = findViewById(R.id.chipGroupTags);
        for (String tag : viewModel.getTags()) {
            chipGroup.addView(makeChip(chipGroup, tag));
        }
        TextInputLayout textInputLayoutTags = findViewById(R.id.textInputLayoutTags);
        ImageView buttonAdd = findViewById(R.id.buttonAddTag);
        buttonAdd.setOnClickListener(v -> {
            String tag = textInputLayoutTags.getEditText().getText().toString();
            if (!TextUtils.isEmpty(tag)) {
                if (viewModel.addTag(tag)) {
                    chipGroup.addView(makeChip(chipGroup, tag));
                    textInputLayoutTags.getEditText().setText("");
                }
            }
        });
    }

    private void setupContinueButton() {
        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> proceed());
    }

    private void setupViewPager() {

        ViewPager viewPager = findViewById(R.id.viewPager);
        HelpfulInformationPagerAdapter adapter = new HelpfulInformationPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setViewPagerPosition(position + 1, adapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setViewPagerPosition(viewPager.getCurrentItem() + 1, adapter.getCount());
    }

    private void setViewPagerPosition(int position, int count){
        TextView textView = findViewById(R.id.textViewHelpfulInformationPosition);
        textView.setText(String.format(Locale.getDefault(), "%02d / %02d", position, count));
    }

    private void proceed() {
        EditAudioActivity.Args args = new EditAudioActivity.Args(tour != null ? tour.getId() : null);
        NavigationHelper.startEditAudioActivity(this, args);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imageViewTitle.setImageURI(resultUri);
                placeholder.setVisibility(View.GONE);
                constraintLayoutImage.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Logger.log(EditGuideActivity.this, "Image cropping error", error);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Chip makeChip(ChipGroup parent, String tag) {
        LayoutInflater inflater = LayoutInflater.from(this);
        Chip chip = (Chip) inflater.inflate(R.layout.item_tour_tag, parent, false);
        chip.setText(tag);
        chip.setCloseIcon(getDrawable(R.drawable.ic_clear));
        chip.setCloseIconTint(ColorStateList.valueOf(getColor(R.color.colorTextOrange)));
        chip.setCloseIconSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            if (viewModel.removeTag(tag)) {
                parent.removeView(chip);
            }
        });
        return chip;
    }
}
