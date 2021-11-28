package com.telemedicine.matrika.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.google.android.material.snackbar.Snackbar;

import static com.telemedicine.matrika.util.extensions.AppExtensions.getString;

public class CustomSnackBar {

    public enum Duration {
        SHORT, LONG, INDEFINITE
    }

    public static Snackbar      snackbar;
    private View                anchor;
    private String              message;
    private String              action;
    private Duration            duration;
    private OnDismissListener   mOnDismissListener;

    public CustomSnackBar(View anchor, Object message, Duration duration) {
        init(anchor, message, null, duration);
    }

    public CustomSnackBar(View anchor, Object message, Object action, Duration duration) {
        init(anchor, message, action, duration);
    }

    public CustomSnackBar(View anchor, Object message) {
        init(anchor, message, null, Duration.SHORT);
    }

    public CustomSnackBar(View anchor, Object message, Object action) {
        init(anchor, message, action, Duration.SHORT);
    }

    public CustomSnackBar(Object message, Duration duration) {
        init(null, message, null, duration);
    }

    public CustomSnackBar(Object message, Object action, Duration duration) {
        init(null, message, action, duration);
    }

    public CustomSnackBar(Object message) {
        init(null, message, null, Duration.SHORT);
    }

    public CustomSnackBar(Object message, Object action) {
        init(null, message, action, Duration.SHORT);
    }

    public void init(View anchor, Object message, Object action, Duration duration){
        this.anchor = anchor == null ? AppController.getActivity().findViewById(android.R.id.content) : anchor;
        if(message != null) this.message = message instanceof String ? (String)message : getString((Integer)message);
        if(action != null) this.action = action instanceof String ? (String)action : getString((Integer)action);
        this.duration = duration == null ? Duration.SHORT : duration;
    }

    @SuppressLint("RestrictedApi")
    public void show(){
        switch (duration){
            case SHORT : snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT);
                break;
            case LONG : snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_LONG);
                break;
            case INDEFINITE : snackbar = Snackbar.make(anchor, message, Snackbar.LENGTH_INDEFINITE);
                break;
        }

        if(action != null){
            snackbar.setAction(action, v -> {
                if (mOnDismissListener != null) mOnDismissListener.onDismiss(snackbar);
                else snackbar.dismiss();
            }).setActionTextColor(Color.WHITE);
        }

        Snackbar.SnackbarLayout s_layout = (Snackbar.SnackbarLayout) snackbar.getView();

        TextView textView = s_layout.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        s_layout.setBackground(AppExtensions.getDrawable(R.drawable.shape_snackbar));

        snackbar.show();
    }

    public void dismiss(){
        if(snackbar != null && snackbar.isShown()) snackbar.dismiss();
    }

    public void setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }

    public interface OnDismissListener {
        void onDismiss(Snackbar snackbar);
    }
}
