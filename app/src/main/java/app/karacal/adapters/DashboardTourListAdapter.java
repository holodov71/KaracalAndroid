package app.karacal.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.karacal.R;
import app.karacal.activities.AddEditTourActivity;
import app.karacal.helpers.ImageHelper;
import app.karacal.models.Tour;
import app.karacal.navigation.NavigationHelper;

public class DashboardTourListAdapter extends RecyclerView.Adapter<DashboardTourListAdapter.ViewHolder> {

    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);

    class ViewHolder extends RecyclerView.ViewHolder{

        private final ImageView imageViewTitle;
        private final TextView textViewTitle;
        private final TextView textViewDate;
        private final ImageView buttonEdit;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTitle = itemView.findViewById(R.id.imageViewTitle);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
        }

        void bind(Tour tour){
            ImageHelper.setImage(imageViewTitle, tour.getImageUrl(), tour.getImage(), false);
            textViewTitle.setText(tour.getTitle());
            Date date = tour.getCreatedAt() != null ? tour.getCreatedAt() : new Date();
            textViewDate.setText(context.getString(R.string.dashboard_tour_created_format, dateFormat.format(date)));
            buttonEdit.setOnClickListener(v -> NavigationHelper.startEditGuideActivity(context, new AddEditTourActivity.Args(tour.getId())));
        }
    }

    private final Activity context;
    private final LayoutInflater inflater;

    private List<Tour> tours = new ArrayList<>();

    public DashboardTourListAdapter(Activity context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setTours(List<Tour> tours){
        this.tours = tours;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_tour_dashboard, parent, false);
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
