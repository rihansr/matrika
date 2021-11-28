package com.telemedicine.matrika.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.telemedicine.matrika.R;
import com.telemedicine.matrika.base.AppBaseActivity;
import com.telemedicine.matrika.util.Constants;
import com.telemedicine.matrika.util.enums.Role;
import com.telemedicine.matrika.util.extensions.AppExtensions;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppExtensions.fullScreenActivity(getWindow(), true);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(this::launchNewActivity, Constants.SPLASH_TIME_OUT);
    }

    private void launchNewActivity() {
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(SplashActivity.this, Constants.roleMode == Role.PATIENT
                    ?
                    PatientHomeActivity.class
                    :
                    DoctorHomeActivity.class
            );
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
