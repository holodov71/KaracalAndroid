package app.karacal.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.core.content.res.ResourcesCompat;

import com.igalata.bubblepicker.adapter.BubblePickerAdapter;
import com.igalata.bubblepicker.model.PickerItem;

import java.util.ArrayList;

import app.karacal.R;
import app.karacal.models.Interest;

public class InterestsBubblePickerAdapter implements BubblePickerAdapter {

    private static final int TEXT_SIZE = 12;    //SP
    private static final int STROKE_WIDTH = 1;      //DP
    private final ArrayList<Interest> interests;
    private final int colorDefault;
    private final int colorSelected;
    private final float textSize;
    private final Typeface typeface;
    private final float strokeWidth;

    public InterestsBubblePickerAdapter(Context context, ArrayList<Interest> interests) {
        this.interests = interests;
        this.colorDefault = Color.parseColor("#80FFFFFF");
        this.colorSelected = context.getColor(R.color.colorTextOrange);
        this.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE, context.getResources().getDisplayMetrics());
        this.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, STROKE_WIDTH, context.getResources().getDisplayMetrics());
        this.typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular);
    }

    @Override
    public int getTotalCount() {
        return interests.size();
    }

    @Override
    public PickerItem getItem(int i) {
        PickerItem item = new PickerItem();
        Interest interest = interests.get(i);
        item.setTitle(interest.getTitle());
        item.setColorDefault(colorDefault);
        item.setColorSelected(colorSelected);
        item.setTextSize(textSize);
        item.setTypeface(typeface);
        item.setColorDefault(colorDefault);
        item.setColorSelected(colorSelected);
        item.setStrokeWidth(strokeWidth);
        item.setCustomData(interest);
        item.setSelected(interest.isSelected());
        return item;
    }


}
