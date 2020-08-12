package app.karacal.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.Serializable;

import app.karacal.R;
import app.karacal.models.CategoryViewMode;
import app.karacal.models.TourCategory;
import app.karacal.navigation.ActivityArgs;
import app.karacal.viewmodels.CategoryActivityViewModel;
import apps.in.android_logger.LogActivity;

import static app.karacal.fragments.CategoryStackFragment.ARG_DESIRED_TOUR_ID;

public class CategoryActivity extends LogActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final TourCategory category;
        private final String categoryName;
        private final CategoryViewMode categoryViewMode;
        private final Integer desiredTourId;

        public Args(TourCategory category, String categoryName, CategoryViewMode categoryViewMode, Integer tourId) {
            this.category = category;
            this.categoryName = categoryName;
            this.categoryViewMode = categoryViewMode;
            this.desiredTourId = tourId;
        }

        public Args(TourCategory category, String categoryName, CategoryViewMode categoryViewMode) {
            this(category, categoryName, categoryViewMode, null);
        }

        public TourCategory getCategory() {
            return category;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public CategoryViewMode getCategoryViewMode(){
            return categoryViewMode;
        }

        public Integer getDesiredTourId() {
            return desiredTourId;
        }
    }

    private CategoryActivityViewModel viewModel;
    private Integer desiredTourId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        desiredTourId = args.getDesiredTourId();
        TourCategory category = args.getCategory();
        CategoryViewMode categoryViewMode = args.getCategoryViewMode();
        viewModel = new ViewModelProvider(this, new CategoryActivityViewModel.CategoryActivityViewModelFactory(category, categoryViewMode)).get(CategoryActivityViewModel.class);
        setupBackButton();
        String categoryName = args.getCategoryName();
        setupTitle(categoryName);
        setupModeButtons();
    }

    private void setupBackButton() {
        ImageView imageView = findViewById(R.id.buttonBack);
        imageView.setOnClickListener(v -> onBackPressed());
    }

    private void setupModeButtons() {
        ImageView imageViewListMode = findViewById(R.id.buttonList);
        imageViewListMode.setOnClickListener(v -> viewModel.setViewMode(CategoryViewMode.LIST));
        ImageView imageViewStackMode = findViewById(R.id.buttonStack);
        imageViewStackMode.setOnClickListener(v -> viewModel.setViewMode(CategoryViewMode.STACK));
        viewModel.getViewModeLiveData().observe(this, categoryViewMode -> {
            imageViewListMode.setSelected(categoryViewMode == CategoryViewMode.LIST);
            imageViewStackMode.setSelected(categoryViewMode == CategoryViewMode.STACK);
            NavController navController = Navigation.findNavController(this, R.id.fragmentHostView);
            switch (categoryViewMode) {
                case LIST:
                    navController.navigate(R.id.categoryListFragment);
                    break;
                case STACK:
                    Bundle bundle = new Bundle();
                    bundle.putInt(ARG_DESIRED_TOUR_ID, desiredTourId);
                    navController.navigate(R.id.categoryStackFragment, bundle);
                    break;
                default:
                    return;
            }
        });
    }

    private void setupTitle(String categoryName) {
        TextView textView = findViewById(R.id.textViewCategoryTitle);
        textView.setText(categoryName);
    }

}
