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
import android.os.Environment;
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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.hootsuite.nachos.NachoTextView;
import com.telemedicine.matrika.BuildConfig;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.activity.DoctorHomeActivity;
import com.telemedicine.matrika.activity.SignUpActivity;
import com.telemedicine.matrika.adapter.DistrictsAdapter;
import com.telemedicine.matrika.adapter.SpecialistAdapter;
import com.telemedicine.matrika.adapter.SpecialtyAdapter;
import com.telemedicine.matrika.adapter.UpazilasAdapter;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.model.address.Address;
import com.telemedicine.matrika.model.address.District;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.model.specialty.Field;
import com.telemedicine.matrika.model.specialty.Specialty;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.service.MatrikaLocationService;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.CustomPopup;
import com.telemedicine.matrika.util.CustomSnackBar;
import com.telemedicine.matrika.util.GpsUtils;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.DrawablePosition;
import com.telemedicine.matrika.util.enums.Photo;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.util.extensions.DateExtensions;
import com.telemedicine.matrika.util.extensions.LocationExtensions;
import com.telemedicine.matrika.util.interfaces.OnBackPressListener;
import com.telemedicine.matrika.wiget.CircleImageView;
import com.telemedicine.matrika.wiget.ClickableEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ClickableViewAccessibility")
public class SignUpDoctorFragment extends Fragment implements OnBackPressListener {

    private Context                         context;
    private Activity                        activity;

    /** Personal Info **/
    private View                            rootView;
    private RelativeLayout                  photoUpload_Button;
    private CircleImageView                 user_Photo;
    private AppCompatEditText               name_Input;
    private AppCompatEditText               email_Input;
    private AppCompatEditText               phone_Input;
    private AppCompatEditText               gender_Input;
    private AppCompatEditText               birthDate_Input;
    private AppCompatEditText               nid_Input;
    private FrameLayout                     nidUpload_Button;
    private CircleImageView                 nid_Photo;

    /** Academic Info **/
    private NachoTextView                   languages_Input;
    private NachoTextView                   qualifications_Input;
    private AppCompatEditText               specialty_Input;
    private Specialty                       selectedSpecialty;
    private NachoTextView                   specialist_Input;
    private AppCompatEditText               bmdc_Input;

    /** Professional Info **/
    private AppCompatEditText               bio_Input;
    private AppCompatEditText               experienceYears_Input;
    private AppCompatEditText               consultPatientsNo_Input;
    private AppCompatEditText               consultationTime_Input;
    private AppCompatEditText               fees_Input;
    private AppCompatEditText               awards_Input;

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
    private Photo                           selectedPhoto = null;
    private Object                          userPhotoFile = null;
    private Object                          nidPhotoFile = null;
    private FirebaseHelper                  firebaseHelper;
    private final PreferenceManager         pm = new PreferenceManager();
    private ProgressDialog                  progressDialog;

    public SignUpDoctorFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign_up_doctor, container, false);

        initId();

        init();

        return rootView;
    }

    private void initId() {
        photoUpload_Button = rootView.findViewById(R.id.signUp_UploadPhoto_Button);
        user_Photo = rootView.findViewById(R.id.signUp_UserPhoto_Iv);

        name_Input = rootView.findViewById(R.id.signUp_Name_Input);
        email_Input = rootView.findViewById(R.id.signUp_Email_Input);
        phone_Input = rootView.findViewById(R.id.signUp_Phone_Input);
        phone_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        gender_Input = rootView.findViewById(R.id.signUp_Gender_Input);
        gender_Input.setInputType(InputType.TYPE_NULL);
        birthDate_Input = rootView.findViewById(R.id.signUp_BirthDate_Input);
        birthDate_Input.setInputType(InputType.TYPE_NULL);
        nid_Input = rootView.findViewById(R.id.signUp_NID_Input);
        nid_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        nidUpload_Button = rootView.findViewById(R.id.signUp_UploadNid_Button);
        nid_Photo = rootView.findViewById(R.id.signUp_NidPhoto_Iv);

        languages_Input = rootView.findViewById(R.id.signUp_Languages_Input);
        qualifications_Input = rootView.findViewById(R.id.signUp_Qualifications_Input);
        specialty_Input = rootView.findViewById(R.id.signUp_Specialty_Input);
        specialty_Input.setInputType(InputType.TYPE_NULL);
        specialist_Input = rootView.findViewById(R.id.signUp_Specialists_Input);
        bmdc_Input = rootView.findViewById(R.id.signUp_BmDc_Input);
        bmdc_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());

        bio_Input = rootView.findViewById(R.id.signUp_Bio_Input);
        experienceYears_Input = rootView.findViewById(R.id.signUp_ExperienceYears_Input);
        experienceYears_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        consultPatientsNo_Input = rootView.findViewById(R.id.signUp_ConsultPatientsNo_Input);
        consultPatientsNo_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        consultationTime_Input = rootView.findViewById(R.id.signUp_ConsultationTime_Input);
        consultationTime_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        fees_Input = rootView.findViewById(R.id.signUp_Fees_Input);
        fees_Input.setTransformationMethod(new AppExtensions.NumericKeyBoardTransformationMethod());
        awards_Input = rootView.findViewById(R.id.signUp_Awards_Input);

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
                        || !AppExtensions.isInputValid(nid_Input, R.string.nid_Error)
                        || !AppExtensions.isInputValid(nid_Input, nidPhotoFile == null, R.string.nidPhoto_Error)
                ) return;
                formFlipper.showNext();
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_AcademicInfo_Layout){
                languages_Input.getAllChips();
                if (!AppExtensions.isInputValid(languages_Input, languages_Input.getAllChips().size() == 0, R.string.languages_Error)
                        || !AppExtensions.isInputValid(qualifications_Input, qualifications_Input.getAllChips().size() == 0, R.string.qualifications_Error)
                        || !AppExtensions.isInputValid(specialty_Input, R.string.specialty_Error)
                        || !AppExtensions.isInputValid(specialist_Input, specialist_Input.getVisibility() == View.VISIBLE && specialist_Input.getAllChips().size() == 0, R.string.specialist_Error)
                        || !AppExtensions.isInputValid(bmdc_Input, R.string.bmdc_Error)
                ) return;
                formFlipper.showNext();
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_ProfessionalInfo_Layout){
                if (!AppExtensions.isInputValid(consultPatientsNo_Input, R.string.consultPatientNo_Error)
                        || !AppExtensions.isInputValid(consultationTime_Input, R.string.consultationTime_Error)
                        || !AppExtensions.isInputValid(fees_Input, R.string.fees_Error)
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
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_AcademicInfo_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.academicDetails));
                register_Button.setText(AppExtensions.getString(R.string.next));
                ((SignUpActivity) context).setOnBackPressListener(this);
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_ProfessionalInfo_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.professionalDetails));
                register_Button.setText(AppExtensions.getString(R.string.next));
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_Address_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.practiceAddress));
                register_Button.setText(AppExtensions.getString(R.string.next));
            }
            else if(formFlipper.getCurrentView().getId() == R.id.signUp_Password_Layout){
                title_Tv.setText(AppExtensions.getString(R.string.password));
                register_Button.setText(AppExtensions.getString(R.string.submit));
            }
        });

        photoUpload_Button.setOnClickListener(view ->
                PhotoActionFragment.show().setOnActionListener((dialog, isCapture) -> {
                    selectedPhoto = Photo.PROFILE;
                    if (isCapture) dispatchTakePictureIntent();
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

        birthDate_Input.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                DateExtensions.setDatePicker(birthDate_Input);
            }
            return false;
        });

        nidUpload_Button.setOnClickListener(view ->
                PhotoActionFragment.show().setOnActionListener((dialog, isCapture) -> {
                    selectedPhoto = Photo.NID;
                    if (isCapture) dispatchTakePictureIntent();
                    else pickFromGallery(R.string.select_NidPhoto);
                    dialog.dismiss();
                })
        );

        languages_Input.setThreshold(0);
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<>(context, R.layout.sample_spinner, AppExtensions.getStringArray(R.array.languages));
        languages_Input.setAdapter(languagesAdapter);

        qualifications_Input.setThreshold(0);
        ArrayAdapter<String> qualificationsAdapter = new ArrayAdapter<>(context, R.layout.sample_spinner, AppExtensions.getStringArray(R.array.qualifications));
        qualifications_Input.setAdapter(qualificationsAdapter);

        specialty_Input.setOnTouchListener((v, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                new CustomPopup(v, new SpecialtyAdapter(context, LocalStorage.specialties), 500, CustomPopup.Popup.WINDOW)
                        .setOnItemClickListener((specialtyParent, specialtyView, specialtyPos, specialtyId) -> {
                            selectedSpecialty = (Specialty) specialtyParent.getAdapter().getItem(specialtyPos);
                            specialty_Input.setText(selectedSpecialty.getSpecialty());
                            specialist_Input.setText(new ArrayList<>());

                            if(selectedSpecialty.getFields() == null){
                                specialist_Input.setVisibility(View.GONE);
                                if (TextUtils.isEmpty(Objects.requireNonNull(bmdc_Input.getText()).toString().trim()))
                                    bmdc_Input.requestFocus();
                                return;
                            }
                            specialist_Input.setVisibility(View.VISIBLE);
                            specialist_Input.requestFocus();
                            specialist_Input.setThreshold(0);
                            ArrayAdapter<Field> specialistsAdapter = new SpecialistAdapter(context, new ArrayList<>(selectedSpecialty.getFields().values()));
                            specialist_Input.setAdapter(specialistsAdapter);
                        });
            }
            return false;
        });

        bmdc_Input.setOnFocusChangeListener((v, hasFocus) ->
                bmdc_Input.setHint(AppExtensions.getString(hasFocus ? R.string.dummyBmdcNo : R.string.bmdc_Hint)));

        bmdc_Input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 6){
                    AppExtensions.hideKeyboard(activity.getCurrentFocus());
                    bmdc_Input.clearFocus();
                }
            }
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
                        if (userPhotoFile != null) uploadUserPhoto(uid);
                        else if(nidPhotoFile != null) uploadNidPhoto(uid, null);
                        else storeUserData(uid, null, null);
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
        firebaseHelper.uploadPhoto(userPhotoFile, Photo.PROFILE, new FirebaseHelper.OnPhotoUploadListener() {
            @Override
            public void onSuccess(String userPhotoLink) {
                uploadNidPhoto(uid, userPhotoLink);
            }

            @Override
            public void onFailure() {
                uploadNidPhoto(uid, null);
            }

            @Override
            public void onProgress(double progress) {
                progressDialog.setMessage((int)(nidPhotoFile == null ? progress : (progress/2)) + "% " + AppExtensions.getString(R.string.complete));
            }
        });
    }

    private void uploadNidPhoto(final String uid, String userPhotoLink){
        firebaseHelper.uploadPhoto(nidPhotoFile, Photo.NID, new FirebaseHelper.OnPhotoUploadListener() {
            @Override
            public void onSuccess(String nidPhotoLink) {
                storeUserData(uid, userPhotoLink, nidPhotoLink);
            }

            @Override
            public void onFailure() {
                storeUserData(uid, userPhotoLink, null);
            }

            @Override
            public void onProgress(double progress) {
                progressDialog.setMessage((int)(userPhotoFile == null ? progress : (50 + (progress/2))) + "% " + AppExtensions.getString(R.string.complete));
            }
        });
    }

    /**
     * Store data to https://console.firebase.google.com/u/0/project/matrika-af9b1/firestore/data~2Fusers
     * {@link User} (Table data model)
     **/
    private void storeUserData(String uid, String userPhotoLink, String nidPhotoLink){
        String name = Objects.requireNonNull(name_Input.getText()).toString().trim();
        String email = Objects.requireNonNull(email_Input.getText()).toString().trim();
        String phone = AppExtensions.getValidateNumber(phone_Input);
        String gender = Objects.requireNonNull(gender_Input.getText()).toString().trim();
        final String dob = Objects.requireNonNull(birthDate_Input.getText()).toString().trim();
        final Date birthDate = TextUtils.isEmpty(dob) ? null : new Date(new DateExtensions(dob).getBirthDate());
        final Integer age = TextUtils.isEmpty(dob) ? null : new DateExtensions(dob).getAge();
        String nid = Objects.requireNonNull(nid_Input.getText()).toString().trim();

        String bmdc = Objects.requireNonNull(bmdc_Input.getText()).toString().trim();

        String bio = Objects.requireNonNull(bio_Input.getText()).toString().trim();
        String experienceYears = Objects.requireNonNull(experienceYears_Input.getText()).toString().trim();
        String consultPatientsNo = Objects.requireNonNull(consultPatientsNo_Input.getText()).toString().trim();
        String consultationTime = Objects.requireNonNull(consultationTime_Input.getText()).toString().trim();
        String fees = Objects.requireNonNull(fees_Input.getText()).toString().trim();
        String awards = Objects.requireNonNull(awards_Input.getText()).toString().trim();

        String address = Objects.requireNonNull(address_Input.getText()).toString().trim();
        String district = Objects.requireNonNull(district_Input.getText()).toString().trim();
        String upazila = Objects.requireNonNull(upazila_Input.getText()).toString().trim();
        String postalCode = Objects.requireNonNull(postalCode_Input.getText()).toString().trim();
        String country = Objects.requireNonNull(country_Input.getText()).toString().trim();

        selectedSpecialty.setFields(null);
        selectedSpecialty.setDescription(null);
        selectedSpecialty.setSpecialists(LocalStorage.getChips(specialist_Input.getAllChips()));

        User doctor = new User();
        doctor.setId(uid);
        doctor.setRole(Role.DOCTOR.getId());
        doctor.setToken(null);
        doctor.setActive(new Status(true, new Date()));
        doctor.setProfileCompleted(!TextUtils.isEmpty(dob) && userPhotoLink != null && nidPhotoLink != null);
        doctor.setProfileReviewed(false);
        doctor.setProfileVerified(false);
        doctor.setPhoneVerified(false);
        doctor.setEmailVerified(false);
        doctor.setRating(0.0);
        doctor.setFollowers(0);
        doctor.setRegisteredAt(new Date());
        doctor.setPhoto(userPhotoLink);
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPhone(phone);
        doctor.setGender(gender);
        doctor.setDateOfBirth(birthDate);
        doctor.setAge(age);
        doctor.setNid(nid);
        doctor.setNidPhoto(nidPhotoLink);
        doctor.setLanguages(LocalStorage.getChips(languages_Input.getAllChips()));
        doctor.setQualifications(LocalStorage.getChips(qualifications_Input.getAllChips()));
        doctor.setSpecialty(selectedSpecialty);
        doctor.setBmDcNo(bmdc);
        doctor.setBio(bio);
        doctor.setExperienceYears(TextUtils.isEmpty(experienceYears) ? 0 : Integer.parseInt(experienceYears));
        doctor.setConsultPatientsNo(Integer.parseInt(consultPatientsNo));
        doctor.setConsultationTime(Integer.parseInt(consultationTime));
        doctor.setConsultationTime(Integer.parseInt(consultationTime));
        doctor.setFees(Integer.parseInt(fees));
        doctor.setAwards(awards);
        doctor.setAddress(new Address(address, country, district, upazila, postalCode));

        firebaseHelper.setDocumentData(FirebaseFirestore.getInstance().collection(FirebaseHelper.USERS_TABLE).document(uid).set(doctor),
                new FirebaseHelper.OnFirebaseUpdateListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        launchNewActivity(doctor);
                    }

                    @Override
                    public void onFailure() { progressDialog.dismiss(); }

                    @Override
                    public void onCancelled() { progressDialog.dismiss(); }
                });
    }

    private void launchNewActivity(User doctor) {
        pm.setUserMode(Role.DOCTOR);
        pm.setSignInData(PreferenceManager.USER_INFO_SP_KEY, new Gson().toJson(doctor));
        pm.setSignInData(PreferenceManager.USER_EMAIL_SP_KEY, null);
        pm.setSignInData(PreferenceManager.USER_PASSWORD_SP_KEY, null);
        pm.setSignInData(PreferenceManager.USER_REMEMBER_SP_KEY, false);

        Intent intent = new Intent(activity, DoctorHomeActivity.class);
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

                switch (selectedPhoto){
                    case PROFILE:
                        user_Photo.setImageBitmap(mBitmap);
                        userPhotoFile = AppExtensions.getBitmapBytes(mBitmap, 1024);
                        break;

                    case NID:
                        nid_Photo.setImageBitmap(mBitmap);
                        nidPhotoFile = AppExtensions.getBitmapBytes(mBitmap, 1024);
                        break;

                    default:
                }
            }
            catch (IOException ex) {
                new CustomSnackBar(R.string.failureToUpload, R.string.retry, CustomSnackBar.Duration.LONG).show();
                ex.printStackTrace();
            }
        }
        else if(requestCode == Constants.CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                switch (selectedPhoto){
                    case PROFILE:
                        if(userPhotoFile == null) return;
                        Bitmap userPhotoBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile((File) userPhotoFile));
                        if(userPhotoBitmap == null) return;
                        user_Photo.setImageBitmap(userPhotoBitmap);
                        break;

                    case NID:
                        if(nidPhotoFile == null) return;
                        Bitmap nidPhotoBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile((File) nidPhotoFile));
                        if(nidPhotoBitmap == null) return;
                        nid_Photo.setImageBitmap(nidPhotoBitmap);
                        break;

                    default:
                }
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
        switch (selectedPhoto){
            case PROFILE:
                userPhotoFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new java.io.File(String.valueOf(file)));
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, (Uri) userPhotoFile);
                break;

            case NID:
                nidPhotoFile = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", new java.io.File(String.valueOf(file)));
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, (Uri) nidPhotoFile);
                break;

            default:
        }
        startActivityForResult(takePicture, Constants.CAMERA_REQUEST_CODE);
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        if (!new PermissionManager(PermissionManager.Permission.CAMERA, true, response -> dispatchTakePictureIntent() ).isGranted()) return;
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePicture.resolveActivity(activity.getPackageManager()) != null) {
            java.io.File photoFile = null;
            try { photoFile = createImageFile(); }
            catch (IOException ex) { ex.printStackTrace(); }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(activity, (BuildConfig.APPLICATION_ID + ".fileprovider"), photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicture, Constants.CAMERA_REQUEST_CODE);
            }
        }
    }

    private java.io.File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        java.io.File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        java.io.File image = java.io.File.createTempFile(imageFileName, ".jpg", storageDir);
        switch (selectedPhoto){
            case PROFILE: userPhotoFile = new java.io.File(image.getAbsolutePath()); break;
            case NID: nidPhotoFile = new java.io.File(image.getAbsolutePath()); break;
            default:
        }
        return image;
    }

    @Override
    public void goBack() {
        if (formFlipper.getCurrentView().getId() == R.id.signUp_Password_Layout
                || formFlipper.getCurrentView().getId() == R.id.signUp_Address_Layout
                || formFlipper.getCurrentView().getId() == R.id.signUp_ProfessionalInfo_Layout
                || formFlipper.getCurrentView().getId() == R.id.signUp_AcademicInfo_Layout) formFlipper.showPrevious();
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