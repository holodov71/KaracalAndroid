package app.karacal.models;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class TourComparator extends DiffUtil.ItemCallback<Tour> {
    @Override
    public boolean areItemsTheSame(@NonNull Tour oldItem,
                                   @NonNull Tour newItem) {
        // Id is unique.
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Tour oldItem,
                                      @NonNull Tour newItem) {
        return oldItem.getId() == newItem.getId();
    }
}
