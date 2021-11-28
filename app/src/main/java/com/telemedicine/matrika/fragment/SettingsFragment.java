package com.telemedicine.matrika.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.helper.PermissionManager;
import com.telemedicine.matrika.util.LocalStorage;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.wiget.CircleImageView;

@SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
public class SettingsFragment extends DialogFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();
    private Context             context;

    /**
     * Toolbar
     **/
    private AppCompatImageView  toolbar_Back_Button;
    private AppCompatTextView   toolbar_title;
    private AppCompatImageView  toolbar_Right_Button;

    /**
     * Profile
     * */
    private RelativeLayout      user_PhotoHolder;
    private CircleImageView     profile_Photo;
    private AppCompatTextView   user_Name;
    private AppCompatTextView   user_Email;

    /**
     * Permissions
     **/
    private SwitchCompat        locationAccess_Switch;
    private SwitchCompat        callAccess_Switch;
    private SwitchCompat        cameraAccess_Switch;
    private SwitchCompat        galleryAccess_Switch;

    /**
     * Preferences
     **/
    private SwitchCompat        allowNotification_Switch;

    /**
     * Other
     **/
    private boolean                     isSettingsUpdated = false;
    private OnSettingsUpdateListener    mOnSettingsUpdateListener;

    public static SettingsFragment show(){
        SettingsFragment fragment = new SettingsFragment();
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
        setPermissions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_layout_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        AppExtensions.fullScreenDialog(getDialog(), false);
        super.onViewCreated(view, savedInstanceState);

        idSetup(view);

        init();
    }

    private void idSetup(View rootView) {
        toolbar_title = rootView.findViewById(R.id.toolbar_Title_Tv);
        toolbar_Back_Button = rootView.findViewById(R.id.toolbar_Left_Button);
        toolbar_Right_Button = rootView.findViewById(R.id.toolbar_Right_Button);

        user_PhotoHolder = rootView.findViewById(R.id.settings_PhotoHolder_Layout);
        profile_Photo = rootView.findViewById(R.id.settings_Photo_Iv);
        user_Name = rootView.findViewById(R.id.settings_Name_Tv);
        user_Email = rootView.findViewById(R.id.settings_Email_Tv);

        locationAccess_Switch = rootView.findViewById(R.id.settings_LocationAccess_Switch);
        callAccess_Switch = rootView.findViewById(R.id.settings_CallAccess_Switch);
        cameraAccess_Switch = rootView.findViewById(R.id.settings_CameraAccess_Switch);
        galleryAccess_Switch = rootView.findViewById(R.id.settings_GalleryAccess_Switch);

        allowNotification_Switch = rootView.findViewById(R.id.settings_AllowNotification_Switch);
    }

    private void init(){
        toolbar_title.setText(AppExtensions.getString(R.string.settings));
        toolbar_Back_Button.setOnClickListener(v -> dismiss());
        toolbar_Right_Button.setVisibility(View.GONE);

        setProfileInfo();

        user_PhotoHolder.setOnClickListener(v -> {
                    if (LocalStorage.USER.getRole().equals(Role.DOCTOR.getId()))
                        DoctorProfileFragment.show(LocalStorage.USER).setOnProfileUpdateListener(isUpdated -> {
                            if (!isSettingsUpdated) isSettingsUpdated = isUpdated;
                        });
                    else
                        PatientProfileFragment.show(LocalStorage.USER).setOnProfileUpdateListener(isUpdated -> {
                            if (!isSettingsUpdated) isSettingsUpdated = isUpdated;
                        });
                }
        );

        for (SwitchCompat switchCompat : new SwitchCompat[]{locationAccess_Switch, callAccess_Switch, cameraAccess_Switch, galleryAccess_Switch}){
            switchCompat.setOnClickListener(v -> new PermissionManager().goToPermissionSetting());
        }

        allowNotification_Switch.setOnClickListener(v -> new PermissionManager().goToNotificationPermissionSetting());
    }

    private void setProfileInfo() {
        if(LocalStorage.USER == null) return;
        AppExtensions.loadPhoto(profile_Photo, LocalStorage.USER.getPhoto(), R.dimen.icon_Size_XX_Large, R.drawable.ic_avatar);
        user_Name.setText(LocalStorage.USER.getName());
        user_Email.setText(LocalStorage.USER.getEmail());
    }

    private void setPermissions() {
        allowNotification_Switch.setChecked(NotificationManagerCompat.from(context).areNotificationsEnabled());
        locationAccess_Switch.setChecked(new PermissionManager(PermissionManager.Permission.LOCATION, false).isGranted());
        callAccess_Switch.setChecked(new PermissionManager(PermissionManager.Permission.PHONE, false).isGranted());
        cameraAccess_Switch.setChecked(new PermissionManager(PermissionManager.Permission.CAMERA, false).isGranted());
        galleryAccess_Switch.setChecked(new PermissionManager(PermissionManager.Permission.GALLERY, false).isGranted());
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnSettingsUpdateListener != null) mOnSettingsUpdateListener.onSettingsUpdate(isSettingsUpdated);
    }

    public void setOnSettingsUpdateListener(OnSettingsUpdateListener mOnSettingsUpdateListener) {
        this.mOnSettingsUpdateListener = mOnSettingsUpdateListener;
    }

    public interface OnSettingsUpdateListener {
        void onSettingsUpdate(boolean isUpdated);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
