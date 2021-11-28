package com.telemedicine.matrika.model.other;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class Report implements Serializable {

    private String  id;
    private String  role;
    private String  reportedBy;
    private String  reportedTo;
    private String  report;
    @ServerTimestamp
    private Date    reportedAt;

    public Report() {}

    public Report(String id, String role, String reportedBy, String reportedTo, String report, Date reportedAt) {
        this.id = id;
        this.role = role;
        this.reportedBy = reportedBy;
        this.reportedTo = reportedTo;
        this.report = report;
        this.reportedAt = reportedAt;
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

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportedTo() {
        return reportedTo;
    }

    public void setReportedTo(String reportedTo) {
        this.reportedTo = reportedTo;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Date getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(Date reportedAt) {
        this.reportedAt = reportedAt;
    }
}
