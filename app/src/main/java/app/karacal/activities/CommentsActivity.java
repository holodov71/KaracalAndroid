package app.karacal.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import app.karacal.R;
import app.karacal.adapters.CommentsListAdapter;
import app.karacal.models.Comment;
import apps.in.android_logger.LogActivity;

public class CommentsActivity extends LogActivity {

    private CommentsListAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setupBackButton();
        setupInput();
        setupRecyclerView();
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

    private void setupInput(){
        EditText editText = findViewById(R.id.editTextComment);
        ImageView button = findViewById(R.id.buttonSendComment);
        button.setOnClickListener(v -> {
            adapter.addComment(new Comment("Me", new Date(), editText.getText().toString()));
            editText.setText("");
            recyclerView.scrollToPosition(0);
        });
        editText.addTextChangedListener(new TextWatcher() {
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
}
