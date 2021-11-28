package com.telemedicine.matrika.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.FileAdapter;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.model.other.File;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Report;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;

import java.util.ArrayList;
import java.util.List;

public class PatientReportsFragment extends DialogFragment {

    private static final String  TAG = PatientReportsFragment.class.getSimpleName();
    private View                 rootView;

    /**
     * Toolbar
     **/
    private AppCompatImageView   toolbar_Back_Button;
    private AppCompatTextView    toolbar_title;
    private AppCompatImageView   toolbar_Right_Button;

    /**
     * Content
     **/
    private RecyclerView         rcv_Files;
    private FileAdapter          reportsAdapter;
    private FloatingActionButton fileUpload_Button;
    private ListenerRegistration filesListenerRegistration;
    private String               UID = null;
    private Report               reportType = null;

    /**
     * Empty Layout
     **/
    private LinearLayoutCompat   empty_Layout;
    private LottieAnimationView  empty_Icon;
    private AppCompatTextView    empty_Title;
    private AppCompatTextView    empty_Subtitle;

    private boolean              allowShareFile = false;
    private OnFileListener       mOnFileListener;

    public static PatientReportsFragment show(Report reportType, String uID, Boolean allowShare){
        PatientReportsFragment fragment = new PatientReportsFragment();
        fragment.setArguments(buildArguments(reportType, uID, allowShare));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static PatientReportsFragment show(Report reportType, String uID){
        PatientReportsFragment fragment = new PatientReportsFragment();
        fragment.setArguments(buildArguments(reportType, uID, null));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    private static Bundle buildArguments(Report reportType, String uID, Boolean allowShare){
        Bundle args = new Bundle();
        args.putSerializable(Constants.REPORT_TYPE_BUNDLE_KEY, reportType);
        if(uID != null) args.putString(Constants.UID_BUNDLE_KEY, uID);
        args.putBoolean(Constants.SHARE_FILE_BUNDLE_KEY, allowShare == null ? false : allowShare);
        return args;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullDark_FadeAnimation);
        setRetainInstance(true);
        setCancelable(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_layout_user_report, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init(){
        if (getArguments() == null || !getArguments().containsKey(Constants.UID_BUNDLE_KEY)) { dismiss(); return; }
        UID = getArguments().getString(Constants.UID_BUNDLE_KEY);
        getArguments().remove(Constants.UID_BUNDLE_KEY);
        if (UID == null) { dismiss(); return; }

        if (getArguments() != null && getArguments().containsKey(Constants.SHARE_FILE_BUNDLE_KEY)) {
            allowShareFile = getArguments().getBoolean(Constants.SHARE_FILE_BUNDLE_KEY);
            getArguments().remove(Constants.SHARE_FILE_BUNDLE_KEY);
        }

        if (getArguments() != null && getArguments().containsKey(Constants.REPORT_TYPE_BUNDLE_KEY)) {
            reportType = (Report) getArguments().getSerializable(Constants.REPORT_TYPE_BUNDLE_KEY);
            getArguments().remove(Constants.REPORT_TYPE_BUNDLE_KEY);
        }

        initId();

        switch (reportType){
            case MEDICAL_REPORT: toolbar_title.setText(AppExtensions.getString(R.string.reports)); break;
            case DEVICE_REPORT: toolbar_title.setText(AppExtensions.getString(R.string.deviceReports)); break;
        }

        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        empty_Icon.setAnimation("lottie/blank_users.json");
        empty_Title.setText(AppExtensions.getString(R.string.emptyReportsTitle));
        empty_Subtitle.setText(AppExtensions.getString(R.string.emptyReportsSubtitle));

        fileUpload_Button.setVisibility(LocalStorage.USER.getRole().equals(Role.PATIENT.getId()) ? View.VISIBLE : View.GONE);
        fileUpload_Button.setOnClickListener(view -> PatientReportUploadFragment.show(reportType));

        setReportsAdapter();

        getAllReports();
    }

    private void initId() {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        empty_Layout = rootView.findViewById(R.id.reports_Empty_Layout);
        empty_Icon = rootView.findViewById(R.id.empty_Icon_Animation);
        empty_Title = rootView.findViewById(R.id.empty_Title_Tv);
        empty_Subtitle = rootView.findViewById(R.id.empty_Subtitle_Tv);

        rcv_Files = rootView.findViewById(R.id.reports_Files_Rcv);
        fileUpload_Button = rootView.findViewById(R.id.reports_FileUpload_Button);
    }

    private void getAllReports() {
        EventListener<QuerySnapshot> filesEventListener = (reportsSnapshot, error) -> {
            /** Error & Null Data Checking **/
            if (error != null) {
                reportsAdapter.setFiles(new ArrayList<>());
                Log.e(Constants.TAG, "Reports Error, Reason: " + error.getMessage(), error);
                if(error.getMessage().contains("PERMISSION_DENIED")){
                    empty_Icon.setAnimation("lottie/not_verified.json");
                    empty_Layout.setVisibility(View.VISIBLE);
                }
                return;
            }
            else if (reportsSnapshot == null || reportsSnapshot.isEmpty()) {
                reportsAdapter.setFiles(new ArrayList<>());
                empty_Icon.setAnimation("lottie/blank_users.json");
                empty_Layout.setVisibility(View.VISIBLE);
                Log.e(Constants.TAG, "No Reports");
                return;
            }

            /** Get Files **/
            List<File> reports = new ArrayList<>();
            for (DocumentSnapshot snapshot : reportsSnapshot){
                File file = snapshot.toObject(File.class);
                reports.add(file);
            }
            reportsAdapter.setFiles(reports);

            empty_Icon.setAnimation("lottie/blank_users.json");
            empty_Layout.setVisibility(reports.isEmpty() ? View.VISIBLE : View.GONE);
        };

        filesListenerRegistration = FirebaseFirestore.getInstance()
                .collection(reportType.getTable())
                .document(UID)
                .collection(FirebaseHelper.REPORTS_COLLECTION)
                .addSnapshotListener(filesEventListener);
    }

    private void setReportsAdapter() {
        reportsAdapter = new FileAdapter(3, reportType);
        rcv_Files.setAdapter(reportsAdapter);
        reportsAdapter.setOnFileListener(file -> {
            if(allowShareFile){
                AlertDialogFragment.show(R.string.fileShare, R.string.shareFileMessage, R.string.cancel, R.string.share)
                        .setOnDialogListener(new AlertDialogFragment.OnDialogListener() {
                            @Override
                            public void onLeftButtonClick() {}

                            @Override
                            public void onRightButtonClick() {
                                if(mOnFileListener != null) mOnFileListener.onFile(getDialog(), file);
                            }
                        });
            }
            else {
                PhotoViewFragment.show(file.getPath());
            }
        });
    }

    public interface OnFileListener {
        void onFile(Dialog dialog, File file);
    }

    public void setOnFileListener(OnFileListener mOnFileListener) {
        this.mOnFileListener = mOnFileListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(filesListenerRegistration != null) filesListenerRegistration.remove();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
