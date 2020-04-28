package app.karacal.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.io.Serializable;

import app.karacal.R;
import app.karacal.navigation.ActivityArgs;
import app.karacal.viewmodels.CategoryActivityViewModel;
import apps.in.android_logger.LogActivity;

public class CategoryActivity extends LogActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final int categoryId;
        private final String categoryName;

        public Args(int categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }

    private CategoryActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int categoryId = args.getCategoryId();
        viewModel = new ViewModelProvider(this, new CategoryActivityViewModel.CategoryActivityViewModelFactory(categoryId)).get(CategoryActivityViewModel.class);
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
        imageViewListMode.setOnClickListener(v -> viewModel.setViewMode(CategoryActivityViewModel.CategoryViewMode.LIST));
        ImageView imageViewStackMode = findViewById(R.id.buttonStack);
        imageViewStackMode.setOnClickListener(v -> viewModel.setViewMode(CategoryActivityViewModel.CategoryViewMode.STACK));
        viewModel.getViewModeLiveData().observe(this, categoryViewMode -> {
            imageViewListMode.setSelected(categoryViewMode == CategoryActivityViewModel.CategoryViewMode.LIST);
            imageViewStackMode.setSelected(categoryViewMode == CategoryActivityViewModel.CategoryViewMode.STACK);
            int destination;
            switch (categoryViewMode) {
                case LIST:
                    destination = R.id.categoryListFragment;
                    break;
                case STACK:
                    destination = R.id.categoryStackFragment;
                    break;
                default:
                    return;
            }
            Navigation.findNavController(this, R.id.fragmentHostView).navigate(destination);
        });
    }

    private void setupTitle(String categoryName) {
        TextView textView = findViewById(R.id.textViewCategoryTitle);
        textView.setText(categoryName);
    }

}
