package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.activity.SignInActivity;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.CustomProgressDialog;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.util.Objects;

public class ResetPasswordFragment extends DialogFragment {

    private static final String     TAG = ResetPasswordFragment.class.getSimpleName();
    private Activity                activity;
    private AppCompatEditText       email_Input;
    private AppCompatButton         send_Button;
    private AppCompatImageView      back_Button;
    private OnSuccessListener       mOnSuccessListener;
    private CustomProgressDialog    progressDialog;

    public static ResetPasswordFragment show(){
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        back_Button.setOnClickListener(view1 -> dismiss());

        send_Button.setOnClickListener(v -> {
            if(!Constants.IS_INTERNET_CONNECTED){
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.network_Error, R.string.retry, CustomSnackBar.Duration.LONG).show();
                return;
            }

            sentEmail();
        });
    }

    private void initId(View view) {
        email_Input = view.findViewById(R.id.passwordReset_Email_Input);
        send_Button = view.findViewById(R.id.passwordReset_Send_Button);
        back_Button = view.findViewById(R.id.passwordReset_Back_Button);
        progressDialog = new CustomProgressDialog();
    }

    private void sentEmail(){
        final String email = Objects.requireNonNull(email_Input.getText()).toString().trim();

        if (!AppExtensions.isEmailValid(email_Input, R.string.email_Error)) return;

        progressDialog.show(R.string.processing, false);

        /**
         * Send password reset email
         **/
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    dismiss();
                    progressDialog.dismiss();
                    if (mOnSuccessListener != null) mOnSuccessListener.onSuccess(email, task.isSuccessful());
                })
                .addOnFailureListener(e -> {
                    new CustomSnackBar(AppExtensions.getRootView(getDialog()), e.getMessage(), R.string.retry, CustomSnackBar.Duration.LONG).show();
                    e.printStackTrace();
                });
    }


    /**
     *  Send success status to {@link SignInActivity}
     **/
    public void setOnSuccessListener(OnSuccessListener mOnSuccessListener) {
        this.mOnSuccessListener = mOnSuccessListener;
    }

    public interface OnSuccessListener {
        void onSuccess(String email, boolean isSuccessful);
    }


    /**
     *  Hide soft keyboard when click outside
     **/
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(activity, getTheme()) {
           @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View v = getCurrentFocus();
                    if (v instanceof AppCompatEditText) {
                        Rect outRect = new Rect();
                        v.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                            v.clearFocus();
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                        }
                    }
                }
                return super.dispatchTouchEvent(event);
            }
        };
    }


    /**
     *  Context Bind
     **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    /**
     * {@link #onStop()} called when the activity is no longer visible to the user
     **/
    @Override
    public void onStop() {
        super.onStop();
        try {
            progressDialog.dismiss();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
