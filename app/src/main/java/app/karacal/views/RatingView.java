package app.karacal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import app.karacal.R;

public class RatingView extends View {

    public interface RatingChangeListener{
        void onRatingChanged(int rating);
    }

    private static final int MAX_RATING = 5;
    private Drawable starEmpty;
    private Drawable starFilled;
    private Rect[] stars;

    private int rating = 0;

    private RatingChangeListener listener;

    public RatingView(Context context) {
        super(context);
        init(context);
    }

    public RatingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RatingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        starEmpty = context.getResources().getDrawable(R.drawable.ic_star_empty, null);
        starFilled = context.getResources().getDrawable(R.drawable.ic_star_filled, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, width / 7);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            stars = new Rect[MAX_RATING];
            float x = 0;
            int size = h;
            for (int i = 0; i < MAX_RATING; i++) {
                stars[i] = new Rect((int)x, 0, (int) x + size, h);
                x += 1.5f * size;
            }
        } else {
            stars = null;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (stars != null){
            for (int i = 0; i < MAX_RATING; i++) {
                Drawable drawable = rating >= i + 1 ? starFilled : starEmpty;
                drawable.setBounds(stars[i]);
                drawable.draw(canvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (stars != null){
            int x = (int) event.getX();
            int y = (int) event.getY();
            for (int i = 0; i < MAX_RATING; i++) {
                if (stars[i].contains(x, y)){
                    setRating(i + 1);
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        if (listener != null){
            listener.onRatingChanged(this.rating);
        }
        invalidate();
    }

    public void setRatingChangeListener(RatingChangeListener listener) {
        this.listener = listener;
        listener.onRatingChanged(rating);
    }
}
