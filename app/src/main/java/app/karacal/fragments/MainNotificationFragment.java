package app.karacal.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import app.karacal.R;
import app.karacal.activities.AudioActivity;
import app.karacal.adapters.NotificationListAdapter;
import app.karacal.navigation.NavigationHelper;

public class MainNotificationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_notification, container, false);
        setupRecyclerView(view);
        return view;
    }

    private void  setupRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        NotificationListAdapter adapter = new NotificationListAdapter(getContext(),
                tourId -> {
                    if (getActivity() != null && tourId != 0) {
                        AudioActivity.Args args = new AudioActivity.Args(tourId);
                        NavigationHelper.startAudioActivity(getActivity(), args);
                    }
                });
        recyclerView.setAdapter(adapter);
    }
}
