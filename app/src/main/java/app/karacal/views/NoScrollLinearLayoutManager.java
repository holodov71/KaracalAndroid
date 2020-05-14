package app.karacal.views;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class NoScrollLinearLayoutManager extends LinearLayoutManager {

    public NoScrollLinearLayoutManager(Context context){
        super(context);
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
