package app.karacal.popups;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import app.karacal.R;
import app.karacal.helpers.EmailHelper;

public class ReportProblemPopup extends BasePopup {

    public interface ReportProblemPopupCallbacks{
        void onButtonTechnicalProblemClick(BasePopup popup);
        void onButtonMistakeClick(BasePopup popup);
        void onButtonErrorClick(BasePopup popup);
        void onButtonSomethingElseClick(BasePopup popup);
        void onButtonCancelClick(BasePopup popup);
    }

    public static class ReportProblemPopupDefaultCallbacks implements ReportProblemPopupCallbacks{

        private Activity activity;

        public ReportProblemPopupDefaultCallbacks(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onButtonTechnicalProblemClick(BasePopup popup) {
            EmailHelper.reportProblem(activity, activity.getString(R.string.a_technical_problem));
        }

        @Override
        public void onButtonMistakeClick(BasePopup popup) {
            EmailHelper.reportProblem(activity, activity.getString(R.string.a_mistake_of_course));
        }

        @Override
        public void onButtonErrorClick(BasePopup popup) {
            EmailHelper.reportProblem(activity, activity.getString(R.string.an_error_in_what_the_guide_says));
        }

        @Override
        public void onButtonSomethingElseClick(BasePopup popup) {
            EmailHelper.reportProblem(activity, activity.getString(R.string.something_else));
        }

        @Override
        public void onButtonCancelClick(BasePopup popup) {
            activity.onBackPressed();
        }
    }

    private final ReportProblemPopupCallbacks callbacks;

    private final View view;

    public ReportProblemPopup(ViewGroup parent, ReportProblemPopupCallbacks callbacks) {
        super(parent);
        this.callbacks = callbacks;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.popup_report_a_problem, parent, false);
        setup(view);
    }

    private void setup(View view){
        LinearLayout buttonTechnicalProblem = view.findViewById(R.id.buttonTechnicalProblem);
        buttonTechnicalProblem.setOnClickListener(v -> callbacks.onButtonTechnicalProblemClick(this));
        LinearLayout buttonMistake = view.findViewById(R.id.buttonMistake);
        buttonMistake.setOnClickListener(v -> callbacks.onButtonMistakeClick(this));
        LinearLayout buttonError = view.findViewById(R.id.buttonError);
        buttonError.setOnClickListener(v -> callbacks.onButtonErrorClick(this));
        LinearLayout buttonSomethingElse = view.findViewById(R.id.buttonSomethingElse);
        buttonSomethingElse.setOnClickListener(v -> callbacks.onButtonSomethingElseClick(this));
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> callbacks.onButtonCancelClick(this));
    }

    @Override
    public View getView() {
        return view;
    }
}
