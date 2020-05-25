package app.karacal.adapters;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import app.karacal.R;
import app.karacal.models.Player;
import app.karacal.models.Track;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView textViewPosition;
        private final TextView textViewTitle;
        private final TextView textViewDuration;
        private final ProgressBar progressBar;

        private int position;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewPosition = itemView.findViewById(R.id.textViewPosition);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(int position, Track track) {
            this.position = position;
            setPositionText(position);
            setTitleText(track);
            setDurationText(track);
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onItemClick(this.position);
                }
            });
        }

        private void setPositionText(int position) {
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
                    metaRetriever.setDataSource(context, mediaPath);
                    String metadata = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    duration = (int) (Long.parseLong(metadata) / 1000);
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

        private void setPositionProgress(Player.PositionInfo positionInfo){
            if (positionInfo != null){
                progressBar.setMax(positionInfo.getDuration());
                progressBar.setProgress(positionInfo.getCurrentPosition());
            } else {
                progressBar.setMax(0);
                progressBar.setProgress(0);
            }
        }
    }

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final ArrayList<Track> tracks = new ArrayList<>();
    private final MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
    private HashSet<ViewHolder> activeViewHolders = new HashSet<>();

    private ItemClickListener clickListener;

    public TrackListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_track_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        addViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position, tracks.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        removeViewHolder(holder);
        super.onViewRecycled(holder);
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

    public void updateItem(int currentPosition, int trackDuration) {
        if (tracks.size()>currentPosition){
            tracks.get(currentPosition).setDuration(trackDuration);
            notifyItemChanged(currentPosition);
        }
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private synchronized void addViewHolder(ViewHolder viewHolder) {
        activeViewHolders.add(viewHolder);
    }

    private synchronized void removeViewHolder(ViewHolder viewHolder) {
        activeViewHolders.remove(viewHolder);
    }

    public synchronized void updateProgress(Integer currentPosition, Player.PositionInfo positionInfo) {
        for (ViewHolder viewHolder : activeViewHolders) {
            viewHolder.setPositionProgress((currentPosition != null && currentPosition == viewHolder.position) ? positionInfo : null);
        }
    }
}
