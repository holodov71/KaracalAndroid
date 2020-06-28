package app.karacal.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.Date;

import app.karacal.App;
import app.karacal.R;
import app.karacal.adapters.CommentsListAdapter;
import app.karacal.helpers.KeyboardHelper;
import app.karacal.helpers.ToastHelper;
import app.karacal.models.Comment;
import app.karacal.navigation.ActivityArgs;
import app.karacal.viewmodels.CommentsActivityViewModel;
import app.karacal.viewmodels.MainActivityViewModel;
import apps.in.android_logger.LogActivity;

public class CommentsActivity extends LogActivity {

    public static class Args extends ActivityArgs implements Serializable {

        private final int tourId;

        public Args(int tourId) {
            this.tourId = tourId;
        }

        public int getTourId() {
            return tourId;
        }

    }

    private CommentsActivityViewModel viewModel;

    private CommentsListAdapter adapter;
    private RecyclerView recyclerView;
    private EditText inputComment;
    private View progressLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
        setContentView(R.layout.activity_comments);

        Args args = ActivityArgs.fromBundle(Args.class, getIntent().getExtras());
        int tourId = args.getTourId();
        viewModel = new ViewModelProvider(this, new CommentsActivityViewModel.CommentsActivityViewModelFactory(tourId)).get(CommentsActivityViewModel.class);

        setupBackButton();
        setupInput();
        setupRecyclerView();
        setupLoading();

        viewModel.loadComments();
        showLoading();
        observeViewModel();
    }

    private void setupBackButton(){
        ImageView button = findViewById(R.id.buttonBack);
        button.setOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CommentsListAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void setupLoading() {
        progressLoading = findViewById(R.id.progressLoading);
    }

    private void setupInput(){
        inputComment = findViewById(R.id.editTextComment);
        ImageView button = findViewById(R.id.buttonSendComment);
        button.setOnClickListener(v -> {
            KeyboardHelper.hideKeyboard(this, v);
            viewModel.createNewComment(inputComment.getText().toString());
            showLoading();
        });
        inputComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                button.setVisibility(TextUtils.isEmpty(s.toString()) ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getCommentsLiveData().observe(this, comments -> {
            if (comments != null){
                inputComment.setText("");
                hideLoading();
                adapter.setComments(comments);
            }
        });

        viewModel.getError().observe(this, errorMsg -> {
            if (errorMsg != null){
                hideLoading();
                ToastHelper.showToast(this, errorMsg);
            }
        });

        viewModel.getNewComment().observe(this, comment -> {
            if (comment != null){
                hideLoading();
                inputComment.setText("");
                adapter.addComment(comment);
                recyclerView.scrollToPosition(0);
            }
        });

    }

    private void showLoading(){
        progressLoading.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        progressLoading.setVisibility(View.GONE);
    }
}
