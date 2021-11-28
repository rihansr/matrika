package com.telemedicine.matrika.wiget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatEditText;
import com.telemedicine.matrika.util.enums.DrawablePosition;

@SuppressLint("ClickableViewAccessibility")
public class ClickableEditText extends AppCompatEditText {

    private OnDrawableClickListener onDrawableClickListener;
    private DrawablePosition        position;

    public ClickableEditText(Context context) {
        super(context);
    }

    public ClickableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public interface OnDrawableClickListener {
        void onDropArrowClick();
    }

    public void setOnDrawableClickListener(DrawablePosition position, OnDrawableClickListener onDrawableClickListener) {
        this.position = position;
        this.onDrawableClickListener = onDrawableClickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if(position != null){
                Drawable drawable = getCompoundDrawables()[position.getAction()];
                if (drawable != null) {
                    /**
                     * The x-axis coordinates of this click event,
                     * if > current control width - control right spacing - drawable actual display size
                     **/
                    if (event.getX() >= (getWidth() - getPaddingRight() - drawable.getIntrinsicWidth())) {
                        /**
                         * Set up to click the EditText icon on the right to lose focus.
                         * Prevent clicking EditText icon on the right side of EditText to get focus and pop-up the soft keyboard
                         **/
                        setFocusableInTouchMode(false);
                        setFocusable(false);
                        if (onDrawableClickListener != null) onDrawableClickListener.onDropArrowClick();
                    }
                    else {
                        setFocusableInTouchMode(true);
                        setFocusable(true);
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }
}

