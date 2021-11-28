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
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.DialogFragment;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.model.other.Feedback;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class FeedbackFragment extends DialogFragment {

    private static final String TAG = FeedbackFragment.class.getSimpleName();
    private Activity            activity;
    private AppCompatRatingBar  user_Rating;
    private AppCompatEditText   title_Input;
    private AppCompatEditText   description_Input;
    private AppCompatButton     submit_Button;
    private AppCompatImageView  back_Button;
    private OnFeedbackListener  mOnFeedbackListener;

    public static FeedbackFragment show(){
        FeedbackFragment fragment = new FeedbackFragment();
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setCancelable(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        AppExtensions.halfScreenDialog(getDialog());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        init();
    }

    private void initId(View view) {
        user_Rating = view.findViewById(R.id.feedback_Rating_Rb);
        title_Input = view.findViewById(R.id.feedback_Title_Input);
        description_Input = view.findViewById(R.id.feedback_Description_Input);
        submit_Button = view.findViewById(R.id.feedback_Submit_Button);
        back_Button = view.findViewById(R.id.feedback_Back_Button);
    }

    private void init() {
        back_Button.setOnClickListener(view -> dismiss());

        submit_Button.setOnClickListener(view -> {
            String title = Objects.requireNonNull(title_Input.getText()).toString().trim();
            String description = Objects.requireNonNull(description_Input.getText()).toString().trim();

            Feedback feedback = new Feedback();
            feedback.setId(UUID.randomUUID().toString());
            feedback.setRole(Constants.roleMode.getId());
            feedback.setSentBy(LocalStorage.USER.getId());
            feedback.setTitle(title);
            feedback.setDescription(description);
            feedback.setRating(user_Rating.getRating());
            feedback.setSentAt(new Date());

            if(mOnFeedbackListener != null) mOnFeedbackListener.onFeedback(feedback);
            dismiss();
        });
    }

    public interface OnFeedbackListener{
        void onFeedback(Feedback feedback);
    }

    public void setOnFeedbackListener(OnFeedbackListener mOnFeedbackListener) {
        this.mOnFeedbackListener = mOnFeedbackListener;
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
                            if (imm != null)
                                imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                        }
                    }
                }
                return super.dispatchTouchEvent(event);
            }
        };
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
