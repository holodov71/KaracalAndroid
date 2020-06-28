package app.karacal.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.karacal.R;
import app.karacal.models.Comment;

public class CommentsListAdapter extends RecyclerView.Adapter<CommentsListAdapter.ViewHolder> {

    private static final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
    private static final int PADDING_VALUE = 16;

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        private final TextView textViewAuthor;
        private final TextView textViewDate;
        private final TextView textViewComment;
        private final FrameLayout layoutComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            layoutComment = itemView.findViewById(R.id.layoutComment);
        }

        void bind(Comment comment, boolean isOdd) {
            textViewAuthor.setText(comment.getAuthor());
            textViewDate.setText(dateFormat.format(comment.getDate()));
            textViewComment.setText(comment.getComment());
            textViewComment.setTextColor(isOdd ? oddTextColor : evenTextColor);
            layoutComment.setBackground(isOdd ? oddBackground : evenBackground);
            layoutComment.setBackgroundTintList(ContextCompat.getColorStateList(context, isOdd ? R.color.colorTextWhite : R.color.colorPrimaryDark));
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            layoutParams.setMarginStart(isOdd ? padding : 3 * padding);
            layoutParams.setMarginEnd(isOdd ? 3 * padding : padding);
            itemView.setLayoutParams(layoutParams);
        }
    }

    private final Context context;
    private final LayoutInflater inflater;

    private final ArrayList<Comment> comments = new ArrayList<>(
//            Arrays.asList(
//            new Comment("Angelika Red", new Date(2019, 11, 14), "Learn about the past of the bohemian hill, imbued with its romance and admire the snow-white temple."),
//            new Comment("Alexander McQueen", new Date(2019, 11, 10), "Learn about the past of the bohemian hill, imbued with its romance."),
//            new Comment("Angelika White", new Date(2019, 11, 8), "Learn about the past of the bohemian hill, imbued with its romance and admire the snow-white temple."),
//            new Comment("Angelika Red", new Date(2019, 11, 6), "Learn about the past of the bohemian hill, imbued with its romance and admire the snow-white temple."),
//            new Comment("Alexander McQueen", new Date(2019, 11, 4), "Learn about the past of the bohemian hill, imbued with its romance."),
//            new Comment("Angelika White", new Date(2019, 11, 1), "Learn about the past of the bohemian hill, imbued with its romance and admire the snow-white temple.")
//    )
    );

    private final NinePatchDrawable oddBackground;
    private final NinePatchDrawable evenBackground;
    private final int oddTextColor;
    private final int evenTextColor;
    private final int padding;

    public CommentsListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        oddBackground = loadNinePathDrawable(R.drawable.cloud_left_vector);
        evenBackground = loadNinePathDrawable(R.drawable.cloud_right_vector);
        oddTextColor = context.getColor(R.color.colorPrimaryDark);
        evenTextColor = context.getColor(R.color.colorTextWhite);
        padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING_VALUE, context.getResources().getDisplayMetrics());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(comments.get(position), position % 2 == 0);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        this.comments.add(0, comment);
        notifyDataSetChanged();
    }

    private NinePatchDrawable loadNinePathDrawable(int resId) {
        NinePatchDrawable ninepatch = null;
        Bitmap image = BitmapFactory.decodeResource(context.getResources(), resId);
        if (image.getNinePatchChunk() != null) {
            byte[] chunk = image.getNinePatchChunk();
            Rect paddingRectangle = new Rect(0, 0, 0, 0);
            ninepatch = new NinePatchDrawable(context.getResources(), image, chunk, paddingRectangle, null);
        }
        return ninepatch;
    }
}
