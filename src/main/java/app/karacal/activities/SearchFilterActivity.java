package app.karacal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.lifecycle.ViewModelProvider;

import app.karacal.R;
import app.karacal.models.SearchFilter;
import app.karacal.navigation.ActivityArgs;
import app.karacal.viewmodels.SearchFilterActivityViewModel;
import apps.in.android_logger.LogActivity;

public class SearchFilterActivity extends LogActivity {

    public static class Args extends ActivityArgs{

        private final SearchFilter searchFilter;

        public Args(SearchFilter searchFilter) {
            this.searchFilter = searchFilter;
        }

        public SearchFilter getSearchFilter() {
            return searchFilter;
        }
    }

    public static final int REQUEST_CODE = 2201;

    private SearchFilterActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SearchFilterActivity.Args args = ActivityArgs.fromBundle(SearchFilterActivity.Args.class, getIntent().getExtras());
        SearchFilter searchFilter = args.getSearchFilter();
        viewModel = new ViewModelProvider(this, new SearchFilterActivityViewModel.SearchFilterActivityViewModelFactory(searchFilter)).get(SearchFilterActivityViewModel.class);
        setContentView(R.layout.activity_search_filter);
        setupBackButton();
        setupApplyButton();
        setupSwitches();
    }

    private void setupBackButton() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupApplyButton() {
        ImageView buttonApply = findViewById(R.id.buttonApply);
        buttonApply.setOnClickListener(v -> apply());
    }

    private void setupSwitches() {
        SearchFilter searchFilter = viewModel.getSearchFilter();
        Switch switchFree = findViewById(R.id.switchFree);
        switchFree.setChecked(searchFilter.isFree());
        switchFree.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setFree(isChecked));
        Switch switchPaid = findViewById(R.id.switchPaid);
        switchPaid.setChecked(searchFilter.isPaid());
        switchPaid.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setPaid(isChecked));
        Switch switchOfficial = findViewById(R.id.switchOfficial);
        switchOfficial.setChecked(searchFilter.isOfficial());
        switchOfficial.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setOfficial(isChecked));
        Switch switchNonOfficial = findViewById(R.id.switchNonOfficial);
        switchNonOfficial.setChecked(searchFilter.isNonOfficial());
        switchNonOfficial.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setNonOfficial(isChecked));
        Switch switchExpert = findViewById(R.id.switchExpert);
        switchExpert.setChecked(searchFilter.isExpert());
        switchExpert.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setExpert(isChecked));
        Switch switchAverage = findViewById(R.id.switchAverage);
        switchAverage.setChecked(searchFilter.isAverage());
        switchAverage.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setAverage(isChecked));
        Switch switchDiscovery = findViewById(R.id.switchDiscovery);
        switchDiscovery.setChecked(searchFilter.isDiscovery());
        switchDiscovery.setOnCheckedChangeListener((buttonView, isChecked) -> searchFilter.setDiscovery(isChecked));
    }

    private void apply() {
        Intent intent = new Intent();
        SearchFilterActivity.Args args = new SearchFilterActivity.Args(viewModel.getSearchFilter());
        intent.putExtras(args.toBundle());
        setResult(RESULT_OK, intent);
        finish();
    }
}
