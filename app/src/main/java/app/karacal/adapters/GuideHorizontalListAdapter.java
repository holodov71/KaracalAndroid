package app.karacal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
import app.karacal.helpers.ImageHelper;
import app.karacal.models.Guide;

public class GuideHorizontalListAdapter extends RecyclerView.Adapter<GuideHorizontalListAdapter.ViewHolder> {

    public interface GuideClickListener {
        void onGuideClick(int guideId);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView textViewName;
        private final ImageView imageViewAvatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewName = itemView.findViewById(R.id.textViewGuideName);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
        }

        void bind(Guide guide) {
            textViewName.setText(guide.getName());
            ImageHelper.setImage(imageViewAvatar, guide.getAvatarUrl(), R.drawable.ic_person, true);
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onGuideClick(guide.getId());
                }
            });
        }
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final List<Guide> guides = new ArrayList<>();
    private GuideClickListener listener;

    public GuideHorizontalListAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_guide_horizontal_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(guides.get(position));
    }

    public void setGuidesList(List<Guide> guides){
        this.guides.clear();
        this.guides.addAll(getRandomGuides(guides));
        notifyDataSetChanged();
    }

    // Function select an element base on index and return
    // an element
    public List<Guide> getRandomGuides(List<Guide> source) {
        List<Guide> list = new ArrayList<>(source);

        Random rand = new Random();

        // create a temporary list for storing
        // selected element
        List<Guide> newList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {

            // take a raundom index between 0 to size
            // of given List
            int randomIndex = rand.nextInt(list.size());

            // add element in temporary list
            newList.add(list.get(randomIndex));

            // Remove selected element from orginal list
            list.remove(randomIndex);
        }
        return newList;
    }

    @Override
    public int getItemCount() {
        return guides.size();
    }

    public void setClickListener(GuideClickListener listener) {
        this.listener = listener;
    }
}
