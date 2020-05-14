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

import app.karacal.R;
import app.karacal.helpers.DummyHelper;
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
            if (guide.getAvatarId() != -1) {
                imageViewAvatar.setImageResource(guide.getAvatarId());
            } else {
                imageViewAvatar.setImageResource(R.drawable.ic_person);
            }
            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onGuideClick(guide.getId());
                }
            });
        }
    }

    private final Context context;
    private final LayoutInflater inflater;
//    private final ArrayList<Guide> guides = new ArrayList<>(Arrays.asList(new Guide(1, "Micheal", R.mipmap.guide_avatar_example_01), new Guide(2, "Anita", R.mipmap.guide_avatar_example_02), new Guide(3,"Jackie", R.mipmap.guide_avatar_example_03), new Guide(4, "Alexander", R.mipmap.guide_avatar_example_04)));
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
        this.guides.addAll(guides);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return guides.size();
    }

    public void setClickListener(GuideClickListener listener) {
        this.listener = listener;
    }
}
