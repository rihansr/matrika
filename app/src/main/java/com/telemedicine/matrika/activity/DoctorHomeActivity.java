package com.telemedicine.matrika.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.fragment.AboutFragment;
import com.telemedicine.matrika.fragment.ChatMessagesFragment;
import com.telemedicine.matrika.fragment.DoctorProfileFragment;
import com.telemedicine.matrika.fragment.LoadingFragment;
import com.telemedicine.matrika.fragment.SettingsFragment;
import com.telemedicine.matrika.helper.FirebaseHelper;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.model.chat.Group;
import com.telemedicine.matrika.model.other.Status;
import com.telemedicine.matrika.model.user.User;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.wiget.CircleImageView;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import static android.graphics.Color.TRANSPARENT;

@SuppressLint("NonConstantResourceId")
public class DoctorHomeActivity extends AppBaseActivity {

    /** Root Layouts **/
    private DrawerLayout            drawer_Layout;
    private LinearLayoutCompat      content_Layout;
    private NavigationView          navigation_Layout;

    /** Toolbar **/
    private CircleImageView         toolbar_UserPhoto;
    public static AppCompatTextView toolbar_title;

    /** Drawer **/
    private CircleImageView         nav_UserPhoto;
    private AppCompatTextView       nav_UserName;
    private AppCompatTextView       nav_UserEmail;
    private AppCompatTextView       nav_Profile;
    private AppCompatTextView       nav_SignOut;
    private AppCompatTextView       nav_Settings;
    private AppCompatTextView       nav_About;
    private AppCompatTextView       nav_Share;
    private BottomNavigationView    navView;
    private NavController           navController;

    /** Other **/
    private LoadingFragment         loading;
    private final PreferenceManager pm = new PreferenceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppExtensions.fullScreenActivity(getWindow(), false);
        setContentView(R.layout.activity_doctor_home);
        init();
    }

    private void init(){
        initId();

        setBottomNavigation();

        setDrawer();

        setProfileInfo();

        setNotificationData();

        /**
         * Check Notification Allowed or not
         **/
        if (!pm.isNotificationPermissionForbidded() && !NotificationManagerCompat.from(this).areNotificationsEnabled()) showNotificationDialog();
    }

    private void initId() {
        drawer_Layout = findViewById(R.id.home_Drawer_Layout);
        content_Layout = findViewById(R.id.home_Content_Layout);
        navigation_Layout = findViewById(R.id.home_Navigation_Layout);

        toolbar_UserPhoto = findViewById(R.id.home_Toolbar_UserPhoto_Iv);
        toolbar_title = findViewById(R.id.home_Toolbar_Title_Tv);

        nav_UserPhoto = findViewById(R.id.home_Nav_UserPhoto_Iv);
        nav_UserName = findViewById(R.id.home_Nav_UserName_Tv);
        nav_UserEmail = findViewById(R.id.home_Nav_UserMail_Tv);
        nav_Profile = findViewById(R.id.home_Nav_Profile);
        nav_SignOut = findViewById(R.id.home_Nav_SignOut);
        nav_Settings = findViewById(R.id.home_Nav_Settings);
        nav_About = findViewById(R.id.home_Nav_About);
        nav_Share = findViewById(R.id.home_Nav_Share);

        navView = findViewById(R.id.home_Nav_View);
        navController = Navigation.findNavController(this, R.id.home_Nav_Host_Fragment);
    }

    private void setProfileInfo(){
        AppExtensions.loadPhoto(toolbar_UserPhoto, LocalStorage.USER.getPhoto(), R.dimen.icon_Size_Medium, R.drawable.ic_avatar);
        toolbar_UserPhoto.setOnClickListener(view -> DoctorProfileFragment.show(LocalStorage.USER)
                .setOnProfileUpdateListener(isUpdated -> {
                    if (isUpdated) setProfileInfo();
                })
        );

        AppExtensions.loadPhoto(nav_UserPhoto, LocalStorage.USER.getPhoto(), R.dimen.icon_Size_XXXXX_Large, R.drawable.ic_avatar);
        nav_UserPhoto.setOnClickListener(view -> DoctorProfileFragment.show(LocalStorage.USER)
                .setOnProfileUpdateListener(isUpdated -> {
                    if (isUpdated) setProfileInfo();
                })
        );

        nav_UserName.setText(LocalStorage.USER.getName());
        nav_UserEmail.setText(LocalStorage.USER.getEmail());
    }

    private void setBottomNavigation() {
        new AppBarConfiguration.Builder(R.id.home_Nav_Chats, R.id.home_Nav_Patients).build();
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()){
                case R.id.home_Nav_Chats:
                    toolbar_title.setText(AppExtensions.getString(R.string.title_chats));
                    break;

                case R.id.home_Nav_Patients:
                    toolbar_title.setText(AppExtensions.getString(R.string.title_Patients));
                    break;
            }
        });

        navView.findViewById(R.id.home_Nav_More).setOnClickListener(view -> {
            if (drawer_Layout.isDrawerOpen(GravityCompat.END)) drawer_Layout.closeDrawer(GravityCompat.END);
            else drawer_Layout.openDrawer(GravityCompat.END);
        });
    }

    private void setDrawer() {
        drawer_Layout.setScrimColor(Color.TRANSPARENT);
        drawer_Layout.setDrawerElevation(0);
        drawer_Layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                float width = navigation_Layout.getWidth() * slideOffset;
                content_Layout.setX(width * -1);
                content_Layout.invalidate();
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {}

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        nav_Profile.setOnClickListener(view -> DoctorProfileFragment.show(LocalStorage.USER)
                .setOnProfileUpdateListener(isUpdated -> {
                    if (isUpdated) setProfileInfo();
                })
        );
        nav_SignOut.setOnClickListener(v -> {
            loading = LoadingFragment.show();
            HashMap<String, Object> activeStatus = new HashMap<>();
            activeStatus.put(Status.STATUS, false);
            activeStatus.put(Status.DATE, new Date());

            new FirebaseHelper().setDocumentData(FirebaseFirestore.getInstance()
                    .collection(FirebaseHelper.USERS_TABLE)
                    .document(LocalStorage.USER.getId())
                    .update(User.ACTIVE, activeStatus), new FirebaseHelper.OnFirebaseUpdateListener() {
                @Override
                public void onSuccess() {
                    AppExtensions.dismissLoading(loading);

                    FirebaseAuth.getInstance().signOut();
                    LocalStorage.setUserInfo(null, true);

                    Intent intent = new Intent(DoctorHomeActivity.this, SignInActivity.class);
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
        View view = LayoutInflater.from(DoctorHomeActivity.this).inflate(R.layout.layout_notification_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DoctorHomeActivity.this);
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