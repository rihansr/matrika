package com.telemedicine.matrika.helper;

import android.content.Context;
import com.telemedicine.matrika.base.AppController;
import com.telemedicine.matrika.util.enums.Role;

public class PreferenceManager {

    private final Context   context;

    private final           String USER_MODE_SP_NAME = "userMode";
    private final           String USER_MODE_SP_KEY = "userModeKey";
    private final           String SIGN_IN_SP_NAME = "signInData";
    public static final     String USER_INFO_SP_KEY = "userInfo";
    public static final     String USER_EMAIL_SP_KEY = "userEmailKey";
    public static final     String USER_PASSWORD_SP_KEY = "userPasswordKey";
    public static final     String USER_REMEMBER_SP_KEY = "userRememberKey";

    private final           String FORBID_NOTIFICATION_PERMISSION_SP_NAME = "isNotificationPermissionForbid";
    private final           String FORBID_NOTIFICATION_PERMISSION_SP_KEY = "forbidKey";

    private final           String APP_RUNNING_SP_NAME = "isAppRunning";
    private final           String APP_RUNNING_SP_KEY = "isAppRunningKey";

    public PreferenceManager(Context context) {
        this.context = context;
    }

    public PreferenceManager() {
        this.context = AppController.getContext();
    }

    public void setUserMode(Role role){
        context.getSharedPreferences(USER_MODE_SP_NAME, Context.MODE_PRIVATE).edit().putInt(USER_MODE_SP_KEY, role.getAction()).apply();
    }

    public Role getUserMode(){
        int mode = context.getSharedPreferences(USER_MODE_SP_NAME, Context.MODE_PRIVATE).getInt(USER_MODE_SP_KEY,1);
        switch (mode){
            default:
            case 0: return Role.ADMIN;
            case 1: return Role.PATIENT;
            case 2: return Role.DOCTOR;
        }
    }

    public void setSignInData(String key, String value){
        context.getSharedPreferences(SIGN_IN_SP_NAME, Context.MODE_PRIVATE).edit().putString(key, value).apply();
    }

    public void setSignInData(String key, boolean state){
        context.getSharedPreferences(SIGN_IN_SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(key, state).apply();
    }

    public String getSignInData(String key){
        return context.getSharedPreferences(SIGN_IN_SP_NAME, Context.MODE_PRIVATE).getString(key,null);
    }

    public boolean isUserCheckedRemember(){
        return context.getSharedPreferences(SIGN_IN_SP_NAME, Context.MODE_PRIVATE).getBoolean(USER_REMEMBER_SP_KEY,false);
    }

    public void setForbidNotificationPermission(boolean isForbidded){
        context.getSharedPreferences(FORBID_NOTIFICATION_PERMISSION_SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(FORBID_NOTIFICATION_PERMISSION_SP_KEY, isForbidded).apply();
    }

    public boolean isNotificationPermissionForbidded(){
        return context.getSharedPreferences(FORBID_NOTIFICATION_PERMISSION_SP_NAME, Context.MODE_PRIVATE).getBoolean(FORBID_NOTIFICATION_PERMISSION_SP_KEY,false);
    }

    public void setAppRunning(boolean isRunning){
        context.getSharedPreferences(APP_RUNNING_SP_NAME, Context.MODE_PRIVATE).edit().putBoolean(APP_RUNNING_SP_KEY, isRunning).apply();
    }

    public boolean isAppRunning(){
        return context.getSharedPreferences(APP_RUNNING_SP_NAME, Context.MODE_PRIVATE).getBoolean(APP_RUNNING_SP_KEY,false);
    }
}
