package com.telemedicine.matrika.model.user;

import java.io.Serializable;

public class Role implements Serializable {

    private String  id;
    private String  title;
    private Boolean enabled;

    public Role(String id, String title, Boolean enabled) {
        this.id = id;
        this.title = title;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
