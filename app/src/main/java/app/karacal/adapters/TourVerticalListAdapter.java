package app.karacal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.karacal.R;
import app.karacal.helpers.ImageHelper;
import app.karacal.models.Tour;
import app.karacal.views.StarsView;

public class TourVerticalListAdapter extends RecyclerView.Adapter<TourVerticalListAdapter.ViewHolder> {

    public interface TourClickListener{
        void onTourClick(int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final View itemView;
        private final ImageView imageViewTitle;
        private final TextView textViewPrice;
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final TextView textViewDuration;
        private final StarsView starsView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageViewTitle = itemView.findViewById(R.id.imageViewTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDuration = itemView.findViewById(R.id.textViewDuration);
            starsView = itemView.findViewById(R.id.starsView);
        }

        public void bind(Tour tour){
            itemView.setOnClickListener(v -> {
                if (clickListener != null){
                    clickListener.onTourClick(tour.getId());
                }
            });
            setupTitleImage(tour);
            setupTitleText(tour);
            setupDescriptionText(tour);
            setupPrice(tour);
            setupDurationText(tour);
            setupRating(tour);
        }

        private void setupTitleImage(Tour tour){
            ImageHelper.setImage(imageViewTitle, tour.getImageUrl(), tour.getImage(), false);
//            Glide.with(imageViewTitle.getContext()).load(tour.getImage()).fitCenter().into(imageViewTitle);
        }

        private void setupTitleText(Tour tour){
            textViewTitle.setText(tour.getTitle());
        }

        private void setupDescriptionText(Tour tour){
            textViewDescription.setText(tour.getDescription());
        }

        private void setupDurationText(Tour tour){
//            int hours = tour.getDuration() / 60;
//            int minutes = tour.getDuration() % 60;
//            String duration = textViewDuration.getContext().getString(R.string.duration_format, hours, minutes);
            textViewDuration.setText(tour.getShortFormattedTourDuration(textViewDuration.getContext()));
        }

        private void setupPrice(Tour tour) {
            if (tour.getPrice() != 0){
                textViewPrice.setVisibility(View.VISIBLE);
                textViewPrice.setText(context.getString(R.string.price_format, tour.getDoublePrice(), context.getString(R.string.euro)));
            } else {
                textViewPrice.setVisibility(View.GONE);
            }
        }

        private void setupRating(Tour tour){
            starsView.setRating(tour.getRating());
        }

        public View getItemView() {
            return itemView;
        }
    }

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final ArrayList<Tour> tours = new ArrayList<>();
    private TourClickListener clickListener;

    public TourVerticalListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setClickListener(TourClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setTours(List<Tour> tours){
        this.tours.clear();
        this.tours.addAll(tours);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_tour_vertical_list, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tours.get(position));
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }
}
