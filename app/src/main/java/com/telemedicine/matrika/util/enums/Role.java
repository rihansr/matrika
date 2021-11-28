package com.telemedicine.matrika.util.enums;

public enum Role {

    ADMIN("TzEOabBG1fB9f2ISZteW", "Admin", 0),
    PATIENT("M5WyRNt5HD7CAdiR53Vq", "Patient", 1),
    DOCTOR("2NF0MYG8VQoElJbsUMX8", "Doctor", 2),
    BOTH(new String[]{"M5WyRNt5HD7CAdiR53Vq", "2NF0MYG8VQoElJbsUMX8"}, "Both", 3),
    ALL(new String[]{"TzEOabBG1fB9f2ISZteW", "M5WyRNt5HD7CAdiR53Vq", "2NF0MYG8VQoElJbsUMX8"}, "All", 4);

    private String   id;
    private String[] ids;
    private String   title;
    private int      action;

    Role(String id, String title, int action) {
        this.id = id;
        this.title = title;
        this.action = action;
    }

    Role(String[] ids, String title, int action) {
        this.ids = ids;
        this.title = title;
        this.action = action;
    }

    public String[] getIds() {
        return ids;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getAction() {
        return action;
    }
}
