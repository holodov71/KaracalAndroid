package app.karacal.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TextInputHelper {

    public static Observable<String> editTextObservable(EditText editText) {
        Observable<String> observable = Observable.create(emitter -> editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!emitter.isDisposed()) {
                    emitter.onNext(editable.toString());
                }
            }
        }));
        return observable.debounce(300, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> editTextObservable(TextInputLayout textInputLayout) {
       return editTextObservable(textInputLayout.getEditText());
    }

}
