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
import app.karacal.models.Tour;

public class TourHorizontalListAdapter extends RecyclerView.Adapter<TourHorizontalListAdapter.ViewHolder> {

    public interface TourClickListener{
        void onTourClick(int id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageViewTitle;
        private final TextView textViewPrice;
        private final TextView textViewTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTitle = itemView.findViewById(R.id.imageViewTitle);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
        }

        public void bind(Tour tour){
            setupTitleImage(tour);
            setupTitleText(tour);
            setupPrice(tour);
        }

        private void setupTitleImage(Tour tour){
            Glide.with(imageViewTitle.getContext()).load(tour.getImage()).fitCenter().into(imageViewTitle);
            imageViewTitle.setOnClickListener(v -> {
                if (clickListener != null){
                    clickListener.onTourClick(tour.getId());
                }
            });
        }

        private void setupTitleText(Tour tour){
            textViewTitle.setText(tour.getTitle());
        }

        private void setupPrice(Tour tour) {
            Double price = tour.getPrice();
            if (price != null){
                textViewPrice.setVisibility(View.VISIBLE);
                textViewPrice.setText(context.getString(R.string.price_format, price, context.getString(R.string.euro)));
            } else {
                textViewPrice.setVisibility(View.GONE);
            }
        }
    }

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final ArrayList<Tour> tours = new ArrayList<>();
    private TourClickListener clickListener;

    public TourHorizontalListAdapter(Context context) {
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
        View view = layoutInflater.inflate(R.layout.item_tour_horizontal_list, parent, false);
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
