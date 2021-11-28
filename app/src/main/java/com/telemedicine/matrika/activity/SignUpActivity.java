package com.telemedicine.matrika.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.FrameLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.fragment.SignUpDoctorFragment;
import com.telemedicine.matrika.fragment.SignUpPatientFragment;
import com.telemedicine.matrika.util.CircularReveal;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.helper.PreferenceManager;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.telemedicine.matrika.util.interfaces.OnBackPressListener;

public class SignUpActivity extends AppBaseActivity {

    private FrameLayout             userMode_Button;
    private AppCompatImageView      userMode_Icon;
    private AppCompatImageView      userType_Icon;
    private final PreferenceManager pm = new PreferenceManager();
    private CircularReveal          circularReveal;
    private OnBackPressListener     mOnBackPressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppExtensions.fullScreenActivity(getWindow(), false);
        setContentView(R.layout.activity_sign_up);

        if(Constants.IS_SWAPPING){
            circularReveal = new CircularReveal(findViewById(R.id.signUp_Root_Layout));
            circularReveal.onActivityCreate(getIntent());
        }

        initId();

        init();
    }

    private void initId(){
        userMode_Button = findViewById(R.id.signUp_UserMode_Button);
        userMode_Icon = findViewById(R.id.signUp_UserMode_Iv);
        userType_Icon = findViewById(R.id.signUp_UserType_Iv);
    }

    private void init() {
        findViewById(R.id.signUp_Back_Button).setOnClickListener(view -> {
            if(mOnBackPressListener != null) mOnBackPressListener.goBack();
            else {
                if(circularReveal != null) circularReveal.unRevealActivity(this);
                Constants.IS_SWAPPING = false;
                Intent intent = new Intent(this, SignInActivity.class);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, new Pair<>(userType_Icon, "userTypeIcon"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent, options.toBundle());
            }
        });

        userMode_Button.setOnClickListener(view -> {
            userMode_Button.setEnabled(false);
            pm.setUserMode(Constants.roleMode == Role.PATIENT ? Role.DOCTOR : Role.PATIENT);

            Intent intent = new Intent(this, SignUpActivity.class);
            Constants.IS_SWAPPING = true;
            CircularReveal.Builder builder = new CircularReveal.Builder(this, userMode_Icon, intent, 750);
            builder.setRevealColor(AppExtensions.getColor(R.color.colorReveal));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            CircularReveal.presentActivity(builder);
        });

        switch (Constants.roleMode){
            case PATIENT:
                userMode_Icon.setImageDrawable(AppExtensions.getDrawable(R.drawable.ic_doctor_mini));
                userType_Icon.setImageDrawable(AppExtensions.getDrawable(R.drawable.ic_patient));
                swapFragment(Role.PATIENT);
                break;

            case DOCTOR:
                userMode_Icon.setImageDrawable(AppExtensions.getDrawable(R.drawable.ic_patient_mini));
                userType_Icon.setImageDrawable(AppExtensions.getDrawable(R.drawable.ic_doctor));
                swapFragment(Role.DOCTOR);
                break;
        }
    }

    private void swapFragment(Role role) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (role){
            case PATIENT:
                fragmentTransaction.replace(R.id.signUp_Fragment_Container, new SignUpPatientFragment());
                break;

            case DOCTOR:
                fragmentTransaction.replace(R.id.signUp_Fragment_Container, new SignUpDoctorFragment());
                break;
        }

        fragmentTransaction.commit();
    }

    public void setOnBackPressListener(OnBackPressListener onBackPressListener) {
        this.mOnBackPressListener = onBackPressListener;
    }

    @Override
    public void onBackPressed() {
        if(mOnBackPressListener != null) mOnBackPressListener.goBack();
        else {
            if(circularReveal != null) circularReveal.unRevealActivity(this);
            Constants.IS_SWAPPING = false;
            Intent intent = new Intent(this, SignInActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignUpActivity.this, new Pair<>(userType_Icon, "userTypeIcon"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent, options.toBundle());
        }
    }
}
