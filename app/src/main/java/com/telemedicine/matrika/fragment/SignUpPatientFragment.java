package com.telemedicine.matrika.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.activity.PatientHomeActivity;
import com.telemedicine.matrika.activity.SignUpActivity;
import com.telemedicine.matrika.adapter.DistrictsAdapter;
import com.telemedicine.matrika.adapter.UpazilasAdapter;
import com.telemedicine.matrika.model.address.Address;
import com.telemedicine.matrika.model.address.District;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.service.MatrikaLocationService;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.CustomPopup;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.GpsUtils;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.util.enums.DrawablePosition;
import com.telemedicine.matrika.util.enums.Photo;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.util.extensions.DateExtensions;
import com.telemedicine.matrika.util.extensions.LocationExtensions;
import com.telemedicine.matrika.util.interfaces.OnBackPressListener;
import com.telemedicine.matrika.wiget.CircleImageView;
import com.telemedicine.matrika.wiget.ClickableEditText;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ClickableViewAccessibility")
public class SignUpPatientFragment extends Fragment implements OnBackPressListener {

    private Context                         context;
    private Activity                        activity;

    /** Personal Info **/
    private View                            rootView;
    private RelativeLayout                  photoUpload_Button;
    private CircleImageView                 userPhoto;
    private AppCompatEditText               name_Input;
    private AppCompatEditText               email_Input;
    private AppCompatEditText               phone_Input;
    private AppCompatEditText               gender_Input;
    private AppCompatEditText               birthDate_Input;
    private AppCompatEditText               height_Input;
    private AppCompatEditText               weight_Input;
    private AppCompatEditText               maritalStatus_Input;

    /** Address **/
    private AppCompatEditText               address_Input;
    private AppCompatAutoCompleteTextView   district_Input;
    private AppCompatAutoCompleteTextView   upazila_Input;
    private AppCompatEditText               postalCode_Input;
    private AppCompatEditText               country_Input;
    private AppCompatTextView               currentLocation_Button;

    /** Password Panel **/
    private ClickableEditText               password_Input;
    private ClickableEditText               confirmPassword_Input;

    /** Other **/
    private AppCompatTextView               title_Tv;
    private ViewFlipper                     formFlipper;
    private AppCompatButton                 register_Button;
    private MatrikaLocationService          locationService;
    private Object                          photoFile = null;
    private FirebaseHelper                  firebaseHelper;
    private final PreferenceManager         pm = new PreferenceManager();
    private ProgressDialog                  progressDialog;

    public SignUpPatientFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign_up_patient, container, false);

        initId();

        init();

        return rootView;
    }

    private void initId() {
        photoUpload_Button = rootView.findViewById(R.id.signUp_UploadPhoto_Button);
        userPhoto = rootView.findViewById(R.id.signUp_UserPhoto_Iv);

        name_Input = rootView.findViewById(R.id.signUp_Name_Input);
        email_Input = rootView.findViewById(R.id.signUp_Email_Input);
        phone_Input = rootView.findViewById(R.id.signUp_Phone_Input);
        phone_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        gender_Input = rootView.findViewById(R.id.signUp_Gender_Input);
        gender_Input.setInputType(InputType.TYPE_NULL);
        birthDate_Input = rootView.findViewById(R.id.signUp_BirthDate_Input);
        birthDate_Input.setInputType(InputType.TYPE_NULL);
        height_Input = rootView.findViewById(R.id.signUp_Height_Input);
        height_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        weight_Input = rootView.findViewById(R.id.signUp_Weight_Input);
        weight_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        maritalStatus_Input = rootView.findViewById(R.id.signUp_MaritalStatus_Input);
        maritalStatus_Input.setInputType(InputType.TYPE_NULL);

        address_Input = rootView.findViewById(R.id.address_Input);
        district_Input = rootView.findViewById(R.id.district_Input);
        upazila_Input = rootView.findViewById(R.id.upazila_Input);
        postalCode_Input = rootView.findViewById(R.id.postalCode_Input);
        country_Input = rootView.findViewById(R.id.country_Input);
        country_Input.setInputType(InputType.TYPE_NULL);
        currentLocation_Button = rootView.findViewById(R.id.currentLocation_Button);

        password_Input = rootView.findViewById(R.id.password_Input);
        confirmPassword_Input = rootView.findViewById(R.id.confirmPassword_Input);

        title_Tv = rootView.findViewById(R.id.signUp_Title_Tv);
        formFlipper = rootView.findViewById(R.id.signUp_Form_Flipper);
        register_Button = rootView.findViewById(R.id.signUp_Register_Button);

        firebaseHelper = new FirebaseHelper();
        progressDialog = new ProgressDialog(activity, R.style.ProgressDialog);
    }

    private void init(){
        register_Button.setOnClickListener(view -> {
            if(formFlipper.getCurrentView().getId() == R.id.signUp_PersonalInfo_Layout){
                if (!AppExtensions.isInputValid(name_Input, R.string.name_Error)
                        || !AppExtensions.isEmailValid(email_Input, R.string.email_Error)
                        || !AppExtensions.isNumberValid(phone_Input, R.string.phone_Error)
                        || !AppExtensions.isInputValid(gender_Input, R.string.gender_Error)
                        || !AppExtensions.isInputValid(maritalStatus_Input, R.string.maritalStatus_Error)
                ) return;
                formFlipper.showNext();
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_Address_Layout){
                if(!AppExtensions.isInputValid(district_Input, R.string.district_Error)) return;
                formFlipper.showNext();
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_Password_Layout){
                if (!AppExtensions.isPasswordValid(password_Input, R.string.password_Error)
                        || !AppExtensions.isPasswordValid(confirmPassword_Input, R.string.password_Error)
                        || !AppExtensions.isInputValid(confirmPassword_Input,
                        !Objects.requireNonNull(password_Input.getText()).toString().trim().equals(Objects.requireNonNull(confirmPassword_Input.getText()).toString().trim()),
                        R.string.passwordNotMatch_Error)
                ) return;
                doRegistration();
            }
        });

        formFlipper.addOnLayoutChangeListener((view, newLeft, newTop, newRight, newBottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(formFlipper.getCurrentView().getId() == R.id.signUp_PersonalInfo_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.personalDetails));
                register_Button.setText(AppExtensions.getString(R.string.next));
                ((SignUpActivity) context).setOnBackPressListener(null);
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_Address_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.presentAddress));
                register_Button.setText(AppExtensions.getString(R.string.next));
                ((SignUpActivity) context).setOnBackPressListener(this);
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_Password_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.password));
                register_Button.setText(AppExtensions.getString(R.string.submit));
            }
        });

        photoUpload_Button.setOnClickListener(view ->
                PhotoActionFragment.show().setOnActionListener((dialog, isCapture) -> {
                    if (isCapture) captureByCamera();
                    else pickFromGallery(R.string.select_UserPhoto);
                    dialog.dismiss();
                }));

        phone_Input.setOnFocusChangeListener((v, hasFocus) ->
                phone_Input.setHint(AppExtensions.getString(hasFocus ? R.string.dummyContactNo : R.string.phone_Hint)));

        phone_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(AppExtensions.isNumberValidate(s.toString())){
                    AppExtensions.hideKeyboard(activity.getCurrentFocus());
                    phone_Input.clearFocus();
                }
            }
        });

        gender_Input.setOnTouchListener((v, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                new CustomPopup(v, AppExtensions.getStringArray(R.array.gender), CustomPopup.Popup.WINDOW)
                        .setOnPopupListener((position, item) -> {
                            switch (position){
                                case 0: gender_Input.setText(String.format("%s", "Male")); AppExtensions.clearError(v); break;
                                case 1: gender_Input.setText(String.format("%s", "Female")); AppExtensions.clearError(v); break;
                                case 2: gender_Input.setText(String.format("%s", "Other")); AppExtensions.clearError(v); break;
                                default:
                            }
                        });
            }
            return false;
        });

        maritalStatus_Input.setOnTouchListener((v, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                new CustomPopup(v, AppExtensions.getStringArray(R.array.maritalStatus), CustomPopup.Popup.WINDOW)
                        .setOnPopupListener((position, item) -> {
                            switch (position){
                                case 0: maritalStatus_Input.setText(String.format("%s", "Single")); AppExtensions.clearError(v); break;
                                case 1: maritalStatus_Input.setText(String.format("%s", "Married")); AppExtensions.clearError(v); break;
                                case 2: maritalStatus_Input.setText(String.format("%s", "Divorced")); AppExtensions.clearError(v); break;
                                case 3: maritalStatus_Input.setText(String.format("%s", "Widowed")); AppExtensions.clearError(v); break;
                                default:
                            }
                        });
            }
            return false;
        });

        birthDate_Input.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                DateExtensions.setDatePicker(birthDate_Input);
            }
            return false;
        });

        district_Input.setThreshold(1);
        district_Input.setAdapter(new DistrictsAdapter(context, AppExtensions.getDistricts()));

        district_Input.setOnItemClickListener((parent, view, position, id) -> {
            District district = (District) parent.getAdapter().getItem(position);
            upazila_Input.setThreshold(0);
            upazila_Input.setAdapter(new UpazilasAdapter(context, district.getUpazilas()));
            upazila_Input.setText(null);
            upazila_Input.requestFocus();
        });

        upazila_Input.setOnItemClickListener((parent, view, position, id) -> postalCode_Input.requestFocus());

        postalCode_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == (4)){
                    AppExtensions.hideKeyboard(activity.getCurrentFocus());
                    postalCode_Input.clearFocus();
                }
            }
        });

        currentLocation_Button.setOnClickListener(view -> {
            if (!new PermissionManager(PermissionManager.Permission.LOCATION, true).isGranted()) return;

            new GpsUtils(context).turnGPSOn(isGPSEnable -> {
                locationService = new MatrikaLocationService(context);
                Location gpsLocation = locationService.getLocation(LocationManager.GPS_PROVIDER);
                if(gpsLocation == null) return;

                Address address = LocationExtensions.getAddress(gpsLocation.getLatitude(), gpsLocation.getLongitude());
                address_Input.setText(address.getAddress());
                district_Input.setText(address.getDistrict());
                upazila_Input.setText(address.getUpazila());
                postalCode_Input.setText(address.getPostalCode());
                country_Input.setText(address.getCountry());
            });
        });

        confirmPassword_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = Objects.requireNonNull(password_Input.getText()).toString().trim();
                if(!TextUtils.isEmpty(password)) return;
                if(s.length() > 7 && s.toString().trim().equals(password)){
                    AppExtensions.hideKeyboardInDialog();
                    confirmPassword_Input.clearFocus();
                }
            }
        });

        password_Input.setOnDrawableClickListener(DrawablePosition.RIGHT, () -> {
            if (password_Input.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                password_Input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_input_password, 0, R.drawable.ic_password_visibility_off, 0);
                password_Input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                password_Input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_input_password, 0, R.drawable.ic_password_visibility_on, 0);
                password_Input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        confirmPassword_Input.setOnDrawableClickListener(DrawablePosition.RIGHT, () -> {
            if (confirmPassword_Input.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                confirmPassword_Input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_input_confirm_password, 0, R.drawable.ic_password_visibility_off, 0);
                confirmPassword_Input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else {
                confirmPassword_Input.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_input_confirm_password, 0, R.drawable.ic_password_visibility_on, 0);
                confirmPassword_Input.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void doRegistration(){
        String email = Objects.requireNonNull(email_Input.getText()).toString().trim();
        String password = Objects.requireNonNull(password_Input.getText()).toString().trim();

        progressDialog.setMessage(AppExtensions.getString(R.string.processing));
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                        if (photoFile != null) uploadUserPhoto(uid);
                        else storeUserData(uid, null);
                    }
                    else {
                        progressDialog.dismiss();
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            new CustomSnackBar(R.string.emailExist, R.string.retry, CustomSnackBar.Duration.LONG).show();
                        } else {
                            new CustomSnackBar(R.string.registrationFailed, R.string.retry, CustomSnackBar.Duration.LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    new CustomSnackBar(R.string.registrationFailed, R.string.retry, CustomSnackBar.Duration.LONG).show();
                });
    }

    private void uploadUserPhoto(final String uid){
        firebaseHelper.uploadPhoto(photoFile, Photo.PROFILE, new FirebaseHelper.OnPhotoUploadListener() {
            @Override
            public void onSuccess(String photoLink) {
                storeUserData(uid, photoLink);
            }

            @Override
            public void onFailure() {
                storeUserData(uid, null);
            }

            @Override
            public void onProgress(double progress) {
                progressDialog.setMessage((int)progress + "% " + AppExtensions.getString(R.string.complete));
            }
        });
    }

    /**
     * Store data to https://console.firebase.google.com/u/0/project/matrika-af9b1/firestore/data~2FUsers
     * {@link User} (Table data model)
     **/
    private void storeUserData(String uid, String photoLink){
        String name = Objects.requireNonNull(name_Input.getText()).toString().trim();
        String email = Objects.requireNonNull(email_Input.getText()).toString().trim();
        String phone = AppExtensions.getValidateNumber(phone_Input);
        String gender = Objects.requireNonNull(gender_Input.getText()).toString().trim();
        final String dob = Objects.requireNonNull(birthDate_Input.getText()).toString().trim();
        final Date birthDate = TextUtils.isEmpty(dob) ? null : new Date(new DateExtensions(dob).getBirthDate());
        final Integer age = TextUtils.isEmpty(dob) ? null : new DateExtensions(dob).getAge();
        String height = Objects.requireNonNull(height_Input.getText()).toString().trim();
        String weight = Objects.requireNonNull(weight_Input.getText()).toString().trim();
        String maritalStatus = Objects.requireNonNull(maritalStatus_Input.getText()).toString().trim();
        String address = Objects.requireNonNull(address_Input.getText()).toString().trim();
        String district = Objects.requireNonNull(district_Input.getText()).toString().trim();
        String upazila = Objects.requireNonNull(upazila_Input.getText()).toString().trim();
        String postalCode = Objects.requireNonNull(postalCode_Input.getText()).toString().trim();
        String country = Objects.requireNonNull(country_Input.getText()).toString().trim();

        User patient = new User();
        patient.setId(uid);
        patient.setRole(Role.PATIENT.getId());
        patient.setToken(null);
        patient.setRegisteredAt(new Date(System.currentTimeMillis()));
        patient.setProfileCompleted(!TextUtils.isEmpty(dob) && !TextUtils.isEmpty(height) && !TextUtils.isEmpty(weight) && photoLink != null);
        patient.setProfileReviewed(true);
        patient.setProfileVerified(true);
        patient.setPhoneVerified(false);
        patient.setEmailVerified(false);
        patient.setActive(new Status(true, new Date(System.currentTimeMillis())));
        patient.setPhoto(photoLink);
        patient.setName(name);
        patient.setEmail(email);
        patient.setPhone(phone);
        patient.setGender(gender);
        patient.setDateOfBirth(birthDate);
        patient.setAge(age);
        patient.setHeight(TextUtils.isEmpty(height) ? null : Double.parseDouble(height));
        patient.setWeight(TextUtils.isEmpty(weight) ? null : Double.parseDouble(weight));
        patient.setMaritalStatus(maritalStatus);
        patient.setAddress(new Address(address, country, district, upazila, postalCode));

        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance().collection(FirebaseHelper.USERS_TABLE).document(uid).set(patient),
                new FirebaseHelper.OnFirebaseUpdateListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        launchNewActivity(patient);
                    }

                    @Override
                    public void onFailure() { progressDialog.dismiss(); }

                    @Override
                    public void onCancelled() { progressDialog.dismiss(); }
                });
    }

    private void launchNewActivity(User patient) {
        pm.setUserMode(Role.PATIENT);
        pm.setSignInData(PreferenceManager.USER_INFO_SP_KEY, new Gson().toJson(patient));
        pm.setSignInData(PreferenceManager.USER_EMAIL_SP_KEY, null);
        pm.setSignInData(PreferenceManager.USER_PASSWORD_SP_KEY, null);
        pm.setSignInData(PreferenceManager.USER_REMEMBER_SP_KEY, false);

        Intent intent = new Intent(activity, PatientHomeActivity.class);
        intent.putExtra(Constants.SUCCESS_KEY, "SUCCESS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), data.getData());
                if(mBitmap == null) return;
                userPhoto.setImageBitmap(mBitmap);
                photoFile = AppExtensions.getBitmapBytes(mBitmap, 1024);
            }
            catch (IOException ex) {
                new CustomSnackBar(ex.getMessage(), R.string.failureToUpload, CustomSnackBar.Duration.LONG).show();
                ex.printStackTrace();
            }
        }
        else if(requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                if(photoFile == null) return;
                userPhoto.setImageURI((Uri) photoFile);
            }
            catch (Exception ex){
                new CustomSnackBar(R.string.failureToUpload, R.string.retry, CustomSnackBar.Duration.LONG).show();
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

    private void captureByCamera() {
        if (!new PermissionManager(PermissionManager.Permission.CAMERA, true, response -> captureByCamera() ).isGranted()) return;
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        java.io.File file = new java.io.File(activity.getExternalCacheDir(), (UUID.randomUUID() + ".jpg"));
        if (file.exists()) file.delete();
        photoFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new java.io.File(String.valueOf(file)));
        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, (Uri) photoFile);
        startActivityForResult(takePicture, Constants.CAMERA_REQUEST_CODE);
    }

    @Override
    public void goBack() {
        if (formFlipper.getCurrentView().getId() == R.id.signUp_Password_Layout
                || formFlipper.getCurrentView().getId() == R.id.signUp_Address_Layout) formFlipper.showPrevious();
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