package com.telemedicine.matrika.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.model.other.File;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.CustomPopup;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Photo;
import com.telemedicine.matrika.util.enums.Report;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;

public class PatientReportUploadFragment extends DialogFragment {

    private static final String     TAG = PatientReportUploadFragment.class.getSimpleName();
    private Context                 context;
    private Activity                activity;

    private AppCompatImageView      report_Photo;
    private FloatingActionButton    photoUpload_Button;
    private AppCompatEditText       title_Input;
    private AppCompatEditText       description_Input;
    private AppCompatButton         submit_Button;
    private AppCompatImageView      back_Button;
    private AppCompatImageView      more_Button;
    private HashMap<String, Object> photoData = null;
    private FirebaseHelper          firebaseHelper;
    private ProgressDialog          progressDialog;
    private Report                  reportType = null;

    public static PatientReportUploadFragment show(Report reportType){
        PatientReportUploadFragment fragment = new PatientReportUploadFragment();
        fragment.setArguments(buildArguments(reportType, null));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    public static PatientReportUploadFragment show(Report reportType, File report){
        PatientReportUploadFragment fragment = new PatientReportUploadFragment();
        fragment.setArguments(buildArguments(reportType, report));
        fragment.show(((AppCompatActivity) AppController.getActivity()).getSupportFragmentManager(), TAG);
        return fragment;
    }

    private static Bundle buildArguments(Report reportType, File file){
        Bundle args = new Bundle();
        args.putSerializable(Constants.REPORT_TYPE_BUNDLE_KEY, reportType);
        if(file != null) args.putSerializable(Constants.FILE_BUNDLE_KEY, file);
        return args;
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
        return inflater.inflate(R.layout.fragment_layout_upload_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initId(view);

        init();
    }

    private void initId(View view) {
        report_Photo = view.findViewById(R.id.attachment_Photo_Iv);
        photoUpload_Button = view.findViewById(R.id.attachment_PhotoUpload_Button);
        title_Input = view.findViewById(R.id.attachment_Title_Input);
        description_Input = view.findViewById(R.id.attachment_Description_Input);
        submit_Button = view.findViewById(R.id.attachment_Submit_Button);
        back_Button = view.findViewById(R.id.attachment_Back_Button);
        more_Button = view.findViewById(R.id.attachment_More_Button);
        firebaseHelper = new FirebaseHelper();
        progressDialog = new ProgressDialog(activity, R.style.ProgressDialog);
    }

    File getFile = null;
    private void init() {
        if (getArguments() != null && getArguments().containsKey(Constants.FILE_BUNDLE_KEY)) {
            getFile = (File) getArguments().getSerializable(Constants.FILE_BUNDLE_KEY);
            getArguments().remove(Constants.FILE_BUNDLE_KEY);

            if(getFile != null) {
                submit_Button.setText(AppExtensions.getString(R.string.update));
                more_Button.setVisibility(getFile != null ? View.VISIBLE : View.GONE);
                AppExtensions.loadPhoto(report_Photo, getFile.getPath(), R.dimen._176sdp, R.drawable.ic_placeholder_large);
                report_Photo.setOnClickListener(view -> PhotoViewFragment.show(getFile.getPath()));
                title_Input.setText(getFile.getTitle());
                description_Input.setText(getFile.getDescription());
            }
        }

        if (getArguments() != null && getArguments().containsKey(Constants.REPORT_TYPE_BUNDLE_KEY)) {
            reportType = (Report) getArguments().getSerializable(Constants.REPORT_TYPE_BUNDLE_KEY);
            getArguments().remove(Constants.REPORT_TYPE_BUNDLE_KEY);
        }

        back_Button.setOnClickListener(view -> dismiss());

        photoUpload_Button.setOnClickListener(view -> {
            switch (reportType) {
                case MEDICAL_REPORT:
                    PhotoActionFragment.show().setOnActionListener((dialog, isCapture) -> {
                        if (isCapture) captureByCamera();
                        else pickFromGallery(R.string.select_ReportPhoto);
                        dialog.dismiss();
                    });
                    break;

                case DEVICE_REPORT:
                    pickFromGallery(R.string.select_ReportPhoto);
                    break;
            }
        });

        File finalFile = getFile;

        more_Button.setOnClickListener(v ->
                new CustomPopup(v, new String[]{AppExtensions.getString(R.string.delete)}, CustomPopup.Popup.MENU)
                        .setOnPopupListener((position, item) -> {
                            if (position == 0) deleteReport(finalFile);
                        })
        );

        submit_Button.setOnClickListener(view -> {
            if(finalFile == null){
                uploadFile();
            }
            else {
                if(photoData == null) submitData();
                else uploadFile();
            }
        });
    }

    private void submitData(){
        progressDialog.setMessage(AppExtensions.getString(R.string.processing));

        String title = Objects.requireNonNull(title_Input.getText()).toString().trim();
        String description = Objects.requireNonNull(description_Input.getText()).toString().trim();

        String fileId = getFile == null ? UUID.randomUUID().toString() : getFile.getId();

        HashMap<String, Object> file = new HashMap<>();
        file.put(File.ID, fileId);
        file.put(File.TITLE, title);
        file.put(File.DESC, description);
        file.put(File.TYPE, "Image");
        file.put(File.PATH, photoData == null ? getFile.getPath() : (String) photoData.get("path"));
        file.put(File.SIZE, photoData == null ? getFile.getSize() : (Long) photoData.get("size"));
        file.put(File.SENT_AT, new Date());

        if(getFile == null) addReport(file);
        else updateReport(file);
    }

    private void addReport(HashMap<String, Object> file) {
        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                        .collection(reportType.getTable())
                        .document(LocalStorage.USER.getId())
                        .collection(FirebaseHelper.REPORTS_COLLECTION)
                        .document((String) Objects.requireNonNull(file.get(File.ID)))
                        .set(file),
                new FirebaseHelper.OnFirebaseUpdateListener() {
                    @Override
                    public void onSuccess() { progressDialog.dismiss(); dismiss(); }

                    @Override
                    public void onFailure() { progressDialog.dismiss(); dismiss(); }

                    @Override
                    public void onCancelled() { progressDialog.dismiss(); dismiss(); }
                });
    }

    private void updateReport(HashMap<String, Object> file) {
        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                        .collection(reportType.getTable())
                        .document(LocalStorage.USER.getId())
                        .collection(FirebaseHelper.REPORTS_COLLECTION)
                        .document((String) Objects.requireNonNull(file.get(File.ID)))
                        .update(file),
                new FirebaseHelper.OnFirebaseUpdateListener() {
                    @Override
                    public void onSuccess() { progressDialog.dismiss(); dismiss(); }

                    @Override
                    public void onFailure() { progressDialog.dismiss(); dismiss(); }

                    @Override
                    public void onCancelled() { progressDialog.dismiss(); dismiss(); }
                });
    }

    private void deleteReport(File file) {
        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                        .collection(reportType.getTable())
                        .document(LocalStorage.USER.getId())
                        .collection(FirebaseHelper.REPORTS_COLLECTION)
                        .document(file.getId())
                        .delete(),
                        new FirebaseHelper.OnFirebaseUpdateListener() {
                            @Override
                            public void onSuccess() { dismiss(); }

                            @Override
                            public void onFailure() { dismiss(); }

                            @Override
                            public void onCancelled() { dismiss(); }
                        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                if(mBitmap == null) return;
                report_Photo.setImageBitmap(mBitmap);
                photoData = AppExtensions.getBitmapData(mBitmap, 1024);
            }
            catch (IOException ex) {
                new CustomSnackBar(AppExtensions.getRootView(getDialog()), R.string.failureToUpload, R.string.retry, CustomSnackBar.Duration.LONG).show();
                ex.printStackTrace();
            }
        }
        else if(requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                if(photoUri == null) return;
                report_Photo.setImageURI(photoUri);
                photoData = AppExtensions.getUriData(photoUri);
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

    private void uploadFile(){
        progressDialog.setMessage(AppExtensions.getString(R.string.uploading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        firebaseHelper.uploadPhoto(photoData.get("file"), Photo.REPORT, new FirebaseHelper.OnPhotoUploadListener() {
            @Override
            public void onSuccess(String photoLink) {
                photoData.put("path", photoLink);
                submitData();
            }

            @Override
            public void onFailure() {
                AppExtensions.toast(R.string.sendingMessageFailed);
            }

            @Override
            public void onProgress(double progress) {
                progressDialog.setMessage((int)progress + "% " + AppExtensions.getString(R.string.complete));
            }
        });
    }

    /**
     *  Context Bind
     **/
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
