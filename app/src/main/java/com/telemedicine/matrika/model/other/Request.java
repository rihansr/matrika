package com.telemedicine.matrika.model.other;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class Request implements Serializable {

    /**
     * Static Fields
     **/
    public static String BY = "requestedBy";
    public static String TO = "requestedTo";
    public static String SENT_AT = "requestSentAt";
    public static String REJECTED_STATUS = "rejected.status";
    public static String REJECTED = "rejected";
    public static String ACCEPTED = "accepted";

    /**
     * Model Fields
     **/
    private String      id;
    private String      role;
    private String      requestedBy;
    private String      requestedTo;
    private Status      accepted;
    private Status      rejected;
    @ServerTimestamp
    private Date        requestSentAt;

    public Request() {}

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

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getRequestedTo() {
        return requestedTo;
    }

    public void setRequestedTo(String requestedTo) {
        this.requestedTo = requestedTo;
    }

    public Status getAccepted() {
        return accepted;
    }

    public void setAccepted(Status accepted) {
        this.accepted = accepted;
    }

    public Status getRejected() {
        return rejected;
    }

    public void setRejected(Status rejected) {
        this.rejected = rejected;
    }

    public Date getRequestSentAt() {
        return requestSentAt;
    }

    public void setRequestSentAt(Date requestSentAt) {
        this.requestSentAt = requestSentAt;
    }
}
