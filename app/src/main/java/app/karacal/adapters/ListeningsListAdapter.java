package app.karacal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
import app.karacal.models.Tour;

public class ListeningsListAdapter extends RecyclerView.Adapter<ListeningsListAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView textViewTitle;
        private final TextView textViewNumberOfPaidGuides;
        private final TextView textViewNumberOfFreeListenings;
        private final TextView textViewEarnings;
        private final ImageView buttonInfo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewNumberOfPaidGuides = itemView.findViewById(R.id.textViewNumberOfPaidGuides);
            textViewNumberOfFreeListenings = itemView.findViewById(R.id.textViewNumberOfFreeListenings);
            textViewEarnings = itemView.findViewById(R.id.textViewEarnings);
            buttonInfo = itemView.findViewById(R.id.buttonInfo);
        }

        void bind(Tour tour){
            int numberOfPaidGuides = 1424;
            int numberOfFreeListenings = 26;
            float earning = 148.99f;
            textViewTitle.setText(tour.getTitle());
            textViewNumberOfPaidGuides.setText(decimalFormat.format(numberOfPaidGuides));
            textViewNumberOfFreeListenings.setText(decimalFormat.format(numberOfFreeListenings));
            textViewEarnings.setText(context.getString(R.string.price_format, earning, context.getString(R.string.euro)));
            buttonInfo.setOnClickListener(v -> DummyHelper.dummyAction(context));

        }
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final DecimalFormat decimalFormat;

    private ArrayList<Tour> tours = new ArrayList<>();

    public ListeningsListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        decimalFormat = new DecimalFormat();
        DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setGroupingSeparator(' ');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_listenings_list, parent, false);
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

    public void setTours(ArrayList<Tour> tours){
        this.tours = tours;
        notifyDataSetChanged();
    }
}
