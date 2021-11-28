package com.telemedicine.matrika.util.enums;

public enum Photo {

    PROFILE("Profile", "profile", 0),
    NID("Nid", "nid", 1),
    REPORT("Report", "report", 2),
    OTHER("Other", "other", 3);

    private String   title;
    private String   path;
    private int      action;

    Photo(String title, String path, int action) {
        this.title = title;
        this.path = path;
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public int getAction() {
        return action;
    }
}
