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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.model.other.Report;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ReportFragment extends DialogFragment {

    private static final String TAG = ReportFragment.class.getSimpleName();
    private Activity            activity;
    private AppCompatEditText   description_Input;
    private AppCompatButton     submit_Button;
    private AppCompatImageView  back_Button;
    private AppCompatTextView   report_Subtitle;
    private AppCompatTextView   report_Message;
    private OnReportListener    mOnReportListener;

    public static ReportFragment show(){
        ReportFragment fragment = new ReportFragment();
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
        return inflater.inflate(R.layout.fragment_layout_profile_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        init();
    }

    private void initId(View view) {
        description_Input = view.findViewById(R.id.report_Description_Input);
        submit_Button = view.findViewById(R.id.report_Submit_Button);
        back_Button = view.findViewById(R.id.report_Back_Button);
        report_Subtitle = view.findViewById(R.id.report_Subtitle_Tv);
        report_Message = view.findViewById(R.id.report_Message_Tv);
    }

    private void init() {
        back_Button.setOnClickListener(view -> dismiss());

        report_Subtitle.setText(AppExtensions.getString(LocalStorage.USER.getRole().equals(Role.PATIENT.getId()) ? R.string.toYourDoctor : R.string.toYourPatient));
        report_Message.setText(AppExtensions.getString(LocalStorage.USER.getRole().equals(Role.PATIENT.getId()) ? R.string.reportDoctorMessage : R.string.reportPatientMessage));

        submit_Button.setOnClickListener(view -> {
            String description = Objects.requireNonNull(description_Input.getText()).toString().trim();

            Report report = new Report();
            report.setId(UUID.randomUUID().toString());
            report.setRole(LocalStorage.USER.getRole());
            report.setReportedBy(LocalStorage.USER.getId());
            report.setReport(description);
            report.setReportedAt(new Date());

            if(mOnReportListener != null) mOnReportListener.onFeedback(report);
            dismiss();
        });
    }

    public interface OnReportListener {
        void onFeedback(Report report);
    }

    public void setOnReportListener(OnReportListener mOnReportListener) {
        this.mOnReportListener = mOnReportListener;
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
