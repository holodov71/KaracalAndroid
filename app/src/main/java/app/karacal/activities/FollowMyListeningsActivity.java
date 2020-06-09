package app.karacal.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.ListeningsListAdapter;
import app.karacal.data.repository.TourRepository;
import app.karacal.helpers.DummyHelper;
import apps.in.android_logger.LogActivity;

public class FollowMyListeningsActivity extends LogActivity {

    @Inject
    TourRepository tourRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_follow_my_listenings);
        setupBackButton();
        setupWithDraw();
        setupRecyclerView();
    }

    private void setupBackButton(){
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupWithDraw(){
        TextView textViewFunds = findViewById(R.id.textViewFunds);
        float amount = 3.99f;
        textViewFunds.setText(getString(R.string.price_format, amount, getString(R.string.euro)));
        Button buttonWithdraw = findViewById(R.id.buttonWithdraw);
        buttonWithdraw.setOnClickListener(v -> DummyHelper.dummyAction(this));
        ImageView buttonWithdrawEdit = findViewById(R.id.buttonWithdrawEdit);
        buttonWithdrawEdit.setOnClickListener(v -> DummyHelper.dummyAction(this));
    }

    private void setupRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        ListeningsListAdapter adapter = new ListeningsListAdapter(this);
        recyclerView.setAdapter(adapter);
        observeTours(adapter);
    }

    private void observeTours(ListeningsListAdapter adapter){
        tourRepository.originalToursLiveData.observe(this, tours -> {
            if (!tours.isEmpty()) {
                adapter.setTours(tours);
            }
        });
    }


}
