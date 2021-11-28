package com.telemedicine.matrika.util.enums;

import com.telemedicine.matrika.helper.FirebaseHelper;

public enum Report {

    MEDICAL_REPORT("Medical Report", FirebaseHelper.USER_REPORTS_TABLE, 0),
    DEVICE_REPORT("Device Report", FirebaseHelper.DEVICE_REPORTS_TABLE, 1);

    private final String  title;
    private final String  table;
    private final int     action;

    Report(String title, String table, int action) {
        this.title = title;
        this.table = table;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public String getTable() {
        return table;
    }

    public int getAction() {
        return action;
    }
}
