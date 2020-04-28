package app.karacal.popups;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BasePopup {

    private static final String POPUP_TAG = "POPUP_TAG";

    private final ViewGroup parent;

    public BasePopup(ViewGroup parent){
        this.parent = parent;
    }

    protected abstract View getView();

    public void show(){
        View view = prepareView();
        TransitionManager.beginDelayedTransition(parent);
        parent.addView(view);
    }

    private View prepareView() {
        View view = getView();
        view.setTag(POPUP_TAG);
        return view;
    }

    public void replace(){
        View view = prepareView();
        TransitionManager.beginDelayedTransition(parent);
        closeAllPopups(parent);
        parent.addView(view);
    }

    public void close(){
        View currentView = getView();
        View view = parent.findViewWithTag(POPUP_TAG);
        while (view != null ){
            if (view == currentView) {
                parent.removeView(view);
                return;
            }
            view = parent.findViewWithTag(POPUP_TAG);
        }
    }

    protected void showPopup(ViewGroup parent, View view){
        view.setTag(POPUP_TAG);
        TransitionManager.beginDelayedTransition(parent);
        closePopups(parent);
        parent.addView(view);
    }

    public static boolean closeAllPopups(ViewGroup parent){
        TransitionManager.beginDelayedTransition(parent);
        return closePopups(parent);
    }

    private static boolean closePopups(ViewGroup parent) {
        boolean isChanged = false;
        View oldView = parent.findViewWithTag(POPUP_TAG);
        while (oldView != null){
            parent.removeView(oldView);
            oldView = parent.findViewWithTag(POPUP_TAG);
            isChanged = true;
        }
        return isChanged;
    }

}
