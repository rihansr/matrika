package com.telemedicine.matrika.util;

import com.telemedicine.matrika.util.enums.Role;

public class Constants {

    /**
     * Permission Request
     **/
    public static final int     GPS_PERMISSION_CODE = 1001;
    public static final int     CAMERA_REQUEST_CODE = 1101;
    public static final int     GALLERY_REQUEST_CODE = 1110;

    /**
     * GPS
     **/
    public static final int     DISPLACEMENT = 10;
    public static final int     GPS_UPDATE_INTERVAL = 10000;
    public static final int     GPS_FASTEST_INTERVAL = 5000;

    /** Data Min Max Value **/
    public static final double  TEMPERATURE_MIN_VALUE = 97;
    public static final double  TEMPERATURE_MAX_VALUE = 99.5;
    public static final long    ECG_MIN_VALUE = 120;
    public static final long    ECG_MAX_VALUE = 200;
    public static final long    PULSE_MIN_VALUE = 60;
    public static final long    PULSE_MAX_VALUE = 90;
    public static final long    SPO2_NORMAL_VALUE = 94;

    /** Timer **/
    public static final long    SPLASH_TIME_OUT = 2000;
    public static final long    DATA_RELOAD_DELAY = 10000; /*10 seconds*/

    /**
     * Other
     **/
    public static final String  TAG = "Hell";
    public static Role          roleMode = Role.PATIENT;
    public static boolean       IS_INTERNET_CONNECTED = false;
    public static final String  PHONE_PATTERN = "^\\+?(88)?0?1[1356789][0-9]{8}\\b$";
    public static final String  COUNTRY_CODE = "+88";
    public static boolean       IS_SWAPPING = false;

    /**
     * Intent key
     **/
    public static final String SUCCESS_KEY = "Success_Key";
    public static final String PHOTO_BUNDLE_KEY = "photoLinkKey";
    public static final String ROLE_BUNDLE_KEY = "roleBundleKey";
    public static final String SPECIALTY_BUNDLE_KEY = "specialtyBundleKey";
    public static final String CHAT_GROUP_BUNDLE_KEY = "groupBundleKey";
    public static final String MESSAGES_BUNDLE_KEY = "messagesBundleKey";
    public static final String FILE_BUNDLE_KEY = "fileBundleKey";
    public static final String SHARE_FILE_BUNDLE_KEY = "shareFileBundleKey";
    public static final String USER_BUNDLE_KEY = "userBundleKey";
    public static final String REPORT_TYPE_BUNDLE_KEY = "reportTypeKey";
    public static final String UID_BUNDLE_KEY = "uIDBundleKey";
    public static final String TOKEN_LISTENER_KEY = "tokenListenerKey";
    public static final String TOKEN_INTENT_KEY = "tokenIntentKey";
}
