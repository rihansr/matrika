package com.telemedicine.matrika.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.adapter.HomeSpecialtiesAdapter;
import com.telemedicine.matrika.api.API;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.fragment.AboutFragment;
import com.telemedicine.matrika.fragment.AlertDialogFragment;
import com.telemedicine.matrika.fragment.ChatMessagesFragment;
import com.telemedicine.matrika.fragment.DeviceDataFragment;
import com.telemedicine.matrika.fragment.DeviceInfoFragment;
import com.telemedicine.matrika.fragment.LoadingFragment;
import com.telemedicine.matrika.fragment.PatientDoctorsFragment;
import com.telemedicine.matrika.fragment.PatientChatGroupsFragment;
import com.telemedicine.matrika.fragment.PatientProfileFragment;
import com.telemedicine.matrika.fragment.PatientReportsFragment;
import com.telemedicine.matrika.fragment.AllUsersFragment;
import com.telemedicine.matrika.fragment.SettingsFragment;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.model.chat.Group;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Report;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.wiget.CircleImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static android.graphics.Color.TRANSPARENT;

public class PatientHomeActivity extends AppBaseActivity {

    /** Root Layouts **/
    private DrawerLayout            drawer_Layout;
    private LinearLayoutCompat      content_Layout;
    private NavigationView          navigation_Layout;

    /** Toolbar **/
    private AppCompatImageView      toolbar_Menu_Button;

    /** Content **/
    private AppCompatTextView       doctor_Search_Tv;
    private RecyclerView            rcv_Specialties;
    private HomeSpecialtiesAdapter  specialtiesAdapter;

    /** Drawer **/
    private CircleImageView         nav_UserPhoto;
    private AppCompatTextView       nav_UserName;
    private AppCompatTextView       nav_UserEmail;
    private AppCompatTextView       nav_Profile;
    private AppCompatTextView       nav_Doctors;
    private AppCompatTextView       nav_Chats;
    private AppCompatTextView       nav_Reports;
    private AppCompatTextView       nav_Device;
    private AppCompatTextView       nav_SignOut;
    private AppCompatTextView       nav_Settings;
    private AppCompatTextView       nav_About;
    private AppCompatTextView       nav_Share;

    /** Other **/
    private FirebaseHelper          firebaseHelper;
    private LoadingFragment         loading;
    private final PreferenceManager pm = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppExtensions.fullScreenActivity(getWindow(), false);
        setContentView(R.layout.activity_patient_home);
        init();
    }

    private void init(){
        initId();

        setDrawer();

        setAdapter();

        setProfileInfo();

        setNotificationData();

        /**
         * Check Notification Allowed or not
         **/
        if (!pm.isNotificationPermissionForbidded() && !NotificationManagerCompat.from(this).areNotificationsEnabled()) showNotificationDialog();

        doctor_Search_Tv.setOnClickListener(view -> AllUsersFragment.show(Role.DOCTOR));

        specialtiesAdapter.setSpecialties(LocalStorage.specialties);
        this.setOnSpecialtyListener(specialties -> specialtiesAdapter.setSpecialties(LocalStorage.specialties));
    }

    private void initId() {
        drawer_Layout = findViewById(R.id.home_Drawer_Layout);
        content_Layout = findViewById(R.id.home_Content_Layout);
        navigation_Layout = findViewById(R.id.home_Navigation_Layout);

        toolbar_Menu_Button = findViewById(R.id.home_Toolbar_Menu_Button);

        doctor_Search_Tv = findViewById(R.id.home_Search_Tv);
        rcv_Specialties = findViewById(R.id.home_Specialties_Rcv);

        nav_UserPhoto = findViewById(R.id.home_Nav_UserPhoto_Iv);
        nav_UserName = findViewById(R.id.home_Nav_UserName_Tv);
        nav_UserEmail = findViewById(R.id.home_Nav_UserMail_Tv);
        nav_Profile = findViewById(R.id.home_Nav_Profile);
        nav_Doctors = findViewById(R.id.home_Nav_Doctors);
        nav_Chats = findViewById(R.id.home_Nav_Chats);
        nav_Reports = findViewById(R.id.home_Nav_Reports);
        nav_Device = findViewById(R.id.home_Nav_Device);
        nav_SignOut = findViewById(R.id.home_Nav_SignOut);
        nav_Settings = findViewById(R.id.home_Nav_Settings);
        nav_About = findViewById(R.id.home_Nav_About);
        nav_Share = findViewById(R.id.home_Nav_Share);

        firebaseHelper = new FirebaseHelper();
    }

    private void setProfileInfo(){
        AppExtensions.loadPhoto(nav_UserPhoto, LocalStorage.USER.getPhoto(), R.dimen.icon_Size_XXXXX_Large, R.drawable.ic_avatar);

        nav_UserPhoto.setOnClickListener(view -> PatientProfileFragment.show(LocalStorage.USER)
                .setOnProfileUpdateListener(isUpdated -> {
                    if (isUpdated) setProfileInfo();
                })
        );

        nav_UserName.setText(LocalStorage.USER.getName());
        nav_UserEmail.setText(LocalStorage.USER.getEmail());
    }

    void setAdapter() {
        specialtiesAdapter = new HomeSpecialtiesAdapter();
        rcv_Specialties.setAdapter(specialtiesAdapter);
        specialtiesAdapter.setOnSpecialtySelectListener(specialty -> AllUsersFragment.show(Role.DOCTOR, specialty));
    }

    private void setDrawer() {
        toolbar_Menu_Button.setOnClickListener(v -> drawer_Layout.openDrawer(GravityCompat.START));

        drawer_Layout.setScrimColor(Color.TRANSPARENT);
        drawer_Layout.setDrawerElevation(0);
        drawer_Layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                float width = navigation_Layout.getWidth() * slideOffset;
                content_Layout.setX(width);
                content_Layout.invalidate();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        nav_Profile.setOnClickListener(view -> PatientProfileFragment.show(LocalStorage.USER)
                .setOnProfileUpdateListener(isUpdated -> {
                    if (isUpdated) setProfileInfo();
                })
        );
        nav_Doctors.setOnClickListener(view -> PatientDoctorsFragment.show());
        nav_Chats.setOnClickListener(view -> PatientChatGroupsFragment.show());
        nav_Reports.setOnClickListener(view -> PatientReportsFragment.show(Report.MEDICAL_REPORT, LocalStorage.USER.getId()));

        nav_Reports.setOnClickListener(view ->
                AlertDialogFragment.show(R.string.reports, R.string.chooseAction, R.string.medicalReports, R.string.deviceReports)
                        .setOnDialogListener(new AlertDialogFragment.OnDialogListener() {
                            @Override
                            public void onLeftButtonClick() {
                                PatientReportsFragment.show(Report.MEDICAL_REPORT, LocalStorage.USER.getId());
                            }

                            @Override
                            public void onRightButtonClick() {
                                PatientReportsFragment.show(Report.DEVICE_REPORT, LocalStorage.USER.getId());
                            }
                        })
        );

        nav_Device.setOnClickListener(view -> {
                    if (LocalStorage.USER.getChannelId() == null || LocalStorage.USER.getDeviceKey() == null) {
                        DeviceInfoFragment.show().setOnDeviceInfoListener((id, key) -> {
                            API.CHANNEL_ID = id;
                            API.READ_API_KEY = key;
                            DeviceDataFragment.show();
                        });
                    }
                    else {
                        AlertDialogFragment.show(R.string.iotDevice, R.string.chooseAction, R.string.addNew, R.string.viewData)
                                .setOnDialogListener(new AlertDialogFragment.OnDialogListener() {
                                    @Override
                                    public void onLeftButtonClick() {
                                        DeviceInfoFragment.show().setOnDeviceInfoListener((id, key) -> {
                                            API.CHANNEL_ID = id;
                                            API.READ_API_KEY = key;
                                            DeviceDataFragment.show();
                                        });
                                    }

                                    @Override
                                    public void onRightButtonClick() {
                                        API.CHANNEL_ID = LocalStorage.USER.getChannelId();
                                        API.READ_API_KEY = LocalStorage.USER.getDeviceKey();
                                        DeviceDataFragment.show();
                                    }
                                });
                    }
                }
        );

        nav_SignOut.setOnClickListener(v -> {
            loading = LoadingFragment.show();
            HashMap<String, Object> activeStatus = new HashMap<>();
            activeStatus.put(Status.STATUS, false);
            activeStatus.put(Status.DATE, new Date());

            firebaseHelper.setDocumentData(FirebaseFirestore.getInstance()
                    .collection(FirebaseHelper.USERS_TABLE)
                    .document(LocalStorage.USER.getId())
                    .update(User.ACTIVE, activeStatus), new FirebaseHelper.OnFirebaseUpdateListener() {
                @Override
                public void onSuccess() {
                    AppExtensions.dismissLoading(loading);

                    FirebaseAuth.getInstance().signOut();
                    LocalStorage.setUserInfo(null, true);

                    Intent intent = new Intent(PatientHomeActivity.this, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                @Override
                public void onFailure() { AppExtensions.dismissLoading(loading); }

                @Override
                public void onCancelled() { AppExtensions.dismissLoading(loading); }
            });
        });

        nav_Settings.setOnClickListener(view -> SettingsFragment.show()
                .setOnSettingsUpdateListener(isUpdated -> {
                    if (isUpdated) setProfileInfo();
                })
        );
        nav_About.setOnClickListener(view -> AboutFragment.show());
        nav_Share.setOnClickListener(view -> AppExtensions.shareApk());
    }

    private void setNotificationData() {
        if(getIntent().hasExtra(Constants.USER_BUNDLE_KEY)) {
            User user = (User) getIntent().getSerializableExtra(Constants.USER_BUNDLE_KEY);
            getIntent().removeExtra(Constants.USER_BUNDLE_KEY);
            Group group = (Group) getIntent().getSerializableExtra(Constants.CHAT_GROUP_BUNDLE_KEY);
            getIntent().removeExtra(Constants.CHAT_GROUP_BUNDLE_KEY);

            ChatMessagesFragment.show(user, group, null);
        }
    }

    public void showNotificationDialog(){
        View view = LayoutInflater.from(PatientHomeActivity.this).inflate(R.layout.layout_notification_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PatientHomeActivity.this);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(alertDialog.getWindow()).getDecorView().setBackgroundColor(TRANSPARENT);
        alertDialog.setCancelable(false);
        alertDialog.show();

        final AppCompatCheckBox dontAsk_Checkbox = view.findViewById(R.id.notification_DontAsk_CheckBox);
        final AppCompatButton allow_Button = view.findViewById(R.id.notification_Allow_Button);
        final AppCompatButton forbid_Button = view.findViewById(R.id.notification_Forbid_Button);

        dontAsk_Checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> pm.setForbidNotificationPermission(isChecked));

        allow_Button.setOnClickListener(v -> {
            alertDialog.dismiss();
            new PermissionManager().goToNotificationPermissionSetting();
        });

        forbid_Button.setOnClickListener(v -> alertDialog.dismiss());
    }
}