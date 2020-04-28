package app.karacal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.AppCompatEditText;

import java.util.concurrent.TimeUnit;

import apps.in.android_logger.Logger;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ReferralCodeEditText extends AppCompatEditText {

    private static final int UNDERSCORE_HEIGHT_DP = 1;
    private static final int CURSOR_WIDTH_DP = 1;
    private static final int CURSOR_BLINK_PERIOD = 400;
    private static final String PLACEHOLDER = "A";

    public interface ReferralCodeChangeListener {
        void onCodeChanged(String code);
    }

    private static final int MAX_LENGTH = 6;
    private static final String CHECK_REGEX = ".*[^a-zA-Z0-9]+.*";
    private static final String REPLACE_REGEX = "[^a-zA-Z0-9]+";

    private String referralCode = "";
    private int cursorPosition;
    private ReferralCodeChangeListener listener;

    private final Paint primaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint secondaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF[] letters = new RectF[MAX_LENGTH];
    private final RectF[] underscores = new RectF[MAX_LENGTH];
    private final RectF[] cursors = new RectF[MAX_LENGTH + 1];
    private boolean isCursorVisible;
    private Disposable cursorVisibilityUpdater;

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String s = editable.toString();
            if (s.length() > MAX_LENGTH + PLACEHOLDER.length()) {
                String ns = s.substring(0, MAX_LENGTH + PLACEHOLDER.length());
                setText(ns);
                return;
            }
            if (s.matches(CHECK_REGEX)) {
                String ns = s.replaceAll(REPLACE_REGEX, "");
                setText(ns);
                return;
            }
            if (s.length() > PLACEHOLDER.length()){
                String newText = s.substring(PLACEHOLDER.length());
                if (referralCode.length() + newText.length() <= MAX_LENGTH){
                    int count = s.length() - PLACEHOLDER.length();
                    StringBuilder stringBuilder = new StringBuilder();
                    if (cursorPosition > 0){
                        stringBuilder.append(referralCode.substring(0, cursorPosition));
                    }
                    stringBuilder.append(newText);
                    if (cursorPosition < referralCode.length()){
                        stringBuilder.append(referralCode.substring(cursorPosition));
                    }
                    updateReferralCode(stringBuilder.toString());
                    cursorPosition += count;
                }
                setText(PLACEHOLDER);
            }
            if (s.length() < PLACEHOLDER.length()){
                int count = Math.min(cursorPosition, PLACEHOLDER.length() - s.length());
                StringBuilder stringBuilder = new StringBuilder();
                if (cursorPosition - count > 0){
                    stringBuilder.append(referralCode.substring(0, cursorPosition - count));
                }
                stringBuilder.append(referralCode.substring(cursorPosition));
                updateReferralCode(stringBuilder.toString());
                cursorPosition -= count;
                setText(PLACEHOLDER);
            }
        }
    };

    public ReferralCodeEditText(Context context) {
        super(context);
        init();
    }

    public ReferralCodeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReferralCodeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setCursorVisible(false);
        this.setInputType(this.getInputType() | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
        setText(PLACEHOLDER);
        addTextChangedListener(textWatcher);
        primaryPaint.setTypeface(getTypeface());
        primaryPaint.setTextAlign(Paint.Align.CENTER);
        primaryPaint.setColor(getTextColors().getDefaultColor());
        primaryPaint.setTextSize(getTextSize());
        secondaryPaint.setColor(getTextColors().getDefaultColor());
        secondaryPaint.setAlpha(128);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textSize = (int) getTextSize();
        setMeasuredDimension(10 * textSize, 2 * textSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        for (int i = 0; i < MAX_LENGTH; i++) {
            if (letters[i].contains(x, y)){
                cursorPosition = Math.min(referralCode.length(), x < letters[i].centerX() ? i : i + 1);
                break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() > 0 && getHeight() > 0) {
            for (int i = 0; i < MAX_LENGTH; i++) {
                if (i < referralCode.length()) {
                    String symbol = referralCode.substring(i, i + 1);
                    canvas.drawText(symbol, letters[i].centerX(), letters[i].bottom, primaryPaint);
                }
                canvas.drawRect(underscores[i], secondaryPaint);
            }
            if (isCursorVisible) {
                canvas.drawRect(cursors[cursorPosition], secondaryPaint);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        updateSizes(w, h);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        Editable editable = getText();
        if (editable != null) {
            setSelection(editable.toString().length());
        } else {
            super.onSelectionChanged(selStart, selEnd);
        }
    }


    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (cursorVisibilityUpdater != null) {
            cursorVisibilityUpdater.dispose();
            cursorVisibilityUpdater = null;
        }
        if (focused) {
            cursorVisibilityUpdater =  Completable.timer(CURSOR_BLINK_PERIOD, TimeUnit.MILLISECONDS)
                    .doOnComplete(() -> {
                        isCursorVisible = !isCursorVisible;
                        post(this::invalidate);
                    })
                    .repeat()
                    .subscribeOn(Schedulers.io()).subscribe(
                    () -> Logger.log(ReferralCodeEditText.this, "Cursor update completed"),
                    throwable -> {
                        Logger.log(ReferralCodeEditText.this, "Error updating cursor", throwable);
                        requestFocus();
                    });
        } else {
            isCursorVisible = false;
            postInvalidate();
        }
    }

    private void updateReferralCode(String code) {
        referralCode = code.toUpperCase();
        if (listener != null) {
            listener.onCodeChanged(referralCode);
        }
        invalidate();
    }

    public void setReferralCodeChangeListener(ReferralCodeChangeListener listener) {
        this.listener = listener;
        this.listener.onCodeChanged(referralCode);
    }

    public void updateSizes(float width, float height) {
        float textSize = getTextSize();
        float letterHeight = 1.5f * textSize;
        float underscoreHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, UNDERSCORE_HEIGHT_DP, getContext().getResources().getDisplayMetrics());
        float cursorWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CURSOR_WIDTH_DP, getContext().getResources().getDisplayMetrics());
        float cursorHalfWidth = cursorWidth / 2;
        float cursorTop = 0.25f * textSize;
        float cursorBottom = 1.1f * letterHeight;
        float x = width / 2 - 4.5f * textSize;

        for (int i = 0; i < MAX_LENGTH; i++) {
            letters[i] = new RectF(x, 0, x + textSize, letterHeight);
            underscores[i] = new RectF(x, height - underscoreHeight, x + textSize, height);
            x += (i == 2 ? 2 : 1.5) * textSize;
        }
        x = width / 2 - 4.75f * textSize;
        for (int i = 0; i < MAX_LENGTH + 1; i++) {
            cursors[i] = new RectF(x - cursorHalfWidth, cursorTop, x + cursorHalfWidth, cursorBottom);
            x += (i == 2 ? 2 : 1.5) * textSize;
        }
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
}
