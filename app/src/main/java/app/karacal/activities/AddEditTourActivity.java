package app.karacal.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.HelpfulInformationPagerAdapter;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.FileHelper;
import app.karacal.helpers.ImageHelper;
import app.karacal.helpers.TextInputHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.ItemHelpfulInfo;
import app.karacal.models.Tag;
import app.karacal.models.Tour;
import app.karacal.navigation.ActivityArgs;
import app.karacal.navigation.NavigationHelper;
import app.karacal.viewmodels.AddEditTourActivityViewModel;
import apps.in.android_logger.Logger;
import io.reactivex.disposables.CompositeDisposable;

public class AddEditTourActivity extends PermissionActivity {

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

    private CompositeDisposable disposable = new CompositeDisposable();

    private Tour tour;

    private AddEditTourActivityViewModel viewModel;

    private Button buttonContinue;
    private ConstraintLayout placeholder;
    private ConstraintLayout constraintLayoutImage;
    private ImageView imageViewTitle;
    private TextInputLayout textInputLayoutLatitude;
    private TextInputLayout textInputLayoutLongitude;
    private AutoCompleteTextView textInputTags;
    private ImageView buttonAddTag;
    private ChipGroup tagGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        viewModel = new ViewModelProvider(this).get(AddEditTourActivityViewModel.class);
        AddEditTourActivity.Args args = AddEditTourActivity.Args.fromBundle(AddEditTourActivity.Args.class, getIntent().getExtras());
        Integer tourId = args.getTourId();
        if (tourId != null) {
            tour = tourRepository.getTourById(tourId);
        }

        if (tour != null){
            viewModel.setTour(tour);
        }

        setContentView(R.layout.activity_edit_guide);
        setupBackButton();
        setupInfoButton();
        setupTitleImage();
        setupInputs();
        setupContinueButton();
        setupViewPager();

        observeViewModel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null){
            disposable.dispose();
        }
    }

    private void setupBackButton() {
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupInfoButton() {
        ImageView button = findViewById(R.id.buttonInfo);
        button.setVisibility(View.GONE);
        button.setOnClickListener(v -> DummyHelper.dummyAction(this));
    }

    private void setupTitleImage() {
        placeholder = findViewById(R.id.imagePlaceholder);
        constraintLayoutImage = findViewById(R.id.constraintLayoutImage);
        imageViewTitle = findViewById(R.id.imageViewTitle);
        ImageView buttonDelete = findViewById(R.id.buttonDelete);
        if (tour != null && tour.getImageUrl() != null) {
            ImageHelper.setImage(imageViewTitle, tour.getImageUrl(), tour.getImage(), false);
            constraintLayoutImage.setVisibility(View.VISIBLE);
            placeholder.setVisibility(View.GONE);
        } else {
            constraintLayoutImage.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
        }
        buttonDelete.setOnClickListener(v -> {
            constraintLayoutImage.setVisibility(View.GONE);
            placeholder.setVisibility(View.VISIBLE);
            viewModel.setImagePath(null);
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
        setupAddressInput();
        setupDescriptionInput();
        setupTagsInput();
    }

    @SuppressLint("CheckResult")
    private void setupTitleInput() {
        TextInputLayout textInputLayoutTitle = findViewById(R.id.textInputLayoutTitle);
        viewModel.setTitle(tour != null ? tour.getTitle() : null);
        textInputLayoutTitle.getEditText().setText(tour != null ? tour.getTitle() : "");
        TextInputHelper.editTextObservable(textInputLayoutTitle).subscribe((s) -> {
            viewModel.setTitle(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    @SuppressLint("CheckResult")
    private void setupLocationInput() {
        ProgressBar progressBar = findViewById(R.id.progressBarGeoCoding);
        textInputLayoutLatitude = findViewById(R.id.textInputLayoutLatitude);
        textInputLayoutLongitude = findViewById(R.id.textInputLayoutLongitude);

        if (tour != null){
            viewModel.setLocation(tour.getTourLocation());
        }

        TextInputHelper.editTextObservable(textInputLayoutLatitude).subscribe((s) -> {
            viewModel.setLatitude(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });

        TextInputHelper.editTextObservable(textInputLayoutLongitude).subscribe((s) -> {
            viewModel.setLongitude(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });


        ImageView buttonLocation = findViewById(R.id.buttonLocation);
        buttonLocation.setOnClickListener(v -> permissionHelper.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,
                () -> viewModel.obtainLocation(),
                () -> Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()));

        viewModel.getGeoCodingState().observe(this, isActive -> {
            textInputLayoutLongitude.setEnabled((!isActive));
            textInputLayoutLatitude.setEnabled((!isActive));
            buttonLocation.setEnabled((!isActive));
            progressBar.setVisibility(isActive ? View.VISIBLE : View.INVISIBLE);
            validateInputs();
        });
    }

    @SuppressLint("CheckResult")
    private void setupAddressInput() {
        TextInputLayout textInputLayoutAddress = findViewById(R.id.textInputLayoutAddress);
        viewModel.setAddress(tour != null ? tour.getAddress() : null);
        textInputLayoutAddress.getEditText().setText(tour != null ? tour.getAddress() : "");
        TextInputHelper.editTextObservable(textInputLayoutAddress).subscribe((s) -> {
            viewModel.setAddress(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    @SuppressLint("CheckResult")
    private void setupDescriptionInput() {
        TextInputLayout textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        viewModel.setDescription(tour != null ? tour.getDescription() : null);
        textInputLayoutDescription.getEditText().setText(tour != null ? tour.getDescription() : "");
        TextInputHelper.editTextObservable(textInputLayoutDescription).subscribe((s) -> {
            viewModel.setDescription(TextUtils.isEmpty(s) ? null : s);
            validateInputs();
        });
    }

    private void validateInputs() {
        buttonContinue.setEnabled(
                viewModel.getTitle() != null
                        && viewModel.getLatitude() != null
                        && viewModel.getLongitude() != null
                        && !viewModel.isGeoCodingInProgress()
                        && viewModel.getAddress() != null
                        && viewModel.getDescription() != null);
    }

    private void setupTagsInput() {
        tagGroup = findViewById(R.id.chipGroupTags);
        for (Tag tag : viewModel.getTags()) {
            tagGroup.addView(makeChip(tagGroup, tag));
        }

        textInputTags = findViewById(R.id.textInputTags);
        buttonAddTag = findViewById(R.id.buttonAddTag);

        viewModel.loadTags();
    }

    private void setTagsList(List<Tag> tags){
        List<String> tagsTitles = new ArrayList<>();

        for (Tag tag: tags){
            tagsTitles.add(tag.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, tagsTitles);

        textInputTags.setAdapter(adapter);

        buttonAddTag.setOnClickListener(v -> {
            String tag = textInputTags.getText().toString();
            if (!TextUtils.isEmpty(tag)) {
                if (tagsTitles.contains(tag)) {
                    Tag selectedTag = null;
                    for (Tag mTag: tags){
                        if (mTag.getName().equalsIgnoreCase(tag)){
                            selectedTag = mTag;
                            break;
                        }
                    }
                    if (viewModel.addTag(selectedTag) && selectedTag != null) {
                        tagGroup.addView(makeChip(tagGroup, selectedTag));
                        textInputTags.setText("");
                    }
                } else {
                    ToastHelper.showToast(this, getString(R.string.tag_undefined));
                }
            }
        });
    }

    private void setupContinueButton() {
        buttonContinue = findViewById(R.id.buttonContinue);
        buttonContinue.setOnClickListener(v -> viewModel.saveTour());
    }

    private void setupViewPager() {

        ViewPager viewPager = findViewById(R.id.viewPager);

        HelpfulInformationPagerAdapter adapter = new HelpfulInformationPagerAdapter(getSupportFragmentManager(), obtainHelpfulInfoItems());
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

    private List<ItemHelpfulInfo> obtainHelpfulInfoItems(){
        List<ItemHelpfulInfo> list = new ArrayList<>();
        String[] titlesArray = getResources().getStringArray(R.array.helpful_items_titles);
        String[] descriptionsArray = getResources().getStringArray(R.array.helpful_items_descriptions);

        for (int i = 0; i < titlesArray.length; i ++){
            list.add(new ItemHelpfulInfo(R.mipmap.image_helpful_information, titlesArray[i], descriptionsArray[i]));
        }

        return list;
    }

    private void setViewPagerPosition(int position, int count){
        TextView textView = findViewById(R.id.textViewHelpfulInformationPosition);
        textView.setText(String.format(Locale.getDefault(), "%02d / %02d", position, count));
    }

    private void observeViewModel(){
        viewModel.getTagsList().observe(this, this::setTagsList);

        viewModel.getErrorEvent().observe(this, errorMsg -> ToastHelper.showToast(this, errorMsg));

        viewModel.getTourSavedEvent().observe(this, tourId -> {
            EditAudioActivity.Args args = new EditAudioActivity.Args(tourId);
            NavigationHelper.startEditAudioActivity(this, args);
        });

        viewModel.getLocationCoordinates().observe(this, location -> {
            String latitude = "";
            String longitude = "";

            if (location != null){
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }

            textInputLayoutLatitude.getEditText().setText(latitude);
            textInputLayoutLongitude.getEditText().setText(longitude);
        });
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
                viewModel.setImagePath(FileHelper.getRealImagePathFromUri(this, resultUri));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Logger.log(AddEditTourActivity.this, "Image cropping error", error);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private Chip makeChip(ChipGroup parent, Tag tag) {
        LayoutInflater inflater = LayoutInflater.from(this);
        Chip chip = (Chip) inflater.inflate(R.layout.item_tour_tag, parent, false);
        chip.setText(tag.getName());
        chip.setCloseIcon(getDrawable(R.drawable.ic_clear));
        chip.setCloseIconTint(ColorStateList.valueOf(getColor(R.color.colorTextOrange)));
        chip.setCloseIconSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorMainBackground)));
        chip.setOnCloseIconClickListener(v -> {
            if (viewModel.removeTag(tag)) {
                parent.removeView(chip);
            }
        });
        return chip;
    }
}
