package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.model.other.Follower;
import com.telemedicine.matrika.model.other.Request;
import com.telemedicine.matrika.model.other.Status;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class DoctorProfileFragment extends DialogFragment implements AppBaseActivity.OnUserRequestListener, AppBaseActivity.OnUserFollowListener {

    private static final String     TAG = DoctorProfileFragment.class.getSimpleName();
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
    private CircleImageView         doctor_Photo;
    private ProgressBar             photoUpload_ProgressBar;
    private AppCompatTextView       photoUpload_Progress_Tv;
    private AppCompatImageView      photoUpload_Icon;
    private AppCompatTextView       doctor_Name;
    private AppCompatTextView       doctor_Specialty;
    private AppCompatTextView       doctor_Specialists;
    private AppCompatTextView       doctor_Followers;
    private AppCompatTextView       doctor_ExperienceYears;
    private AppCompatTextView       doctor_Ratings;
    private AppCompatTextView       doctor_Bio;
    private AppCompatTextView       doctor_Qualifications;
    private AppCompatTextView       doctor_Awards;
    private AppCompatTextView       doctor_Languages;

    private LinearLayoutCompat      chat_Button;
    private LinearLayoutCompat      call_Button;

    /**
     * Other
     **/
    private boolean                 isProfileUpdated = false;
    private User                    doctor;
    private final FirebaseHelper    firebaseHelper = new FirebaseHelper();
    private LoadingFragment         loading;
    private OnProfileUpdateListener mOnProfileUpdateListener;

    public static DoctorProfileFragment show(User doctor){
        DoctorProfileFragment fragment = new DoctorProfileFragment();
         if(doctor != null){
            Bundle args = new Bundle();
            args.putSerializable(Constants.USER_BUNDLE_KEY, doctor);
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        if (getArguments() == null) { dismiss(); return; }
        doctor = (User) getArguments().getSerializable(Constants.USER_BUNDLE_KEY);
        getArguments().remove(Constants.USER_BUNDLE_KEY);
        if (doctor == null) { dismiss(); return; }
        init();
        ((AppBaseActivity) activity).setOnUserRequestListener(this);
        ((AppBaseActivity) activity).setOnUserFollowListener(this);
    }

    private void initId(View view) {
        back_Button = view.findViewById(R.id.toolbar_Left_Button);
        more_Button = view.findViewById(R.id.toolbar_Right_Button);
        photo_Holder = view.findViewById(R.id.profile_PhotoHolder_Layout);
        doctor_Photo = view.findViewById(R.id.profile_Photo_Iv);
        photoUpload_ProgressBar = view.findViewById(R.id.profile_PhotoUpload_Progress);
        photoUpload_Progress_Tv = view.findViewById(R.id.profile_PhotoUpload_Tv);
        photoUpload_Icon = view.findViewById(R.id.profile_PhotoUpload_Iv);
        doctor_Name = view.findViewById(R.id.profile_Name_Tv);
        doctor_Specialty = view.findViewById(R.id.profile_Specialty_Tv);
        doctor_Specialists = view.findViewById(R.id.profile_Specialists_Tv);
        doctor_Followers = view.findViewById(R.id.profile_Followers_Tv);
        doctor_ExperienceYears = view.findViewById(R.id.profile_ExperienceYears_Tv);
        doctor_Ratings = view.findViewById(R.id.profile_Ratings_Tv);
        doctor_Bio = view.findViewById(R.id.profile_Bio_Tv);
        doctor_Qualifications = view.findViewById(R.id.profile_Qualifications_Tv);
        doctor_Awards = view.findViewById(R.id.profile_Awards_Tv);
        doctor_Languages = view.findViewById(R.id.profile_Languages_Tv);
        chat_Button = view.findViewById(R.id.profile_Chat_Button);
        call_Button = view.findViewById(R.id.profile_AudioCall_Button);
    }

    private void init() {
        back_Button.setOnClickListener(v -> dismiss());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        more_Button.setVisibility(LocalStorage.USER.getRole().equals(Role.PATIENT.getId()) ? View.VISIBLE : View.GONE);
        more_Button.setOnClickListener(v -> new CustomPopup(v,
                new String[]{
                        AppExtensions.getString(LocalStorage.sentRequestIds.contains(doctor.getId()) ? R.string.cancelRequest : R.string.addDoctor),
                        AppExtensions.getString(LocalStorage.followingIds.contains(doctor.getId()) ? R.string.unfollow : R.string.follow),
                        AppExtensions.getString(R.string.ratingAndFeedback),
                        AppExtensions.getString(R.string.report)
                }, CustomPopup.Popup.MENU).setOnPopupListener((position, item) -> {

            switch (position) {
                case 0:
                    loading = LoadingFragment.show();
                    Request request = new Request();
                    request.setId(UUID.randomUUID().toString());
                    request.setRole(Role.PATIENT.getId());
                    request.setRequestedBy(LocalStorage.USER.getId());
                    request.setRequestedTo(doctor.getId());
                    request.setAccepted(new Status(false, new Date()));
                    request.setRejected(new Status(false, new Date()));
                    request.setRequestSentAt(new Date());

                    Query requestQuery = db.collection(FirebaseHelper.USER_REQUESTS_TABLE)
                            .whereEqualTo(Request.BY, LocalStorage.USER.getId())
                            .whereEqualTo(Request.TO, doctor.getId());

                    requestQuery.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            if (snapshots.isEmpty()) {
                                firebaseHelper.setDocumentData(db.collection(FirebaseHelper.USER_REQUESTS_TABLE)
                                                .document(request.getId())
                                                .set(request),
                                                new FirebaseHelper.OnFirebaseUpdateListener() {
                                                    @Override
                                                    public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                                    @Override
                                                    public void onFailure() { AppExtensions.dismissLoading(loading); }

                                                    @Override
                                                    public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                                });
                            }
                            else {
                                Request patientRequest = snapshots.getDocuments().get(0).toObject(Request.class);
                                if(patientRequest == null) { AppExtensions.dismissLoading(loading); return; }

                                Task<Void> requestTask = patientRequest.getRejected().getStatus()
                                        ?
                                        db.collection(FirebaseHelper.USER_REQUESTS_TABLE)
                                                .document(patientRequest.getId())
                                                .update(Request.ACCEPTED, new Status(false, new Date()), Request.REJECTED, new Status(false, new Date()))
                                        :
                                        db.collection(FirebaseHelper.USER_REQUESTS_TABLE)
                                                .document(patientRequest.getId())
                                                .delete();

                                firebaseHelper.setDocumentData(requestTask, new FirebaseHelper.OnFirebaseUpdateListener() {
                                    @Override
                                    public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onFailure() { AppExtensions.dismissLoading(loading); }

                                    @Override
                                    public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                });
                            }
                        }
                        else {
                            AppExtensions.dismissLoading(loading);
                            new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.requestFailed, R.string.tryAgain, CustomSnackBar.Duration.LONG).show();
                        }
                    });
                    break;

                case 1:
                    loading = LoadingFragment.show();
                    Follower follower = new Follower();
                    follower.setId(UUID.randomUUID().toString());
                    follower.setRole(Role.PATIENT.getId());
                    follower.setFollowedBy(LocalStorage.USER.getId());
                    follower.setFollowedTo(doctor.getId());
                    follower.setFollowedAt(new Date());

                    Query followerQuery = db.collection(FirebaseHelper.FOLLOWERS_TABLE)
                            .whereEqualTo(Follower.BY, LocalStorage.USER.getId())
                            .whereEqualTo(Follower.TO, doctor.getId());

                    followerQuery.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshots = task.getResult();
                            if (snapshots.isEmpty()) {
                                WriteBatch batch = db.batch();
                                batch.set(db.collection(FirebaseHelper.FOLLOWERS_TABLE).document(follower.getId()), follower);
                                batch.update(db.collection(FirebaseHelper.USERS_TABLE).document(doctor.getId()), User.FOLLOWER, (doctor.getFollowers() + 1));

                                batch.commit().addOnCompleteListener(batchTask -> {
                                    if(!batchTask.isSuccessful()){
                                        AppExtensions.dismissLoading(loading);
                                        new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.requestFailed, R.string.tryAgain, CustomSnackBar.Duration.LONG).show();
                                    }
                                    else getDoctorInfo();
                                });
                            }
                            else {
                                Follower patientFollower = snapshots.getDocuments().get(0).toObject(Follower.class);
                                if(patientFollower == null) { AppExtensions.dismissLoading(loading); return; }

                                WriteBatch batch = db.batch();

                                batch.delete(db.collection(FirebaseHelper.FOLLOWERS_TABLE).document(patientFollower.getId()));

                                batch.update(db.collection(FirebaseHelper.USERS_TABLE).document(doctor.getId()), User.FOLLOWER, (doctor.getFollowers() - 1));

                                batch.commit().addOnCompleteListener(batchTask -> {
                                    if(!batchTask.isSuccessful()){
                                        AppExtensions.dismissLoading(loading);
                                        new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.requestFailed, R.string.tryAgain, CustomSnackBar.Duration.LONG).show();
                                    }
                                    else getDoctorInfo();
                                });
                            }
                        } else {
                            AppExtensions.dismissLoading(loading);
                            new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.requestFailed, R.string.tryAgain, CustomSnackBar.Duration.LONG).show();
                        }
                    });
                    break;

                case 2:
                    FeedbackFragment.show().setOnFeedbackListener(feedback -> {
                        loading = LoadingFragment.show();
                        feedback.setSentTo(doctor.getId());

                        WriteBatch batch = db.batch();

                        batch.set(db.collection(FirebaseHelper.USER_FEEDBACKS_TABLE).document(feedback.getId()), feedback);

                        batch.update(db.collection(FirebaseHelper.USERS_TABLE)
                                .document(doctor.getId()), User.RATING, ((doctor.getRating() + feedback.getRating()) / (doctor.getRating() < 1 ? 1 : 2)));

                        batch.commit().addOnCompleteListener(batchTask -> {
                            if(!batchTask.isSuccessful()){
                                AppExtensions.dismissLoading(loading);
                                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.feedbackFailed, R.string.tryAgain, CustomSnackBar.Duration.LONG).show();
                            }
                            else getDoctorInfo();
                        });
                    });
                    break;

                case 3:
                    ReportFragment.show().setOnReportListener(report -> {
                        loading = LoadingFragment.show();
                        report.setReportedTo(doctor.getId());
                        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                                        .collection(FirebaseHelper.PROFILE_REPORTS_TABLE)
                                        .document(report.getId())
                                        .set(report),
                                        new FirebaseHelper.OnFirebaseUpdateListener() {
                                            @Override
                                            public void onSuccess() { AppExtensions.dismissLoading(loading); }

                                            @Override
                                            public void onFailure() {
                                                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.reportFailed, R.string.tryAgain, CustomSnackBar.Duration.LONG).show();
                                                AppExtensions.dismissLoading(loading);
                                            }

                                            @Override
                                            public void onCancelled() { AppExtensions.dismissLoading(loading); }
                                        });
                    });
                    break;
            }
        }));

        AppExtensions.loadPhoto(doctor_Photo, doctor.getPhoto(), R.dimen.icon_Size_XXXXXX_Large, R.drawable.ic_avatar);

        photoUpload_Icon.setVisibility(LocalStorage.USER.getRole().equals(Role.DOCTOR.getId()) ? View.VISIBLE : View.GONE);
        photo_Holder.setOnClickListener(view -> {
            if (LocalStorage.USER.getRole().equals(Role.PATIENT.getId())) {
                PhotoViewFragment.show(doctor.getPhoto());
                return;
            }
            PhotoActionFragment.show().setOnActionListener((dialog, isCapture) -> {
                if (isCapture) captureByCamera();
                else pickFromGallery(R.string.select_ProfilePhoto);
                dialog.dismiss();
            });
        });

        doctor_Name.setText(doctor.getName());
        doctor_Specialty.setText(doctor.getSpecialty().getTitle());
        doctor_Specialists.setVisibility(doctor.getSpecialty().getSpecialists() == null || doctor.getSpecialty().getSpecialists().isEmpty() ? View.GONE : View.VISIBLE);
        doctor_Specialists.setText(AppExtensions.join(doctor.getSpecialty().getSpecialists(), ","));

        doctor_Followers.setText(String.format(Locale.getDefault(),"%d", doctor.getFollowers()));
        doctor_ExperienceYears.setText(String.format(Locale.getDefault(),"%d %s", doctor.getExperienceYears(), AppExtensions.getString(R.string.yrs)));
        doctor_Ratings.setText(String.format(Locale.getDefault(),"%s", AppExtensions.decimalFormat(doctor.getRating(), "0.0", true)));

        doctor_Bio.setText(doctor.getBio());
        doctor_Awards.setText(doctor.getAwards());
        doctor_Qualifications.setText(AppExtensions.join(doctor.getQualifications(), ","));
        doctor_Languages.setText(AppExtensions.join(doctor.getLanguages(), ","));

        chat_Button.setOnClickListener(view -> {
            if(LocalStorage.USER.getRole().equals(Role.DOCTOR.getId())) return;
            if(!doctor.isProfileVerified() || !isPatientAccepted()){
                String message = AppExtensions.getString(R.string.youCantChat) + " " + Role.DOCTOR.getTitle().toLowerCase() + "!";
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), message, R.string.okay, CustomSnackBar.Duration.SHORT).show();
                return;
            }

            ChatMessagesFragment.show(doctor, null, null);
        });

        call_Button.setOnClickListener(view -> {
            if(LocalStorage.USER.getRole().equals(Role.DOCTOR.getId())) return;
            if(!doctor.isProfileVerified() || !isPatientAccepted()){
                String message = AppExtensions.getString(R.string.youCantCall) + " " + Role.DOCTOR.getTitle().toLowerCase() + "!";
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), message, R.string.okay, CustomSnackBar.Duration.SHORT).show();
                return;
            }

            AppExtensions.call(doctor.getPhone(), doctor.getPhone());
        });
    }

    private boolean isPatientAccepted(){
        for (Request request : LocalStorage.sentRequests){
            if(request.getRejected().getStatus()) continue;
            if(!request.getAccepted().getStatus()) continue;
            if(request.getAccepted().getStatus() && request.getRequestedTo().equals(doctor.getId())) return true;
        }
        return false;
    }

    private void getDoctorInfo(){
        FirebaseFirestore.getInstance().collection(FirebaseHelper.USERS_TABLE)
                .document(doctor.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    AppExtensions.dismissLoading(loading);
                    if(!documentSnapshot.exists()) return;
                    doctor = documentSnapshot.toObject(User.class);
                    init();
                })
                .addOnFailureListener(e -> Log.e(Constants.TAG, e.toString()));
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
                            AppExtensions.loadPhoto(doctor_Photo, userPhotoLink, R.dimen.icon_Size_XXXXXX_Large, R.drawable.ic_avatar);

                            updateUI(View.GONE, View.GONE, 0);
                            AppExtensions.dismissLoading(loading);
                        })
                        .addOnFailureListener(e -> {
                            updateUI(View.GONE, View.GONE, 0);
                            AppExtensions.dismissLoading(loading);
                            AppExtensions.toast(R.string.failureMessage);
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
    public void onRequest(List<Request> requests) {
        init();
    }

    @Override
    public void onFollow(List<Follower> requests) {
        init();
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (Activity) context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
