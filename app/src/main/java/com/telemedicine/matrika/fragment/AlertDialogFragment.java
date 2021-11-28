package com.telemedicine.matrika.fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.extensions.AppExtensions;

public class AlertDialogFragment extends DialogFragment {

    private static final String TAG = AlertDialogFragment.class.getSimpleName();
    private AppCompatTextView   title;
    private AppCompatTextView   message;
    private AppCompatButton     leftBtn;
    private AppCompatButton     rightBtn;
    private View                divider;
    private OnDialogListener    mOnDialogListener;

    public static AlertDialogFragment show(Object title, Object message, Object leftButton, Object rightButton){
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(getArguments(title, message, leftButton, rightButton));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static AlertDialogFragment show(Object title, Object message, Object rightButton){
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(getArguments(title, message, null, rightButton));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static AlertDialogFragment show(Object title, Object message){
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(getArguments(title, message, null, null));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    private static Bundle getArguments(Object title, Object message, Object leftButton, Object rightButton){
        Bundle args = new Bundle();
        if(title != null) args.putString("title", title instanceof String ? (String)title : AppExtensions.getString((Integer)title));
        if(message != null) args.putString("message", message instanceof String ? (String)message : AppExtensions.getString((Integer)message));
        if(leftButton != null) args.putString("leftButton", leftButton instanceof String ? (String)leftButton : AppExtensions.getString((Integer)leftButton));
        if(rightButton != null) args.putString("rightButton", rightButton instanceof String ? (String)rightButton : AppExtensions.getString((Integer)rightButton));
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppExtensions.halfScreenDialog(getDialog());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_custom_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        idSetup(view);

        init();
    }

    private void idSetup(View view) {
        title = view.findViewById(R.id.title);
        message = view.findViewById(R.id.message);
        leftBtn = view.findViewById(R.id.leftBtn);
        rightBtn = view.findViewById(R.id.rightBtn);
        divider = view.findViewById(R.id.divider);
    }

    private void init(){
        if (getArguments() == null) { dismiss(); return; }

        String getTitle = getArguments().getString("title");
        title.setVisibility(getTitle != null ? View.VISIBLE : View.GONE);
        title.setText(getTitle);
        getArguments().remove("title");

        String getMessage = getArguments().getString("message");
        message.setVisibility(getMessage != null ? View.VISIBLE : View.GONE);
        message.setText(getMessage);
        getArguments().remove("message");

        String getLeftButton = getArguments().getString("leftButton");
        leftBtn.setVisibility(getLeftButton != null ? View.VISIBLE : View.INVISIBLE);
        leftBtn.setText(getLeftButton);
        getArguments().remove("leftButton");

        String getRightButton = getArguments().getString("rightButton");
        rightBtn.setVisibility(getRightButton != null ? View.VISIBLE : View.INVISIBLE);
        rightBtn.setText(getRightButton);
        getArguments().remove("rightButton");

        divider.setVisibility(getLeftButton != null && getRightButton != null ? View.VISIBLE : View.GONE);

        title.setGravity(getLeftButton == null && getRightButton == null ? Gravity.START : Gravity.CENTER);
        message.setGravity(getLeftButton == null && getRightButton == null ? Gravity.START : Gravity.CENTER);

        leftBtn.setOnClickListener(v -> {
            if(mOnDialogListener != null) mOnDialogListener.onLeftButtonClick();
            dismiss();
        });

        rightBtn.setOnClickListener(v -> {
            if(mOnDialogListener != null) mOnDialogListener.onRightButtonClick();
            dismiss();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setOnDialogListener(OnDialogListener mOnDialogListener) {
        this.mOnDialogListener = mOnDialogListener;
    }

    public interface OnDialogListener {
        void onLeftButtonClick();
        void onRightButtonClick();
    }
}
