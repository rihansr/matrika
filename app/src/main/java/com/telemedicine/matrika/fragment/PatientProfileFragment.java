package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.CustomPopup;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Photo;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.wiget.CircleImageView;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PatientProfileFragment extends DialogFragment {

    private static final String     TAG = PatientProfileFragment.class.getSimpleName();
    private Context                 context;
    private Activity                activity;

    /**
     * Toolbar
     **/
    private AppCompatImageView      back_Button;
    private AppCompatImageView      more_Button;

    /**
     * Content
     **/
    private FrameLayout             photo_Holder;
    private CircleImageView         patient_Photo;
    private ProgressBar             photoUpload_ProgressBar;
    private AppCompatTextView       photoUpload_Progress_Tv;
    private AppCompatImageView      photoUpload_Icon;
    private AppCompatTextView       patient_Name;
    private AppCompatTextView       patient_Email;
    private AppCompatTextView       patient_Info;
    private AppCompatTextView       patient_Phone;
    private AppCompatTextView       patient_Height;
    private AppCompatTextView       patient_Weight;
    private AppCompatTextView       patient_MaritalStatus;

    /**
     * Other
     **/
    private boolean                 isProfileUpdated = false;
    private User                    patient;
    private final FirebaseHelper    firebaseHelper = new FirebaseHelper();
    private LoadingFragment         loading;
    private OnProfileUpdateListener mOnProfileUpdateListener;

    public static PatientProfileFragment show(User patient){
        PatientProfileFragment fragment = new PatientProfileFragment();
         if(patient != null){
            Bundle args = new Bundle();
            args.putSerializable(Constants.USER_BUNDLE_KEY, patient);
            fragment.setArguments(args);
        }
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Dialog_FullDark_FadeAnimation);
        setRetainInstance(true);
        setCancelable(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_patient, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        if (getArguments() == null) { dismiss(); return; }
        patient = (User) getArguments().getSerializable(Constants.USER_BUNDLE_KEY);
        getArguments().remove(Constants.USER_BUNDLE_KEY);
        if (patient == null) { dismiss(); return; }
        init();
    }

    private void initId(View view) {
        back_Button = view.findViewById(R.id.toolbar_Left_Button);
        more_Button = view.findViewById(R.id.toolbar_Right_Button);
        photo_Holder = view.findViewById(R.id.profile_PhotoHolder_Layout);
        patient_Photo = view.findViewById(R.id.profile_Photo_Iv);
        photoUpload_ProgressBar = view.findViewById(R.id.profile_PhotoUpload_Progress);
        photoUpload_Progress_Tv = view.findViewById(R.id.profile_PhotoUpload_Tv);
        photoUpload_Icon = view.findViewById(R.id.profile_PhotoUpload_Iv);
        patient_Name = view.findViewById(R.id.profile_Name_Tv);
        patient_Email = view.findViewById(R.id.profile_Email_Tv);
        patient_Info = view.findViewById(R.id.profile_Info_Tv);
        patient_Phone = view.findViewById(R.id.profile_Phone_Tv);
        patient_Height = view.findViewById(R.id.profile_Height_Tv);
        patient_Weight = view.findViewById(R.id.profile_Weight_Tv);
        patient_MaritalStatus = view.findViewById(R.id.profile_MaritalStatus_Tv);
    }

    private void init() {
        back_Button.setOnClickListener(v -> dismiss());

        more_Button.setVisibility(LocalStorage.USER.getRole().equals(Role.DOCTOR.getId()) ? View.VISIBLE : View.GONE);
        more_Button.setOnClickListener(v -> new CustomPopup(v,
                new String[]{AppExtensions.getString(R.string.report)}, CustomPopup.Popup.MENU).setOnPopupListener((position, item) -> {
                if (position == 0) {
                    ReportFragment.show().setOnReportListener(report -> {
                        loading = LoadingFragment.show();
                        report.setReportedTo(patient.getId());
                        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance().collection(FirebaseHelper.PROFILE_REPORTS_TABLE).document(report.getId()).set(report),
                                new FirebaseHelper.OnFirebaseUpdateListener() {
                                    @Override
                                    public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onFailure() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                });
                    });
            }
        }));

        AppExtensions.loadPhoto(patient_Photo, patient.getPhoto(), R.dimen.icon_Size_XXXXXX_Large, R.drawable.ic_avatar);

        photoUpload_Icon.setVisibility(LocalStorage.USER.getRole().equals(Role.PATIENT.getId()) ? View.VISIBLE : View.GONE);
        photo_Holder.setOnClickListener(view -> {
            if (LocalStorage.USER.getRole().equals(Role.DOCTOR.getId())) {
                PhotoViewFragment.show(patient.getPhoto());
                return;
            }
            PhotoActionFragment.show().setOnActionListener((dialog, isCapture) -> {
                if (isCapture) captureByCamera();
                else pickFromGallery(R.string.select_ProfilePhoto);
                dialog.dismiss();
            });
        });

        patient_Name.setText(patient.getName());
        patient_Email.setText(patient.getEmail());

        String age = patient.getAge() == null ? "" : " " + (AppExtensions.getString(R.string.bullet) + " " +  patient.getAge()) + " " +  AppExtensions.getString(R.string.yrs);
        patient_Info.setText(String.format(Locale.getDefault(),"%s%s", patient.getGender(), age));
        patient_Phone.setText(patient.getPhone());

        String height = patient.getHeight() == null ? "" : AppExtensions.decimalFormat(patient.getHeight(), "#.#", false) + " " + AppExtensions.getString(R.string.feet_Hint);
        patient_Height.setText(String.format(Locale.getDefault(),"%s", height));

        String weight = patient.getWeight() == null ? "" : AppExtensions.decimalFormat(patient.getWeight(), "#.#", false) + " " + AppExtensions.getString(R.string.kg_Hint);
        patient_Weight.setText(String.format(Locale.getDefault(),"%s", weight));
        patient_MaritalStatus.setText(patient.getMaritalStatus());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                if(mBitmap == null) return;
                uploadProfilePhoto(AppExtensions.getBitmapBytes(mBitmap, 612));
            }
            catch (IOException ex) {
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.failureToUpload, R.string.retry, CustomSnackBar.Duration.LONG).show();
                ex.printStackTrace();
            }
        }
        else if(requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                if(photoUri == null) return;
                uploadProfilePhoto(photoUri);
                photoUri = null;
            }
            catch (Exception ex){
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.failureToUpload, R.string.retry, CustomSnackBar.Duration.LONG).show();
                ex.printStackTrace();
            }
        }
    }

    private void pickFromGallery(int chooserTitle) {
        if (!new PermissionManager(PermissionManager.Permission.GALLERY, true, response -> pickFromGallery(chooserTitle)).isGranted()) return;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, AppExtensions.getString(chooserTitle)), Constants.GALLERY_REQUEST_CODE);
    }

    private Uri photoUri = null;
    private void captureByCamera() {
        if (!new PermissionManager(PermissionManager.Permission.CAMERA, true, response -> captureByCamera() ).isGranted()) return;
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        java.io.File file = new java.io.File(activity.getExternalCacheDir(), (UUID.randomUUID() + ".jpg"));
        if (file.exists()) file.delete();
        photoUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new java.io.File(String.valueOf(file)));
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takePicture, Constants.CAMERA_REQUEST_CODE);
    }

    private void uploadProfilePhoto(Object photoFile){
        if(!Constants.IS_INTERNET_CONNECTED){
            new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.network_Error, R.string.retry, CustomSnackBar.Duration.LONG).show();
            return;
        }

        loading = LoadingFragment.show();

        firebaseHelper.uploadPhoto(photoFile, Photo.PROFILE, new FirebaseHelper.OnPhotoUploadListener() {
            @Override
            public void onSuccess(String userPhotoLink) {
                FirebaseFirestore.getInstance()
                        .collection(FirebaseHelper.USERS_TABLE)
                        .document(LocalStorage.USER.getId())
                        .update(User.PHOTO, userPhotoLink)
                        .addOnSuccessListener(aVoid -> {
                            LocalStorage.USER.setPhoto(userPhotoLink);
                            LocalStorage.setUserInfo(LocalStorage.USER, true);

                            isProfileUpdated = true;
                            AppExtensions.loadPhoto(patient_Photo, userPhotoLink, R.dimen.icon_Size_XXXXXX_Large, R.drawable.ic_avatar);

                            updateUI(View.GONE, View.GONE, 0);
                            AppExtensions.dismissLoading(loading);
                        })
                        .addOnFailureListener(e -> {
                            updateUI(View.GONE, View.GONE, 0);
                            AppExtensions.dismissLoading(loading);
                            new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.profileUpdateFailed, R.string.retry, CustomSnackBar.Duration.LONG).show();
                            e.printStackTrace();
                        });
            }

            @Override
            public void onFailure() {
                updateUI(View.GONE, View.GONE, 0);
                AppExtensions.dismissLoading(loading);
            }

            @Override
            public void onProgress(double progress) {
                updateUI(View.VISIBLE, View.VISIBLE, (int) progress);
            }
        });
    }

    private void updateUI(int showProgressBar, int showProgress, int progress){
        photoUpload_ProgressBar.setVisibility(showProgressBar);
        photoUpload_ProgressBar.setProgress(progress);
        photoUpload_Progress_Tv.setVisibility(showProgress);
        photoUpload_Progress_Tv.setText(String.format(Locale.getDefault(), "%d%s", progress, "%"));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnProfileUpdateListener != null) mOnProfileUpdateListener.onProfileUpdate(isProfileUpdated);
    }

    public void setOnProfileUpdateListener(OnProfileUpdateListener mOnProfileUpdateListener) {
        this.mOnProfileUpdateListener = mOnProfileUpdateListener;
    }

    public interface OnProfileUpdateListener {
        void onProfileUpdate(boolean isUpdated);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity) context;
    }
}
