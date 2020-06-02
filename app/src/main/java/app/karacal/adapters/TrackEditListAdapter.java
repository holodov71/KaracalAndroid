package app.karacal.adapters;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import app.karacal.R;
import app.karacal.helpers.FileHelper;
import app.karacal.models.Track;

public class TrackEditListAdapter extends RecyclerView.Adapter<TrackEditListAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class TrackEditTouchHelperCallback extends ItemTouchHelper.Callback {

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return false;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            boolean move = onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            if (viewHolder instanceof TrackEditListAdapter.ViewHolder){
                ((TrackEditListAdapter.ViewHolder) viewHolder).setPositionText(viewHolder.getAdapterPosition());
            }
            if (target instanceof TrackEditListAdapter.ViewHolder){
                ((TrackEditListAdapter.ViewHolder) target).setPositionText(target.getAdapterPosition());
            }
            return move;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView textViewPosition;
        private final TextView textViewTitle;
        private final TextView textViewDuration;
        private final ImageView buttonMore;

        private int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewPosition = itemView.findViewById(R.id.textViewPosition);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            buttonMore = itemView.findViewById(R.id.buttonMore);
        }

        public void bind(int position, Track track) {
            this.position = position;
            setPositionText(position);
            setTitleText(track);
            setDurationText(track);
            buttonMore.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(this.position);
                }
            });
        }

        public void setPositionText(int position) {
            textViewPosition.setText(String.format(Locale.getDefault(), "%02d.", position + 1));
        }

        private void setTitleText(Track track) {
            textViewTitle.setText(track.getTitle());
        }

        private void setDurationText(Track track) {
            int duration = track.getDuration();
            if (duration == 0) {
                try {
                    Uri mediaPath = Uri.parse("android.resource://" + context.getPackageName() + "/" + track.getResId());
                    duration = FileHelper.getAudioDuration(textViewDuration.getContext(), mediaPath);
                } catch (Exception e) {
                    textViewDuration.setVisibility(View.INVISIBLE);
                }

            }
            if (duration != 0) {
                int minutes = duration / 60;
                int seconds = duration % 60;
                textViewDuration.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                textViewDuration.setVisibility(View.VISIBLE);
            } else {
                textViewDuration.setVisibility(View.INVISIBLE);
            }

        }
    }

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final TrackEditTouchHelperCallback callback;
    private final ArrayList<Track> tracks = new ArrayList<>();
    private final MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();

    private ItemClickListener clickListener;

    public TrackEditListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        callback = new TrackEditTouchHelperCallback();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_track_edit_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position, tracks.get(position));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setTracks(ArrayList<Track> tracks) {
        this.tracks.clear();
        this.tracks.addAll(tracks);
        notifyDataSetChanged();
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public TrackEditTouchHelperCallback getCallback() {
        return callback;
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(tracks, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(tracks, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

}
