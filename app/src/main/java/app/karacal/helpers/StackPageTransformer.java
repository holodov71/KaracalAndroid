package app.karacal.helpers;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class StackPageTransformer implements ViewPager.PageTransformer {

    private static final float SCALE_FACTOR = 0.1f;
    private static final float Y_OFFSET_FACTOR = 0.01f;
    private static final float MIN_ALPHA = 0.9f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();
        if (position < -1f) {
            view.setAlpha(0f);
        } else if (position <= 0) {
            view.setAlpha(1f);
            view.setTranslationX(0f);
            view.setScaleX(1f);
            view.setScaleY(1f);
        } else if (position <= 3f) {
            view.setAlpha(Math.max(MIN_ALPHA, 1f - (1f - MIN_ALPHA) * position));
            view.setTranslationX(pageWidth * -position);
            float scaleFactor = 1f - SCALE_FACTOR * Math.abs(position);
            float yCompensation = -(pageHeight - pageHeight * scaleFactor) / 2f;
            view.setTranslationY(yCompensation - position * Y_OFFSET_FACTOR * pageHeight);
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
        } else {
            view.setAlpha(0f);
        }
    }
}
