package app.karacal.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import app.karacal.R;
import app.karacal.models.SearchTagCategory;
import app.karacal.models.Tag;

public class SearchTagsAdapter extends RecyclerView.Adapter<SearchTagsAdapter.ViewHolder> {

    public interface TagClickListener{
        void onTagClicked(Tag tag);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewTitle;
        private final ChipGroup chipGroup;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.textViewTitle);
            this.chipGroup = itemView.findViewById(R.id.chipGroup);
        }

        void bind(SearchTagCategory category) {
            textViewTitle.setText(category.getTitle());
            chipGroup.removeAllViews();
            for (Tag tag : category.getTags()) {
                Chip chip = (Chip) inflater.inflate(R.layout.item_search_tag, chipGroup, false);
                chip.setText(tag.getName());
                chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorMainBackground)));
                chip.setOnClickListener(v -> {
                    if (listener != null){
                        listener.onTagClicked(tag);
                    }
                });
                chipGroup.addView(chip);
            }
        }
    }

    private final Context context;
    private LayoutInflater inflater;
    private TagClickListener listener;

    private ArrayList<SearchTagCategory> categories = new ArrayList<>();

    public void setTags(List<Tag> tags) {
        categories.clear();
        categories.add(new SearchTagCategory("", tags));
        notifyDataSetChanged();
    }

    public SearchTagsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_search_tag_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setTagClickListener(TagClickListener listener){
        this.listener = listener;
    }
}
