package app.karacal.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.ColorUtils;

import app.karacal.R;

public class StarsView extends AppCompatImageView {

    private static final int MAX_RATING = 5;
    private static final int MIN_RATING = 0;
    private Paint normalPaint;
    private Paint overlayPaint;

    private Bitmap bitmap;
    private Canvas bitmapCanvas;

    private double rating;

    public StarsView(Context context) {
        super(context);
        init(context);
    }

    public StarsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StarsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.TRANSPARENT);
        setImageDrawable(context.getDrawable(R.drawable.ic_stars));
        ColorStateList backgroundTintList = getForegroundTintList();
        int activeColor = backgroundTintList != null ? backgroundTintList.getDefaultColor() : Color.WHITE;
        int disabledColor = ColorUtils.blendARGB(activeColor, Color.BLACK, 0.5f);
        if (normalPaint == null) {
            normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            normalPaint.setColorFilter(new PorterDuffColorFilter(activeColor, PorterDuff.Mode.SRC_ATOP));
        }

        if (overlayPaint == null) {
            overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            overlayPaint.setColorFilter(new PorterDuffColorFilter(disabledColor, PorterDuff.Mode.SRC_ATOP));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            bitmapCanvas = new Canvas(bitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() > 0 && getHeight() > 0) {
            super.onDraw(bitmapCanvas);
            float mid = getWidth() * (float) rating / MAX_RATING;
            canvas.save();
            canvas.clipRect(0, 0, mid, getHeight());
            canvas.drawBitmap(bitmap, 0, 0, normalPaint);
            canvas.restore();
            canvas.save();
            canvas.clipRect(mid, 0, getWidth(), getHeight());
            canvas.drawBitmap(bitmap, 0, 0, overlayPaint);
            canvas.restore();
        }
    }

    public void setRating(double rating) {
        this.rating = Math.max(Math.min(rating, MAX_RATING), MIN_RATING);
        invalidate();
    }
}
