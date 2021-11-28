package com.telemedicine.matrika.model.other;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Follower implements Serializable {

    public static String BY = "followedBy";
    public static String TO = "followedTo";

    private String  id;
    private String  role;
    private String  followedBy;
    private String  followedTo;
    @ServerTimestamp
    private Date    followedAt;

    public Follower() {}

    public Follower(String id, String role, String followedBy, String followedTo, Date followedAt) {
        this.id = id;
        this.role = role;
        this.followedBy = followedBy;
        this.followedTo = followedTo;
        this.followedAt = followedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public String getFollowedTo() {
        return followedTo;
    }

    public void setFollowedTo(String followedTo) {
        this.followedTo = followedTo;
    }

    public Date getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(Date followedAt) {
        this.followedAt = followedAt;
    }
}
